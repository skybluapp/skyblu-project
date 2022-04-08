package com.skyblu.data.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys{
    val FREEFALL_CERTAINTY_KEY = intPreferencesKey("freefall_certainty_key")
    val LAST_JUMP_NUMBER = intPreferencesKey("last_jump_number")
    val LAST_EQUIPMENT = stringPreferencesKey("last_equipment")
    val LAST_AIRCRAFT = stringPreferencesKey("last_aircraft")
    val LAST_DROPZONE = stringPreferencesKey("last_dropzone")
}

