package com.vjapp.writest.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.vjapp.writest.data.local.dao.CachedQueueDao
import com.vjapp.writest.data.local.dao.CachedTestsDao
import com.vjapp.writest.data.local.model.CachedQueue
import com.vjapp.writest.data.local.model.CachedTest

@Database(entities = [CachedTest::class, CachedQueue::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cachedTestsDao(): CachedTestsDao
    abstract fun cachedQueueDao(): CachedQueueDao
}
