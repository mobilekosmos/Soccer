package com.mobilekosmos.android.clubs.ui.model

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilekosmos.android.clubs.data.model.ClubEntity
import com.mobilekosmos.android.clubs.data.repository.ClubsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: ideally inject the repository for testing purposes.
//class MyViewModel(private val repository: ClubsRepository) : ViewModel() {
class ClubsViewModel : ViewModel() {

    enum class SortingMode {
        SORT_BY_NAME_ASCENDING, SORT_BY_VALUE_DESCENDING
    }

    private var sortingMode = SortingMode.SORT_BY_NAME_ASCENDING

    // Uses prefix "_" as it's the naming convention used in backing properties.
    // We use LazyThreadSafetyMode.NONE to avoid using thread synchronization because we are using this only from the MainThread.
    private val _clubs: MutableLiveData<List<ClubEntity>> by lazy(LazyThreadSafetyMode.NONE) {
        // by lazy normally expects a value but since we load async we create a mutable liveData with no value assigned to it.
        // "also" returns the empty MutableLiveData and at the same time runs a lambda.
        // The real value is then set in the called function.
        MutableLiveData<List<ClubEntity>>().also {
            fetchClubListFromRepository()
        }
    }
    /* Just for reference, above is same as following JAVA code:
        (from https://developer.android.com/topic/libraries/architecture/viewmodel#java):

        private MutableLiveData<List<ClubEntity>> clubs;
        public LiveData<List<ClubEntity>> getClubs() {
            if (clubs == null) {
                clubs = new MutableLiveData<List<ClubEntity>>();
                fetchClubListFromRepository();
            }
            return clubs;
        }
     */

    // LiveData.value is still @Nullable so you must always use ?operator when accessing.
    // https://proandroiddev.com/improving-livedata-nullability-in-kotlin-45751a2bafb7
    val clubs: LiveData<List<ClubEntity>>
        get() = _clubs

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    // We use LazyThreadSafetyMode.NONE to avoid using thread synchronization because we are using this only from the MainThread.
    private val _eventNetworkError:MutableLiveData<Boolean> by lazy(LazyThreadSafetyMode.NONE) {
        MutableLiveData<Boolean>()
    }

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    // We use SharedFlow because it doesn't emit instantly after starting observing and then it just
    // emits when the value changed, so we are using this for the purpose of "One Shot Action". We also
    // use this instead of MutableLiveData because with the latest we would need a separate variable
    // and handling for storing if we already showed the sorted event or not.
    // MutableStateFlow on the other hand would emit instantly that's why we don't use it.
    // We use LazyThreadSafetyMode.NONE to avoid using thread synchronization because we are using this only from the MainThread.
    // TODO: Need to test LazyThreadSafetyMode.NONE being used from the tests, not sure about the Threading behavior in that case.
    private val _eventSorted: MutableSharedFlow<SortingMode> by lazy(LazyThreadSafetyMode.NONE) {
        MutableSharedFlow()
    }
    val eventSorted = _eventSorted.asSharedFlow()

    /**
     * Fetches data using Retrofit which is configured to cache the response for a short period of time.
     * (Good solution for simple requests and responses, infrequent network calls, or small datasets.)
     */
    private fun fetchClubListFromRepository() {
        _isNetworkErrorShown.value = false
        _eventNetworkError.value = false
        viewModelScope.launch {
            try {
                // Here you can not call _clubs.value = clubsRepository.getAllClubs()
                // because the lazy initialization did not finish yet and you would actually recall this function again.

                // To avoid saving the resulting array in a variable we work with the retrofit response instead.
                // TODO: Ideally we shouldn't know anything about Retrofit here, but just for simplicity we do. There is a todo in the retrofit api class to address this.
                val apiResponse = ClubsRepository.getAllClubs()
                val clubsList = apiResponse.body()
                if (apiResponse.isSuccessful && clubsList != null) {
//                    _clubs.value = getListSorted(SortingMode.SORT_BY_NAME_ASCENDING, body)
                    _clubs.value = clubsList.applyMainSafeSort(SortingMode.SORT_BY_NAME_ASCENDING)
                } else {
                    if (clubs.value.isNullOrEmpty()) {
                        _eventNetworkError.value = true
                    }
                }
                // viewModelScope uses the MainThread Dispatcher by default so we don't need to use "withContext(Dispatchers.Main)"
                // to access the UI.
            } catch (ex: Exception) {
                // TODO: extend/improve error handling: check internet connection, listen to connection state changes and automatically retry, etc.
                if (clubs.value.isNullOrEmpty()) {
                    _eventNetworkError.value = true
                }
            }
        }
    }

    fun retryLoadingData() {
        fetchClubListFromRepository()
    }

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    fun toggleSorting() {
        clubs.value?.let {
            val newSortingMode = when (sortingMode) {
                SortingMode.SORT_BY_NAME_ASCENDING -> SortingMode.SORT_BY_VALUE_DESCENDING
                SortingMode.SORT_BY_VALUE_DESCENDING -> SortingMode.SORT_BY_NAME_ASCENDING
            }
            sortClubsList(newSortingMode)
            sortingMode = newSortingMode
            viewModelScope.launch {
                _eventSorted.emit(newSortingMode)
            }
        }
    }

    // In this case we do sorting logic in the ViewModel because the repository should only manage
    // the single source of truth but not modifications needed by UI.
    // ------------------------------------------------------------------------------------------------
    private fun sortClubsList(sortingMode: SortingMode) {
//        _clubs.value?.let {
//            _clubs.value = getListSorted(sortingMode, it)
//        }
        _clubs.value?.let {
            viewModelScope.launch {
                _clubs.value = it.applyMainSafeSort(sortingMode)
            }
        }
    }

//    private fun getListSorted(sortingMode: SortingMode, clubsList:List<ClubEntity>) : List<ClubEntity> {
//        return when (sortingMode) {
//            SortingMode.SORT_BY_NAME_ASCENDING -> {
//                clubsList.sortedBy { it.name }
//            }
//            SortingMode.SORT_BY_VALUE_DESCENDING -> {
//                // TODONT: list manipulation chaining is mostly slower than own implementation.
//                clubsList.sortedBy { it.value }.reversed()
//            }
//        }
//    }

    private fun List<ClubEntity>.applySort(sortingMode: SortingMode): List<ClubEntity> {
        return when (sortingMode) {
            SortingMode.SORT_BY_NAME_ASCENDING -> {
                this@applySort.sortedBy { it.name }
            }
            SortingMode.SORT_BY_VALUE_DESCENDING -> {
                // TODO: list manipulation chaining is mostly slower than own implementation.
                this@applySort.sortedBy { it.value }.reversed()
            }
        }
    }

    // I have this from the Android Tutorial "Advanced Coroutines on Android", but you won't find
    // "AnyThread" being used a lot in other projects, I think it just means that the code runs in the
    // background.
    @AnyThread
    suspend fun List<ClubEntity>.applyMainSafeSort(sortingMode: SortingMode) =
        withContext(Dispatchers.Default) {
            this@applyMainSafeSort.applySort(sortingMode)
        }
    // ------------------------------------------------------------------------------------------------
}