package com.vjapp.writest.data.local.dao

import androidx.room.*
import com.vjapp.writest.data.local.database.DatabaseConstants
import com.vjapp.writest.data.local.model.CachedTest

@Dao
abstract class CachedTestsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTest(cachedTest: CachedTest) : Long

    @Query("SELECT count(*) FROM " + DatabaseConstants.TABLE_TESTS)
    abstract suspend fun countTests(): Int

    @Query("SELECT * FROM ${DatabaseConstants.TABLE_TESTS}")
    abstract suspend fun getTests(): List<CachedTest>?

    @Query("SELECT * FROM ${DatabaseConstants.TABLE_TESTS} where idTest=:id")
    abstract suspend fun getTest(id:Int): CachedTest?

    @Query("SELECT idTest FROM ${DatabaseConstants.TABLE_TESTS} where token=:uploadToken")
    abstract suspend fun findIdFromToken(uploadToken:String):Int

    @Query("DELETE FROM ${DatabaseConstants.TABLE_TESTS}")
    abstract suspend fun clearTestTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllTests(cachedTests: List<CachedTest>) : List<Long>



}