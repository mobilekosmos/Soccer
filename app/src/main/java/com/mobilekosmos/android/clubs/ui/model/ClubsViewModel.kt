package com.mobilekosmos.android.clubs.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilekosmos.android.clubs.data.model.ClubEntity
import com.mobilekosmos.android.clubs.data.repository.ClubsRepository
import kotlinx.coroutines.launch

//class MyViewModel(private val repository: ClubsRepository) : ViewModel() {
class ClubsViewModel() : ViewModel() {

    enum class SortingMode {
        SORT_BY_NAME_ASCENDING, SORT_BY_VALUE_DESCENDING
    }

    var isSortedToastShown: Boolean = true

    private val clubsRepository = ClubsRepository()

    // Uses prefix "_" as it's the naming convention used in backing properties.
    private val _clubs: MutableLiveData<List<ClubEntity>> by lazy {
        MutableLiveData<List<ClubEntity>>().also {
            fetchClubListFromRepository()
        }
    }

    // LiveData.value is still @Nullable so you must always use ?operator when accessing.
    // https://proandroiddev.com/improving-livedata-nullability-in-kotlin-45751a2bafb7
    val clubs: LiveData<List<ClubEntity>>
        get() = _clubs

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private val _eventNetworkError:MutableLiveData<Boolean> by lazy {
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

    /**
     * Fetches data using Retrofit which is configured to cache the response for a short period of time.
     * (Good solution for simple requests and responses, infrequent network calls, or small datasets.)
     */
    private fun fetchClubListFromRepository() {
        _isNetworkErrorShown.value = false
        _eventNetworkError.value = false
        viewModelScope.launch {
            try {
                // TODO: call could look like this:
                //  _clubs.value = clubsRepository.getAllClubs().apply{sortGivenList(SortingMode.SORT_BY_NAME_ASCENDING, this)}
                //  maybe possible with MutableList, are there any cons?

                // Here you cannot call _clubs.value = clubsRepository.getAllClubs()
                var clubsList = clubsRepository.getAllClubs()
                clubsList = getListSorted(SortingMode.SORT_BY_NAME_ASCENDING, clubsList)

                _clubs.value = clubsList

                // viewModelScope uses the MainThread Dispatcher by default so we don't need to use "withContext(Dispatchers.Main)"
                // to access the UI.
            } catch (ex: Exception) {
                // TODO: extend/improve error handling: check internet connection, show UI element
                //  to retry loading or hear for connection state changes.
                if (clubs.value.isNullOrEmpty())
                    _eventNetworkError.value = true
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
            val sortingMode = when (_eventSorted.value) {
                SortingMode.SORT_BY_NAME_ASCENDING -> SortingMode.SORT_BY_VALUE_DESCENDING
                SortingMode.SORT_BY_VALUE_DESCENDING -> SortingMode.SORT_BY_NAME_ASCENDING
                // On first call this will be null and we are already sorted by name so we sort by value.
                null -> SortingMode.SORT_BY_VALUE_DESCENDING
            }
            sortClubsList(sortingMode)
            isSortedToastShown = false
            _eventSorted.value = sortingMode
        }
    }

    // TODO: Maybe use a coroutine for the sorting.
    private fun sortClubsList(sortingMode: SortingMode) {
        _clubs.value?.let {
            _clubs.value = getListSorted(sortingMode, it)
        }
    }

    private fun getListSorted(sortingMode: SortingMode, clubsList:List<ClubEntity>) : List<ClubEntity> {
        return when (sortingMode) {
            SortingMode.SORT_BY_NAME_ASCENDING -> {
                clubsList.sortedBy { it.name }
            }
            SortingMode.SORT_BY_VALUE_DESCENDING -> {
                // TODO: list manipulation chaining is mostly slower than own implementation.
                clubsList.sortedBy { it.value }.reversed()
            }
        }
    }

    private val _eventSorted: MutableLiveData<SortingMode> by lazy {
        MutableLiveData<SortingMode>()
    }
    val eventSorted: LiveData<SortingMode>
        get() = _eventSorted

    fun onSortedEventShown() {
        isSortedToastShown = true
    }
}