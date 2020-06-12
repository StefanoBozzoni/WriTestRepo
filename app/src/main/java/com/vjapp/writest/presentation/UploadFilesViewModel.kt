package com.vjapp.writest.presentation

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.vjapp.writest.domain.interctor.*
import com.vjapp.writest.domain.model.TestEntity
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.domain.model.UploadFilesResponseEntity
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class UploadFilesViewModel(
    private val getTokenUseCase: UseCaseGetToken,
    private val httpBinDemoUseCase: UseCaseHttpBinDemo,
    private val sendFilesUseCase: UseCaseUploadFiles,
    private val getFilePathUseCase: UseCaseGetFilePathFromUri,
    private val getFileNameUseCase: UseCaseGetFileNameFromCursor,
    private val getSchoolsUseCase: UseCaseGetSchools,
    private val getClassesUseCase: UseCaseGetClasses,
    private val saveTestUseCase: UseCaseSaveTest,
    private val registerUploadUSeCase: UseCaseRegisterUpload,
    private val context:Context
) : ViewModel() {
    lateinit var sharedpreferences: SharedPreferences

    var sendFilesLivData = MutableLiveData<Resource<UploadFilesResponseEntity>>()
    var getConfigLivData = MutableLiveData<Resource<Any>>()
    var newTokenLivData = MutableLiveData<Resource<String>>()

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

    fun uploadAllFilesUsingFirebaseStorage(token: String, classe:String, scuola:String ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                coroutineScope {
                    sendFilesLivData.postValue(Resource.loading())
                    val defImgUri = async {
                        uploadSingleFileUsingFirebaseStorage(
                            selectedPhotoUri!!,
                            token,
                            "jpg"
                        )
                    }

                    val defVideoUri = async {
                        uploadSingleFileUsingFirebaseStorage(
                            selectedVideoUri!!,
                            token,
                            "mp4"
                        )
                    }

                    registerUploadUSeCase.execute(UseCaseRegisterUpload.Params(token))

                    val imgUri   = defImgUri.await()
                    val videoUri = defVideoUri.await()
                    //defreg.await()

                    val uploadOutcome = UploadFilesResponseEntity(esito = "OK")
                    if (uploadOutcome.esito=="OK") {
                        val testToSave = TestEntity(
                                           sendDate    = Date(),
                                           token       = token,
                                           videoUri    = videoUri.toString(),
                                           imgUri      = imgUri.toString(),
                                           iDSchool    = "1",
                                           iDClassType = "1",
                                           school      = scuola,
                                           classType   = classe,
                                           idTest      = null)
                        async { saveDataToDB(testToSave) }.await()
                    }
                    generateNewTokenSusp()

                    sendFilesLivData.postValue(Resource.success(uploadOutcome))
                }
            } catch (t: Throwable) {
                sendFilesLivData.postValue(Resource.error("Errore"))
            }
        }
    }

    suspend fun saveDataToDB(t:TestEntity):Boolean {
        return saveTestUseCase.execute(UseCaseSaveTest.Params(t))>0
    }

    fun uploadSingleFileUsingFirebaseStorage(fileUri: Uri, token: String, extension:String): Uri {
        val TAG = "UploadFiles"

        val metadata = storageMetadata {
            setCustomMetadata("fromUID", Firebase.auth.currentUser?.uid)
        }
        /*
        uploadPathRef.updateMetadata(metadata).addOnSuccessListener {
            Log.d(TAG, "metadata updated")
        }
        */

        val storageRef = Firebase.storage.reference
        val uploadPathRef = storageRef.child("uploads/${token + "."+extension}")

        val myUploadTask = fileUri.let {
            // Upload file to Firebase Storage
            Log.d(TAG, "uploadFromUri:dst:" + uploadPathRef.path)
            uploadPathRef.putFile(fileUri,metadata).addOnProgressListener { taskSnapshot ->
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

        return Tasks.await(myUploadTask)
    }

    /*
    fun generateNewToken(): String {
        val randomUUID = UUID.randomUUID().toString()
        val editor = sharedpreferences.edit()
        editor.putString("token", randomUUID)
        editor.apply()
        return randomUUID
    }
   */

    suspend fun generateNewTokenSusp() {
        newTokenLivData.postValue(Resource.loading())
        val newToken = getTokenUseCase.execute()
        val editor = sharedpreferences.edit()
        editor.putString("token", newToken)
        editor.commit() //sincrono
        newTokenLivData.postValue(Resource.success(newToken))
    }

    fun generateNewToken() {
        viewModelScope.launch(Dispatchers.IO) {
            generateNewTokenSusp()
        }
    }

}
