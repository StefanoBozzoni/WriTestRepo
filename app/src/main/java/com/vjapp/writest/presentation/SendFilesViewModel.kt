package com.vjapp.writest.presentation

import android.app.AlertDialog
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Contacts
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.common.config.GservicesValue.value
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.vjapp.writest.UploadFilesActivity
import com.vjapp.writest.data.model.SchoolsResponse
import com.vjapp.writest.data.model.UploadFilesRequest
import com.vjapp.writest.domain.interctor.*
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.domain.model.UploadFilesResponseEntity
import kotlinx.android.synthetic.main.activity_upload_files.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

class SendFilesViewModel(
    private val getTokenUseCase: UseCaseGetToken,
    private val httpBinDemoUseCase: UseCaseHttpBinDemo,
    private val sendFilesUseCase: UseCaseUploadFiles,
    private val getFilePathUseCase: UseCaseGetFilePathFromUri,
    private val getFileNameUseCase: UseCaseGetFileNameFromCursor,
    private val getSchoolsUseCase: UseCaseGetSchools,
    private val getClassesUseCase: UseCaseGetClasses
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
        viewModelScope.launch(Dispatchers.Main) {
            try {
                getConfigLivData.postValue(Resource.loading())
                val schools = async(Dispatchers.IO) { getSchoolsUseCase.execute() }
                val classes = async(Dispatchers.IO) { getClassesUseCase.execute() }
                getConfigLivData.value = Resource.success(classes.await())
                getConfigLivData.value = Resource.success(schools.await())
            } catch (t:Throwable) {
                getConfigLivData.value = Resource.error("Errore caricamento configurazione")
            }
        }
    }

    fun uploadAllFilesUsingFirebaseStorage(token: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                coroutineScope {
                    sendFilesLivData.value = Resource.loading()
                    val uno = async(Dispatchers.IO) {
                        uploadSingleFileUsingFirebaseStorage(
                            selectedPhotoUri!!,
                            token
                        )
                    }
                    val due = async(Dispatchers.IO) {
                        uploadSingleFileUsingFirebaseStorage(
                            selectedVideoUri!!,
                            token
                        )
                    }
                    val x1 = uno.await()
                    val x2 = due.await()
                    val esito = UploadFilesResponseEntity(esito = x2.toString())
                    sendFilesLivData.value = Resource.success(esito)
                }
            } catch (t: Throwable) {
                sendFilesLivData.value = Resource.error("Errore")
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

    fun generateNewToken():String {
        val randomUUID = UUID.randomUUID().toString()
        val editor = sharedpreferences.edit()
        editor.putString("token", randomUUID)
        editor.apply()
        return randomUUID
    }

}
