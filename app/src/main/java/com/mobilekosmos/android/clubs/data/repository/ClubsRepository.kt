package com.mobilekosmos.android.clubs.data.repository

import com.mobilekosmos.android.clubs.data.model.ClubEntity
import com.mobilekosmos.android.clubs.data.network.ClubsApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

// Sample if using blocking connection. Retrofit runs safely in the background so not needed for it.
//// TODO: ideally this should be a singleton.
//class ClubsRepository(
//    // This dependency injection pattern makes testing easier as you can replace those dispatchers in unit and instrumentation tests with a test dispatcher to make your tests more deterministic.
//    // https://developer.android.com/kotlin/coroutines/coroutines-best-practices
//    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
//) {
//    // We rely on Retrofit's httpClient disk cache instead of Room for this simple project.
//    suspend fun getAllClubs(): List<ClubEntity> = withContext(defaultDispatcher) {
//        ClubsApi.RETROFIT_SERVICE.getClubs()
//    }
//}

object ClubsRepository {
    // We rely on Retrofit's httpClient disk cache instead of Room for this simple project.
    // "No need to change context to Dispatchers.IO as Retrofit handles that automatically."
    suspend fun getAllClubs(): Response<List<ClubEntity>> {
        return ClubsApi.RETROFIT_SERVICE.getClubs()
    }
}