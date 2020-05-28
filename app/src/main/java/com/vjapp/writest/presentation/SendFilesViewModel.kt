package com.vjapp.writest.presentation

import android.net.Uri
import android.provider.Contacts
import androidx.lifecycle.*
import com.vjapp.writest.data.model.SchoolsResponse
import com.vjapp.writest.data.model.UploadFilesRequest
import com.vjapp.writest.domain.interctor.*
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.domain.model.UploadFilesResponseEntity
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class SendFilesViewModel(
    private val getTokenUseCase       : UseCaseGetToken,
    private val httpBinDemoUseCase    : UseCaseHttpBinDemo,
    private val sendFilesUseCase      : UseCaseUploadFiles,
    private val getFilePathUseCase    : UseCaseGetFilePathFromUri,
    private val getFileNameUseCase    : UseCaseGetFileNameFromCursor,
    private val getSchoolsUseCase: UseCaseGetSchools,
    private val getClassesUseCase: UseCaseGetClasses
) : ViewModel() {

  var sendFilesLivData = MutableLiveData<Resource<UploadFilesResponseEntity>>()

  var getConfigLivData = MutableLiveData<Resource<Any>>()

  /*
  val httpBinLiveData = liveData(Dispatchers.IO) {
        val retrivedTodo = httpBinDemoUseCase.execute()
        emit(retrivedTodo)
  }
  var httpBinLiveData2 = MutableLiveData<String>()
  */

  var selectedPhotoUri : Uri ?= null
  var selectedVideoUri : Uri ?= null
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

  fun sendFiles(x : UploadFilesRequestEntity) {
      try {
          viewModelScope.launch(Dispatchers.IO) {
              val res = sendFilesUseCase.execute(UseCaseUploadFiles.Params(x))
              sendFilesLivData.postValue(Resource.success(res))
          }
      } catch (e:Throwable) {
          sendFilesLivData.postValue(Resource.error("Errore invio files"))
      }
  }

  fun getFilePathFromUri(uri: Uri, outPutFileName: String) : File? {
      return getFilePathUseCase.execute(uri, outPutFileName)
  }

  fun getFileNameFromCursor(uri: Uri): Pair<String?, Long> {
      return getFileNameUseCase.execute(uri)
  }

  fun getConfig() {
      viewModelScope.launch(Dispatchers.Main) {
          getConfigLivData.postValue(Resource.loading())
          val schools = async(Dispatchers.IO) {getSchoolsUseCase.execute()}
          val classes = async(Dispatchers.IO) {getClassesUseCase.execute()}
          getConfigLivData.value=Resource.success(classes.await())
          getConfigLivData.value=Resource.success(schools.await())
      }
  }

}
