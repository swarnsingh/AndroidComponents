package com.swarn.androidcomponents.di.dagger

import dagger.Component
import javax.inject.Singleton

/**
 * @author Swarn Singh.
 */

// @Component makes Dagger create a graph of dependencies
@Singleton
@Component
interface ApplicationGraph {
    // The return type  of functions inside the component interface is
    // what can be provided from the container
    fun repository(): UserRepository
}