package com.skyblu.skybluapp

import android.app.Application
import timber.log.Timber

class SkybluApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}