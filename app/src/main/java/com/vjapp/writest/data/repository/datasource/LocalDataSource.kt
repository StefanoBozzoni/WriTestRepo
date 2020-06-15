package com.vjapp.writest.data.repository.datasource

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vjapp.writest.data.local.database.AppDatabase
import com.vjapp.writest.data.local.model.CachedQueue
import com.vjapp.writest.data.local.model.CachedTest
import com.vjapp.writest.data.local.model.CachedTests
import com.vjapp.writest.domain.utils.await
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.CancellationException

class LocalDataSource(private val database: AppDatabase) {
    private val remoteDB  = Firebase.database.reference
    private val storageRef = Firebase.storage.reference

    suspend fun getTests(): List<CachedTest> {
        return database.cachedTestsDao().getTests()?:emptyList()
    }

    suspend fun findIdFromToken(uploadToken : String) : Int {
        return database.cachedTestsDao().findIdFromToken(uploadToken)
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
        remoteDB.child("history").child(user.uid).addValueEventListener(testListener)
        val result = def.await()
        remoteDB.child("history").child(user.uid).removeEventListener(testListener)

        return result.tests.map { el -> el.value }.sortedByDescending { el->el.idTest }
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

    suspend fun getTestFromRemote(uploadToken:String): CachedTest? {
        val def : CompletableDeferred<CachedTest> = CompletableDeferred()
        val user = Firebase.auth.currentUser!!

        //val currentTestRef = Firebase.database.getReference("history/${user.uid}/tests/${uploadToken}/diagnosis")

        val testListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val test = dataSnapshot.getValue(CachedTest::class.java)
                if (test==null)
                    def.completeExceptionally(Exception("Test non trovato"))
                else
                    def.complete(test)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                def.cancel(CancellationException("Errore recupero dati storici dal server"))
            }
        }

        remoteDB.child("history").child(user.uid).child("tests").child(uploadToken).addValueEventListener(testListener)
        val result:CachedTest? = def.await()
        remoteDB.child("history").child(user.uid).child("tests").child(uploadToken).removeEventListener(testListener)

        return result
    }

    suspend fun saveTest(ct : CachedTest): Long {
        val newId = database.cachedTestsDao().insertTest(ct) //insert into localDB
        val ct2 = ct.copy(idTest = newId.toInt())
        val user= Firebase.auth.currentUser
        remoteDB.child("history").child(user!!.uid).child("tests").child(ct2.token).setValue(ct2)  //insert into realtime DB (it is also local and remote)
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

    suspend fun getDiagnosisUrl(uploadToken: String): Uri {
        //return storageRef.child("uploads").child("diagnosis").child(uploadToken).downloadUrl.await()
        return storageRef.child("uploads").child(uploadToken+".jpg").downloadUrl.await()
    }


}