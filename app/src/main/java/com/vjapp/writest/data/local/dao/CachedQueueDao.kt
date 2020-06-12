package com.vjapp.writest.data.local.dao

import androidx.room.*
import com.vjapp.writest.data.local.database.DatabaseConstants
import com.vjapp.writest.data.local.model.CachedQueue
import com.vjapp.writest.data.local.model.CachedTest

@Dao
abstract class CachedQueueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun pushItem(cachedTest: CachedQueue) : Long

    @Query("SELECT count(*) FROM " + DatabaseConstants.TABLE_QUEUE)
    abstract suspend fun countQueueItems(): Int

    @Query("SELECT  * FROM ${DatabaseConstants.TABLE_QUEUE} where idItem=:id")
    abstract suspend fun getItem(id:Int): CachedQueue

    @Query("SELECT  * FROM ${DatabaseConstants.TABLE_QUEUE} LIMIT 1")
    abstract suspend fun popItem(): CachedQueue

}