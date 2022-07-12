package com.mobilekosmos.android.clubs.data.repository

import com.mobilekosmos.android.clubs.data.model.ClubEntity
import com.mobilekosmos.android.clubs.data.network.ClubsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

object ClubsRepositoryFlow {
    val clubsFlow: Flow<Response<List<ClubEntity>>> = flow {
        val latestNews = ClubsApi.RETROFIT_SERVICE.getClubs()
        emit(latestNews) // Emits the result of the request to the flow.
    }
}