package com.skyblu.models.jump

import android.util.Log
import junit.framework.TestCase
import org.junit.AfterClass
import org.junit.BeforeClass
import timber.log.Timber

class SkydiveDataPointKtTest : TestCase() {


    @BeforeClass
    fun plant(){
        Timber.plant(Timber.DebugTree())
    }



    @AfterClass
    fun uproot(){
        Timber.uproot(Timber.DebugTree())
    }
}