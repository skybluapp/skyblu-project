package com.skyblu.data.firestore

import android.content.Context
import androidx.work.*
import com.google.firebase.firestore.*
import com.skyblu.models.jump.*
import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

class FireStore : ServerInterface {

    private val fakeDataSource = (1 .. 100).map {
        generateSampleJump()
    }

    init {
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }

    override suspend fun getSkydivesLocally(
        page: Int,
        pageSize: Int
    ): Result<List<Skydive>> {
        delay(2000)
        val startingIndex = page * pageSize
        return if (startingIndex + pageSize <= fakeDataSource.size) {
            Result.success(
                fakeDataSource.slice(startingIndex until startingIndex + pageSize)
            )
        } else {
            Result.success(emptyList())
        }
    }

    override suspend fun getDatapointsFromServer(
        skydiveID: String
    ): Result<QuerySnapshot> {
        val firestore = FirebaseFirestore.getInstance()
        var result: Result<QuerySnapshot>? = null
        val startTime = System.currentTimeMillis()


        val documentReference = firestore.collection("skydives").document(skydiveID).collection("datapoints").get()

        documentReference
            .addOnSuccessListener { datapointDocuments ->
                result = Result.success(datapointDocuments)
            }
            .addOnFailureListener {
                result = Result.failure(it)
            }

        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS * 300 > startTime) {
                Timber.d("Doing Work : Getting Datapoints Timeout")
                return Result.failure(Exception())
            }
            delay(1000)
        }

        return result as Result<QuerySnapshot>
    }

    override suspend fun getSkydivesFromServer(
        page: DocumentSnapshot?,
        pageSize: Int
    ): Result<QuerySnapshot> {
        val firestore = FirebaseFirestore.getInstance()
        var result: Result<QuerySnapshot>? = null
        val startTime = System.currentTimeMillis()
        val documentReference = if (page != null) {
            Timber.d("Next Page")
            firestore.collection("skydives").orderBy("date", Query.Direction.DESCENDING).startAfter(page).limit(pageSize.toLong()).get()
        } else {
            Timber.d("First Page")
            firestore.collection("skydives").orderBy("date", Query.Direction.DESCENDING).limit(pageSize.toLong()).get()
        }

        documentReference
            .addOnSuccessListener { skydiveDocuments ->
                result = Result.success(skydiveDocuments)
            }
            .addOnFailureListener {
                result = Result.failure(it)
            }

        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                Timber.d("Doing Work : Delete Timeout")
                return Result.failure(Exception())
            }
            delay(1000)
        }

        return result as Result<QuerySnapshot>
    }

    override suspend fun getSkydiverFromServer(id: String): Result<DocumentSnapshot?> {
        val firestore = FirebaseFirestore.getInstance()
        var result: Result<DocumentSnapshot?>? = null
        val startTime = System.currentTimeMillis()

        firestore.collection("users").document(id).get()
            .addOnSuccessListener {document ->
                result = Result.success(document)
                if(document.exists()){
                    result = Result.success(document)
                } else {
                    result = Result.success(null)
                }

            }
            .addOnFailureListener{
                result = Result.failure(java.lang.Exception())
            }

        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                return Result.failure(Exception())
            }
            delay(100)
        }

        return result as Result<DocumentSnapshot>

    }

    override suspend fun getSkydiveFromServer(id: String): Result<DocumentSnapshot?> {
        val firestore = FirebaseFirestore.getInstance()
        var result: Result<DocumentSnapshot?>? = null
        val startTime = System.currentTimeMillis()



        firestore.collection("skydives").document(id).get()
            .addOnSuccessListener {document ->
                result = Result.success(document)
                if(document.exists()){
                    result = Result.success(document)
                } else {
                    result = Result.success(null)
                }
            }
            .addOnFailureListener{
                result = Result.failure(java.lang.Exception())
            }

        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                return Result.failure(Exception())
            }
            delay(100)
        }
        return result as Result<DocumentSnapshot>
    }

    override fun deleteSkydive(
        skydiveID: String,
        applicationContext: Context
    ) {
        val deleteWork: OneTimeWorkRequest = OneTimeWorkRequestBuilder<DeleteSkydiveWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(
                workDataOf(
                    "SKYDIVE_ID" to skydiveID
                )
            )
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .addTag("delete_work")
            .build()
        WorkManager.getInstance(applicationContext).beginWith(deleteWork).enqueue()
    }

    override fun uploadSkydive(
        skydive: Skydive,
        applicationContext: Context
    ) {
        val uploadSkydiveWork: OneTimeWorkRequest = OneTimeWorkRequestBuilder<UploadSkydiveWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(
                workDataOf(
                    "SKYDIVE" to Json.encodeToString(skydive)
                )
            )
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .addTag("delete_work")
            .build()
        WorkManager.getInstance(applicationContext).beginWith(uploadSkydiveWork).enqueue()
    }

    override fun uploadSkydiveDatapoint(
        datapoint: SkydiveDataPoint,
        applicationContext: Context
    ) {
        val uploadDataPointWork: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<UploadSkydiveWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(
                    workDataOf(
                        "DATAPOINT" to Json.encodeToString(datapoint)
                    )
                )
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .addTag("upload_data_point_work")
                .build()
        WorkManager.getInstance(applicationContext).beginWith(uploadDataPointWork).enqueue()
    }

    override fun uploadSkydiveWithDatapoints(
        skydiveWithDataPoints: SkydiveWithDatapoints,
        applicationContext: Context
    ) {
        val url = skydiveWithDataPoints.skydive.staticMapUrl
        skydiveWithDataPoints.skydive.staticMapUrl = ""
        val uploadSkydiveWork: OneTimeWorkRequest = OneTimeWorkRequestBuilder<UploadSkydiveWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(
                workDataOf(
                    "SKYDIVE" to Json.encodeToString(skydiveWithDataPoints.skydive),
                    "URL" to url
                )
            )
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .addTag("delete_work")
            .build()
        val dataPointsWorkList = mutableListOf<OneTimeWorkRequest>()
        skydiveWithDataPoints.datapoints.forEach { datapoint ->
            dataPointsWorkList.add(
                OneTimeWorkRequestBuilder<UploadDatapointWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setInputData(
                        workDataOf(
                            "DATAPOINT" to Json.encodeToString(datapoint)
                        )
                    )
                    .setConstraints(
                        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                    )
                    .addTag("upload_data_point_work")
                    .build()
            )
        }
        WorkManager.getInstance(applicationContext).beginWith(uploadSkydiveWork)
            .then(dataPointsWorkList).enqueue()
    }
}

const val TIMEOUT_MILLIS = 10000

class DeleteSkydiveWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        Timber.d("Doing Work")
        val skydiveID = inputData.getString("SKYDIVE_ID") ?: return Result.failure()
        var result: Result? = null
        val startTime = System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection("skydives").document(skydiveID).delete()
            .addOnSuccessListener {
                result = Result.success()
                Timber.d("Doing Work : Delete Successful")
            }
            .addOnFailureListener {
                Timber.d("Doing Work : Delete Failed")
                result = Result.retry()
            }
        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                Timber.d("Doing Work : Delete Timeout")
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
        val skydiveString = inputData.getString("SKYDIVE") ?: return Result.failure()
        val urlString = inputData.getString("URL") ?: return Result.failure()
        val skydive: Skydive = Json.decodeFromString<Skydive>(skydiveString)
        skydive.staticMapUrl = urlString
        var result: Result? = null
        val startTime = System.currentTimeMillis()

        firestore.collection("skydives").document(skydive.skydiveID).set(skydive)
            .addOnFailureListener {
                Timber.d("Upload Failed" + it.message)
                result = Result.retry()
            }
            .addOnSuccessListener {
                Timber.d("Upload successful")
                result = Result.success()
            }

        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                Timber.d("Doing Work : Delete Timeout")
                return Result.retry()
            }
            delay(1000)
        }
        return result as Result
    }
}

class UploadSkydiverWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val skydiverString = inputData.getString("SKYDIVER") ?: return Result.failure()
        val skydiver: Skydiver = Json.decodeFromString<Skydiver>(skydiverString)
        val url : String? = inputData.getString("URL")
        skydiver.skydiverPhotoUrl =  url

        var result: Result? = null
        val startTime = System.currentTimeMillis()
        Timber.d("Here I Am")

        firestore.collection("users").document(skydiver.skydiverID).set(skydiver)
            .addOnFailureListener {
                Timber.d("Upload Failed" + it.message)
                result = Result.retry()
            }
            .addOnSuccessListener {
                Timber.d("Upload successful")
                result = Result.success()
            }

        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                Timber.d("Doing Work : Delete Timeout")
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
        val skydiveDataPointString = inputData.getString("DATAPOINT") ?: return Result.failure()
        val datapoint: SkydiveDataPoint =
            Json.decodeFromString<SkydiveDataPoint>(skydiveDataPointString)
        var result: Result? = null
        val startTime = System.currentTimeMillis()


        firestore.collection("skydives").document(datapoint.skydiveID).collection("datapoints")
            .add(datapoint)
            .addOnFailureListener {
                Timber.d("Upload Failed" + it.message)
                result = Result.retry()
            }
            .addOnSuccessListener {
                Timber.d("Upload successful")
                result = Result.success()
            }


        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                Timber.d("Doing Work : Delete Timeout")
                return Result.retry()
            }
            delay(1000)
        }
        return result as Result
    }
}
