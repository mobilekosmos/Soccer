package com.mobilekosmos.android.clubs.data.repository

import com.mobilekosmos.android.clubs.data.model.ClubEntity
import com.mobilekosmos.android.clubs.data.network.ClubsApiHilt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class ClubsRepositoryFlowHilt @Inject constructor(private val service : ClubsApiHilt) {
    val clubsFlow: Flow<Response<List<ClubEntity>>> = flow {
        val latestNews = service.RETROFIT_SERVICE.getClubs()
        emit(latestNews) // Emits the result of the request to the flow.
    }
}