package com.skyblu.data.room

import androidx.room.*
import com.skyblu.models.jump.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Database(entities = [SkydiveDataPoint::class, Skydive::class], version = 13)
@TypeConverters(SkydiveDataPointConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackingPointsDao(): TrackingPointsDao
}

@Dao
interface TrackingPointsDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJumpAndTrackingPoints(skydive : Skydive, trackingPoints : List<SkydiveDataPoint>)

    @Query("SELECT * FROM $SKYDIVE_TABLE")
    fun getAllJumps(): Flow<List<Skydive>>

    @Query("SELECT * FROM $SKYDIVE_TABLE WHERE skydiveID == :id")
    fun getJump(id : String): Flow<Skydive>

    @Query("SELECT * FROM $SKYDIVE_DATA_POINT_TABLE WHERE skydiveID == :id")
    fun getJumpTrackingPoints(id : String) : Flow<List<SkydiveDataPoint>>

    @Transaction
    @Query("SELECT * FROM $SKYDIVE_TABLE WHERE uploaded == 0")
    fun getQueuedSkydives() : Flow<List<SkydiveWithDatapoints>>


    @Query("DELETE  FROM $SKYDIVE_TABLE WHERE skydiveID == :id")
    suspend fun deleteSkydive(id : String)

    @Query("DELETE  FROM $SKYDIVE_DATA_POINT_TABLE WHERE skydiveID == :skyID")
    suspend fun deleteDataPoints(skyID : String)







}

class Converters {



}