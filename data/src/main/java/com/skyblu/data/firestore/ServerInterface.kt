package com.skyblu.data.firestore

import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.skyblu.models.jump.Skydive
import com.skyblu.models.jump.SkydiveDataPoint
import com.skyblu.models.jump.SkydiveWithDatapoints
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception

interface ServerInterface {
    fun deleteSkydive(skydiveID: String, applicationContext: Context)
    fun uploadSkydive(skydive : Skydive, applicationContext : Context)
    fun uploadSkydiveDatapoint(datapoint : SkydiveDataPoint, applicationContext: Context)
    fun uploadSkydiveWithDatapoints(skydiveWithDataPoints : SkydiveWithDatapoints, applicationContext: Context)
    suspend fun getSkydivesLocally(page : Int, pageSize : Int ) : Result<List<Skydive>>
    suspend fun getSkydivesFromServer(page : DocumentSnapshot?, pageSize : Int ) : Result<QuerySnapshot>
    suspend fun getSkydiveFromServer(skydiveID : String) : Result<DocumentSnapshot?>
    suspend fun getDatapointsFromServer(skydiveID : String) : Result<QuerySnapshot>
    suspend fun getSkydiverFromServer(id : String) : Result<DocumentSnapshot?>
}