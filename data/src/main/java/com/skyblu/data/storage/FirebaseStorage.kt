package com.skyblu.data.storage

import android.content.Context
import android.net.Uri
import androidx.work.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skyblu.data.firestore.TIMEOUT_MILLIS
import com.skyblu.data.firestore.UploadUserWorker
import com.skyblu.models.jump.User
import com.skyblu.models.jump.UserParameterNames
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

class FirebaseStorage : StorageInterface {

    override suspend fun uploadProfilePicture(
        applicationContext: Context,
        userID: String,
        user: User,
        uri: Uri,
    ) {
        val uploadProfilePictureWork: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<UploadProfilePictureWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(
                    workDataOf(
                        UserParameterNames.ID to userID,
                        UserParameterNames.PHOTO_URL to uri.toString()
                    )
                )
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .addTag("upload_profile_picture_work")
                .build()
        val uploadUserWork: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<UploadUserWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(
                    workDataOf(
                        UserParameterNames.USER to Json.encodeToString(user),
                    )
                )
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .addTag("upload_user_work")
                .build()
        WorkManager.getInstance(applicationContext).beginWith(uploadProfilePictureWork)
            .then(uploadUserWork).enqueue()
    }

    override fun getProfilePicture(userID: String): Result<String?> {
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
        val photoUriString = inputData.getString(UserParameterNames.PHOTO_URL) ?: return Result.failure()
        val userID = inputData.getString(UserParameterNames.ID) ?: return Result.failure()
        var result: Result? = null
        val startTime = System.currentTimeMillis()

        Timber.d("UploadWorker: $userID")
        Timber.d("UploadWorker: $photoUriString")
        val location = firebaseStorage.child("profilePictures/$userID")
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
                        UserParameterNames.PHOTO_URL to downloadUri
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
