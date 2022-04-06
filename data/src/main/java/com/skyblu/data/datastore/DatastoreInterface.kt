package com.skyblu.data.datastore

interface DatastoreInterface {
    suspend fun readAircraftCertaintyKey() : Int
}