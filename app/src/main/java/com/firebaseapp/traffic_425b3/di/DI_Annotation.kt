package com.firebaseapp.traffic_425b3.di

import javax.inject.Qualifier

/**
 * Created by Samer on 10/08/2020 10:25.
 */

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StationAdapter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StopsAdapter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BusAdapter
