package com.swarn.androidcomponents.di.dagger

import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Swarn Singh.
 */

// @Inject lets Dagger know how to create instances of this object
@Singleton
class UserRepository @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
) {  }