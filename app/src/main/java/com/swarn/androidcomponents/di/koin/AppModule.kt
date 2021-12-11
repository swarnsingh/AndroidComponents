package com.swarn.androidcomponents.di.koin

import com.swarn.androidcomponents.report.CrashlyticsErrorReporter
import com.swarn.androidcomponents.report.ErrorReporter
import com.swarn.androidcomponents.report.ErrorReportingTree
import com.swarn.androidcomponents.report.NewRelicErrorReporter
import org.koin.dsl.module

/**
 * @author Swarn Singh.
 */

val appModule = module {
    single<ErrorReporter> {
        NewRelicErrorReporter(
            context = get(),
            nextReporter = CrashlyticsErrorReporter()
        )
    }

    single { ErrorReportingTree.newInstance(errorReporter = get()) }
}