package com.skyblu.data.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


//const val PREFERENCES = "skyblu_preferences"
//
//val AIRCRAFT_CERTAINTY_KEY = intPreferencesKey("aircraft_certainty_key")
//val AIRCRAFT_MIN_ALTITUDE_KEY = intPreferencesKey("aircraft_min_altitude_key")
//val AIRCRAFT_MIN_EXIT_HEIGHT = intPreferencesKey("aircraft_max_altitude_key")
//
//val CANOPY_MIN_OPEN_HEIGHT = intPreferencesKey("canopy_min_open_height")
//
//val FREEFALL_ALTITUDE_LOSS_KEY = intPreferencesKey("freefall_altitude_loss_key")
//val FREEFALL_SPEED_LOSS_KEY = intPreferencesKey("freefall_speed_loss_key")
val FREEFALL_CERTAINTY_KEY = intPreferencesKey("freefall_certainty_key")

//class KeyValue(
//    key : Preferences.Key<String>, value : String
//){
//    val kkey = stringPreferencesKey(key)
//
//    init {
//        p(FREEFALL_CERTAINTY_KEY)
//    }
//
//}
//
