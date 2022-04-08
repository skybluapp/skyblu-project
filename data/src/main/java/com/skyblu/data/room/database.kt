package com.skyblu.data.room

import androidx.room.*
import com.skyblu.models.jump.*
import kotlinx.coroutines.flow.Flow

@Database(entities = [SkydiveDataPoint::class, Jump::class], version = 13)
@TypeConverters(SkydiveDataPointConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackingPointsDao(): TrackingPointsDao
}

@Dao
interface TrackingPointsDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJumpAndTrackingPoints(skydive : Jump, trackingPoints : List<SkydiveDataPoint>)

    @Query("SELECT * FROM $JUMP_TABLE")
    fun getAllJumps(): Flow<List<Jump>>

    @Query("SELECT * FROM $JUMP_TABLE WHERE jumpID == :id")
    fun getJump(id : String): Flow<Jump>

    @Query("SELECT * FROM $JUMP_DATA_POINT_TABLE WHERE jumpID == :id")
    fun getJumpTrackingPoints(id : String) : Flow<List<SkydiveDataPoint>>

    @Transaction
    @Query("SELECT * FROM $JUMP_TABLE WHERE uploaded == 0")
    fun getQueuedSkydives() : Flow<List<JumpWithDatapoints>>


    @Query("DELETE  FROM $JUMP_TABLE WHERE jumpID == :id")
    suspend fun deleteSkydive(id : String)

    @Query("DELETE  FROM $JUMP_DATA_POINT_TABLE WHERE jumpID == :skyID")
    suspend fun deleteDataPoints(skyID : String)







}

class Converters {



}