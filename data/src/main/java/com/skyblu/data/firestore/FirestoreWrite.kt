package com.skyblu.data.firestore

import android.content.Context
import androidx.work.*
import com.skyblu.models.jump.DatapointParams
import com.skyblu.models.jump.JumpParams
import com.skyblu.models.jump.JumpWithDatapoints
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.ObjectOutput

class FirestoreWrite : WriteServerInterface {

    val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    val outOfQuotaPolicy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST

    /**
     * Deletes a jump from the firestore database
     * @param jumpID the ID of the user to delete
     */
    override fun deleteJump(
        jumpID: String,
        applicationContext: Context
    ) {
        val deleteWork: OneTimeWorkRequest = OneTimeWorkRequestBuilder<DeleteSkydiveWorker>()
            .setExpedited(outOfQuotaPolicy)
            .setInputData(
                workDataOf(
                    JumpParams.JUMP_ID to jumpID
                )
            )
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()
        WorkManager.getInstance(applicationContext).beginWith(deleteWork).enqueue()
    }

    override fun updateJump(
        jumpID: String?,
        title: String?,
        dropzone: String?,
        aircraft: String?,
        jumpNumber: Int?,
        description: String?,
        equipment: String?,
        applicationContext: Context
    ) {
        Timber.d(dropzone.toString())
        val updateSkydiveWork: OneTimeWorkRequest = OneTimeWorkRequestBuilder<UpdateSkydiveWorker>()

            .setExpedited(outOfQuotaPolicy)
            .setInputData(
                workDataOf(
                    JumpParams.JUMP_ID to jumpID,
                    JumpParams.EQUIPMENT to equipment,
                    JumpParams.DROPZONE to dropzone,
                    JumpParams.AIRCRAFT to aircraft,
                    JumpParams.DESCRIPTION to description,
                    JumpParams.TITLE to title,
                    JumpParams.JUMP_NUMBER to jumpNumber
                )
            )
            .setConstraints(
                constraints
            )
            .build()
        WorkManager.getInstance(applicationContext).beginWith(updateSkydiveWork).enqueue()
    }

    override fun uploadJump(
        jumpWithDatapoints: JumpWithDatapoints,
        applicationContext: Context
    ) {
        val url = jumpWithDatapoints.jump.staticMapUrl
        jumpWithDatapoints.jump.staticMapUrl = ""

        val uploadSkydiveWork: OneTimeWorkRequest = OneTimeWorkRequestBuilder<UploadSkydiveWorker>()
            .setExpedited(outOfQuotaPolicy)
            .setInputData(
                workDataOf(
                     JumpParams.JUMP to Json.encodeToString(jumpWithDatapoints.jump),
                    JumpParams.STATIC_MAP_URL to url
                )
            )
            .setConstraints(
                constraints
            )
            .build()
        val dataPointsWorkList = mutableListOf<OneTimeWorkRequest>()
        jumpWithDatapoints.datapoints.forEach { datapoint ->
            dataPointsWorkList.add(
                OneTimeWorkRequestBuilder<UploadDatapointWorker>()
                    .setExpedited(outOfQuotaPolicy)
                    .setInputData(
                        workDataOf(
                            DatapointParams.DATAPOINT to Json.encodeToString(datapoint)
                        )
                    )
                    .setConstraints(
                        constraints
                    )
                    .build()
            )
        }
        WorkManager.getInstance(applicationContext).beginWith(uploadSkydiveWork)
            .then(dataPointsWorkList).enqueue()
    }
}





interface WriteServerInterface {
    fun deleteJump(
        jumpID: String,
        applicationContext: Context
    )
    fun uploadJump(
        jumpWithDatapoints: JumpWithDatapoints,
        applicationContext: Context
    )
    fun updateJump(
        jumpID: String?,
        title: String?,
        dropzone: String?,
        aircraft: String?,
        jumpNumber: Int?,
        description: String?,
        equipment: String?,
        applicationContext: Context
    )
}
