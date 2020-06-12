package com.vjapp.writest.data.repository.datasource

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vjapp.writest.data.local.database.AppDatabase
import com.vjapp.writest.data.local.model.CachedQueue
import com.vjapp.writest.data.local.model.CachedTest
import com.vjapp.writest.data.local.model.CachedTests
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.CancellationException

class LocalDataSource(private val database: AppDatabase) {

    private val remoteDB = Firebase.database.reference

    suspend fun getTests(): List<CachedTest> {
        return database.cachedTestsDao().getTests()?:emptyList()
    }

    suspend fun getTestsFromRemote(): List<CachedTest> {
        val def : CompletableDeferred<CachedTests> = CompletableDeferred()
        val testListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val tests = dataSnapshot.getValue(CachedTests::class.java)
                def.complete(tests!!)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                def.cancel(CancellationException("Errore recupero dati storici dal server"))
            }
        }
        val user = Firebase.auth.currentUser!!
        remoteDB.child("history").child(user.uid).addListenerForSingleValueEvent(testListener)
        val result = def.await()
        remoteDB.child("history").child(user.uid).removeEventListener(testListener)

        return result.tests.map { el -> el.value }
    }

    @ExperimentalCoroutinesApi
    suspend fun getTestsAndListenFromRemote(): Flow<List<CachedTest>> = callbackFlow {
        val testListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val tests = dataSnapshot.getValue(CachedTests::class.java)
                offer(tests?.tests?.map {el -> el.value}?: emptyList<CachedTest>())
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                cancel(CancellationException("Errore recupero dati storici dal server"))
            }
        }
        val user = Firebase.auth.currentUser!!
        remoteDB.child("history").child(user.uid).addValueEventListener(testListener)

        awaitClose { remoteDB.child("history").child(user.uid).removeEventListener(testListener) }
    }

    suspend fun getTest(id:Int): CachedTest? {
        return database.cachedTestsDao().getTest(id)
    }

    suspend fun saveTest(ct : CachedTest): Long {
        val newId = database.cachedTestsDao().insertTest(ct) //insert into localDB
        val ct2 = ct.copy(idTest = newId.toInt())
        val user= Firebase.auth.currentUser
        remoteDB.child("history").child(user!!.uid).child("tests").child(newId.toString()).setValue(ct2)  //insert into realtime DB (it is also local and remote)
        return newId
    }

    fun registerUpload(token:String) {
        val user = Firebase.auth.currentUser!!
        try {
            remoteDB.child("deliveryhistory").child(token).child("email")
                .setValue(user.email!!.replace(".", ","))
        }
        catch (e:Throwable) {
            Log.d("TAG",e.message)
        }
    }

    suspend fun deleteTests() {
        database.cachedTestsDao().clearTestTable()
    }

    suspend fun insertAllTests(listTests:List<CachedTest>):List<Long> {
        return database.cachedTestsDao().insertAllTests(listTests)
    }

    suspend fun addDiagnosisToQueue(token:String,email:String, diagnosis:String) {
        database.cachedQueueDao().pushItem(CachedQueue("updateDiagnosis",token,email,diagnosis,null))
    }


}