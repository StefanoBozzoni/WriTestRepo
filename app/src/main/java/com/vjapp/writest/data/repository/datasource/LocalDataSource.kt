package com.vjapp.writest.data.repository.datasource

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vjapp.writest.data.local.database.AppDatabase
import com.vjapp.writest.data.local.model.CachedTest

class LocalDataSource(private val database: AppDatabase) {

    private val remoteDB = Firebase.database.reference

    suspend fun getTests(): List<CachedTest> {
        return database.cachedTestsDao().getTests()?:emptyList()
    }

    suspend fun getTest(id:Int): CachedTest? {
        return database.cachedTestsDao().getTest(id)
    }

    suspend fun saveTest(ct : CachedTest): Long {
        val newId = database.cachedTestsDao().insertTest(ct) //insert into localDB
        val ct2 = ct.copy(idTest = newId.toInt())
        val user= Firebase.auth.currentUser
        remoteDB.child("history").child(user!!.uid).child(newId.toString()).setValue(ct2)  //insert into realtime DB (it is also local and remote)
        return newId
    }

    /*
    fun saveContact(contact: ContactsEntity) {
        return database.cachedDoctorDao().insertContact(ContactsEntityMapper.mapFromEntity(contact))
    }

    fun saveDoctorInfos(note: String) {
        return database.cachedDoctorDao().saveDoctorInfos(CachedDoctorInfos(note))
    }

    fun getDoctorInfos():CachedDoctorInfos? {
        return database.cachedDoctorDao().getDoctorInfos()
    }

    fun deleteContact(contact: ContactsEntity):Boolean {
        return (database.cachedDoctorDao().deleteContact(ContactsEntityMapper.mapFromEntity(contact))>0)
    }

    fun clear() {
        database.clearAllTables()
    }

     */


}