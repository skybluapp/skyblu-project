package com.skyblu.data.storage

import android.content.Context
import android.net.Uri
import androidx.work.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skyblu.data.firestore.TIMEOUT_MILLIS
import com.skyblu.data.firestore.UploadSkydiverWorker
import com.skyblu.models.jump.Skydiver
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

class FirebaseStorage : StorageInterface {

    override suspend fun uploadProfilePicture(
        applicationContext: Context,
        skydiverID: String,
        skydiver: Skydiver,
        uri: Uri,
    ) {
        val uploadProfilePictureWork: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<UploadProfilePictureWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(
                    workDataOf(
                        "SKYDIVER_ID" to skydiverID,
                        "URI" to uri.toString()
                    )
                )
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .addTag("upload_profile_picture_work")
                .build()
        val uploadSkydiverWork: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<UploadSkydiverWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(
                    workDataOf(
                        "SKYDIVER" to Json.encodeToString(skydiver),
                    )
                )
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .addTag("upload_skydiver_work")
                .build()
        WorkManager.getInstance(applicationContext).beginWith(uploadProfilePictureWork)
            .then(uploadSkydiverWork).enqueue()
    }

    override fun getProfilePicture(skydiverID: String): Result<String?> {
        TODO("Not yet implemented")
    }
}

class UploadProfilePictureWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        val firebaseStorage = Firebase.storage.reference
        val photoUriString = inputData.getString("URI") ?: return Result.failure()
        val skydiverID = inputData.getString("SKYDIVER_ID") ?: return Result.failure()
        var result: Result? = null
        val startTime = System.currentTimeMillis()
        val url: String? = null

        Timber.d("UploadWorker: $skydiverID")
        Timber.d("UploadWorker: $photoUriString")
        val location = firebaseStorage.child("profilePictures/$skydiverID")
        val file = Uri.parse(photoUriString)
        Timber.d("Uploading ${file.path}")
        val task = location.putFile(file)
        task.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            location.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                result = Result.success(
                    workDataOf(
                        "URL" to downloadUri
                    )
                )
            } else {
                // Handle failures
                // ...
            }
        }


        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS * 30 > startTime) {
                Timber.d("Doing Work : Delete Timeout")
                return Result.retry()
            }
            delay(1000)
        }

        return result as Result
    }
}
