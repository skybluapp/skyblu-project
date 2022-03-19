package com.skyblu.database.database

import androidx.room.*
import com.skyblu.models.jump.*


@Database(entities = [TrackingPoint::class], version = 2)
abstract class MyDatabase : RoomDatabase() {
    abstract fun userDao(): JumpDao
}




@Dao
interface JumpDao {

}