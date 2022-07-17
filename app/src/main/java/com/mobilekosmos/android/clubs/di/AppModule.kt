package com.mobilekosmos.android.clubs.di

import android.content.Context
import android.net.ConnectivityManager
import com.mobilekosmos.android.clubs.data.network.ClubsApiHilt
import com.mobilekosmos.android.clubs.data.repository.ClubsRepositoryFlowHilt
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    @Singleton
    @Provides
    fun provideRepository(clubsApiHilt: ClubsApiHilt): ClubsRepositoryFlowHilt =
        ClubsRepositoryFlowHilt(clubsApiHilt)

    @Singleton
    @Provides
    fun provideService(@ApplicationContext context: Context): ClubsApiHilt =
        ClubsApiHilt(context, provideConnectivityManager(context))
}