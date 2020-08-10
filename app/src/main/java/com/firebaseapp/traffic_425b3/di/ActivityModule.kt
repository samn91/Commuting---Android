package com.firebaseapp.traffic_425b3.di

import com.firebaseapp.traffic_425b3.timetable.FilterAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @StationAdapter
    @Provides
    fun provideStationAdapter() =
        FilterAdapter()

    @StopsAdapter
    @Provides
    fun provideStopsAdapter() =
        FilterAdapter()

    @BusAdapter
    @Provides
    fun provideBusAdapter() =
        FilterAdapter()
}