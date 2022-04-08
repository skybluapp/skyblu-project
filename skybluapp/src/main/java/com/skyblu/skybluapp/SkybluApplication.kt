package com.skyblu.skybluapp

import android.app.Application
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for Skyblu App
 * Used for Hilt and Timber Logging
 *  * @author Oliver Stocks
 */
@HiltAndroidApp
class SkybluApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}