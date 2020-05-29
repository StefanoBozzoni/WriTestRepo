package com.vjapp.writest.presentation

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vjapp.writest.domain.interctor.*
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.domain.model.UploadFilesResponseEntity
import kotlinx.coroutines.*
import org.koin.dsl.module.applicationContext
import java.io.File
import java.util.*

class SendFilesViewModel(
    private val getTokenUseCase: UseCaseGetToken,
    private val httpBinDemoUseCase: UseCaseHttpBinDemo,
    private val sendFilesUseCase: UseCaseUploadFiles,
    private val getFilePathUseCase: UseCaseGetFilePathFromUri,
    private val getFileNameUseCase: UseCaseGetFileNameFromCursor,
    private val getSchoolsUseCase: UseCaseGetSchools,
    private val getClassesUseCase: UseCaseGetClasses,
    private val context:Context
) : ViewModel() {
    lateinit var sharedpreferences: SharedPreferences

    var sendFilesLivData = MutableLiveData<Resource<UploadFilesResponseEntity>>()

    var getConfigLivData = MutableLiveData<Resource<Any>>()

    /*
    val httpBinLiveData = liveData(Dispatchers.IO) {
          val retrivedTodo = httpBinDemoUseCase.execute()
          emit(retrivedTodo)
    }
    var httpBinLiveData2 = MutableLiveData<String>()
    */

    var selectedPhotoUri: Uri? = null
    var selectedVideoUri: Uri? = null
    var photoPage = 0
    var videoPage = 0

    fun init() {
        sharedpreferences = context.getSharedPreferences("WritestPref", Context.MODE_PRIVATE)
    }

    /*
    fun httpBinDemo() {
        viewModelScope.launch(Dispatchers.IO) {
            val res =httpBinDemoUseCase.execute()
            //val res = async { httpBinDemoUseCase.execute() }.await()
            httpBinLiveData2.postValue(res)
        }

        /* alternativamente senza usare retrofit con le suspended functions
        viewModelScope.launch(Dispatchers.IO) {
            val res = async { httpBinDemoUseCase.execute() }.await()
            httpBinLiveData2.postValue(res)
        }
        */
    }
    */

    fun sendFiles(filesToUpload: UploadFilesRequestEntity) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = sendFilesUseCase.execute(UseCaseUploadFiles.Params(filesToUpload))
                sendFilesLivData.postValue(Resource.success(res))
            } catch (e: Throwable) {
                sendFilesLivData.postValue(Resource.error("Errore invio files"))
            }

        }
    }

    fun getFilePathFromUri(uri: Uri, outPutFileName: String): File? {
        return getFilePathUseCase.execute(uri, outPutFileName)
    }

    fun getFileNameFromCursor(uri: Uri): Pair<String?, Long> {
        return getFileNameUseCase.execute(uri)
    }

    fun getConfig() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getConfigLivData.postValue(Resource.loading())
                val schools = async { getSchoolsUseCase.execute() }
                val classes = async { getClassesUseCase.execute() }
                val classesResp = classes.await()
                val schoolsResp = schools.await()
                withContext(Dispatchers.Main) {
                    getConfigLivData.value = Resource.success(classesResp)
                    getConfigLivData.value = Resource.success(schoolsResp)
                }
            } catch (t: Throwable) {
                getConfigLivData.postValue(Resource.error("Errore caricamento configurazione"))
            }
        }
    }

    fun uploadAllFilesUsingFirebaseStorage(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                coroutineScope {
                    sendFilesLivData.postValue(Resource.loading())
                    val uno = async {
                        uploadSingleFileUsingFirebaseStorage(
                            selectedPhotoUri!!,
                            token
                        )
                    }
                    val due = async {
                        uploadSingleFileUsingFirebaseStorage(
                            selectedVideoUri!!,
                            token
                        )
                    }
                    val x1 = uno.await()
                    val x2 = due.await()
                    val esito = UploadFilesResponseEntity(esito = x2.toString())
                    sendFilesLivData.postValue(Resource.success(esito))
                }
            } catch (t: Throwable) {
                sendFilesLivData.postValue(Resource.error("Errore"))
            }
        }
    }

    fun uploadSingleFileUsingFirebaseStorage(fileUri: Uri, token: String): Uri {
        val TAG = "UploadFiles"

        val storageRef = Firebase.storage.reference
        val uploadPathRef = storageRef.child("uploads/${token + fileUri.lastPathSegment}")

        val myUploadTask = fileUri.lastPathSegment?.let {
            // Upload file to Firebase Storage
            Log.d(TAG, "uploadFromUri:dst:" + uploadPathRef.path)
            uploadPathRef.putFile(fileUri).addOnProgressListener { taskSnapshot ->
                //showProgressNotification(getString(R.string.progress_uploading),
                //    taskSnapshot.bytesTransferred,
                //    taskSnapshot.totalByteCount)
            }.continueWithTask { task ->
                // Forward any exceptions
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                Log.d(TAG, "uploadFromUri: upload success")
                uploadPathRef.downloadUrl
            }.addOnSuccessListener { downloadUri ->
                // Upload succeeded
                Log.d(TAG, "uploadFromUri: getDownloadUri success")
                //broadcastUploadFinished(downloadUri, fileUri)
                //showUploadFinishedNotification(downloadUri, fileUri)
            }.addOnFailureListener { exception ->
                // Upload failed
                Log.w(TAG, "uploadFromUri:onFailure", exception)
                //broadcastUploadFinished(null, fileUri)
                //showUploadFinishedNotification(null, fileUri)
            }
        }

        return Tasks.await(myUploadTask!!)
    }

    fun generateNewToken(): String {
        val randomUUID = UUID.randomUUID().toString()
        val editor = sharedpreferences.edit()
        editor.putString("token", randomUUID)
        editor.apply()
        return randomUUID
    }

}
