package com.skyblu.data.firestore

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.skyblu.configuration.*
import com.skyblu.models.jump.*
import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import timber.log.Timber

class DeleteSkydiveWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {

        val jumpID = inputData.getString(JumpParams.JUMP_ID) ?: return Result.failure()
        var result: Result? = null
        val startTime = System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection(JUMPS_COLLECTION).document(jumpID).delete()
            .addOnSuccessListener {
                result = Result.success()
            }
            .addOnFailureListener {
                result = Result.retry()
            }
        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                return Result.retry()
            }
            delay(1000)
        }
        return result as Result
    }
}

class UploadSkydiveWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val jumpString = inputData.getString(JumpParams.JUMP) ?: return Result.failure()
        val urlString = inputData.getString(JumpParams.STATIC_MAP_URL) ?: return Result.failure()
        val jump: Jump = Json.decodeFromString<Jump>(jumpString)
        jump.staticMapUrl = urlString
        var result: Result? = null
        val startTime = System.currentTimeMillis()

        firestore.collection(JUMPS_COLLECTION).document(jump.jumpID).set(jump)
            .addOnFailureListener {
                result = Result.retry()
            }
            .addOnSuccessListener {
                result = Result.success()
            }

        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                return Result.retry()
            }
            delay(1000)
        }
        return result as Result
    }
}

class UpdateSkydiveWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val jumpID = inputData.getString(JumpParams.JUMP_ID) ?: return Result.failure()
        val title: String? = inputData.getString(JumpParams.TITLE)
        val equipment: String? = inputData.getString(JumpParams.EQUIPMENT)
        val aircraft: String? = inputData.getString(JumpParams.AIRCRAFT)
        val dropzone: String? = inputData.getString(JumpParams.DROPZONE)
        val jumpNumber: Int = inputData.getInt(JumpParams.JUMP_NUMBER, 10)

        val map = mutableMapOf<String, Any>()
        map[JumpParams.JUMP_NUMBER] = jumpNumber
        if (!title.isNullOrBlank()) {
            map[TITLE_STRING] = title
        }
        if (!equipment.isNullOrBlank()) {
            map[EQUIPMENT_STRING] = equipment
        }
        if (!aircraft.isNullOrBlank()) {
            map[AIRCRAFT_STRING] = aircraft
        }
        if (!dropzone.isNullOrBlank()) {
            map[DROPZONE_STRING] = dropzone
        }

        var result: Result? = null
        val startTime = System.currentTimeMillis()

        firestore.collection(JUMPS_COLLECTION).document(jumpID).update(
            map
        )
            .addOnFailureListener {
                result = Result.retry()
            }
            .addOnSuccessListener {
                result = Result.success()
            }

        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                return Result.retry()
            }
            delay(1000)
        }
        return result as Result
    }
}

class UploadUserWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val userString = inputData.getString(UserParameterNames.USER) ?: return Result.failure()
        val user: User = Json.decodeFromString<User>(userString)
        val url: String? = inputData.getString(UserParameterNames.PHOTO_URL)
        user.photoUrl = url
        var result: Result? = null
        val startTime = System.currentTimeMillis()

        firestore.collection(USERS_COLLECTION).document(user.ID).set(user)
            .addOnFailureListener {
                result = Result.retry()
            }
            .addOnSuccessListener {
                result = Result.success()
            }

        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                return Result.retry()
            }
            delay(1000)
        }
        return result as Result
    }

}

class UploadDatapointWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val jumpDataPointString = inputData.getString(DatapointParams.DATAPOINT) ?: return Result.failure()
        val datapoint: SkydiveDataPoint =
            Json.decodeFromString<SkydiveDataPoint>(jumpDataPointString)
        var result: Result? = null
        val startTime = System.currentTimeMillis()


        firestore.collection(JUMPS_COLLECTION).document(datapoint.jumpID)
            .collection(DATAPOINTS_COLLECTION)
            .add(datapoint)
            .addOnFailureListener {
                result = Result.retry()
            }
            .addOnSuccessListener {
                result = Result.success()
            }


        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                return Result.retry()
            }
            delay(1000)
        }
        return result as Result
    }
}