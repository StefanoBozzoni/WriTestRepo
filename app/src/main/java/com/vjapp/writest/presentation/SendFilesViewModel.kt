package com.vjapp.writest.presentation

import android.net.Uri
import androidx.lifecycle.*
import com.vjapp.writest.data.model.UploadFilesRequest
import com.vjapp.writest.domain.interctor.*
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.domain.model.UploadFilesResponseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class SendFilesViewModel(
    private val getTokenUseCase       : UseCaseGetToken,
    private val httpBinDemoUseCase    : UseCaseHttpBinDemo,
    private val sendFilesUseCase      : UseCaseUploadFiles,
    private val getFilePathUseCase    : UseCaseGetFilePathFromUri,
    private val getFileNameUseCase    : UseCaseGetFileNameFromCursor
) : ViewModel() {

  var httpBinLiveData2 = MutableLiveData<String>()
  var sendFilesLivData = MutableLiveData<Resource<UploadFilesResponseEntity>>()

  val httpBinLiveData = liveData(Dispatchers.IO) {
        val retrivedTodo = httpBinDemoUseCase.execute()
        emit(retrivedTodo)
  }

  var selectedPhotoUri : Uri ?= null
  var selectedVideoUri : Uri ?= null
  var photoPage = 0
  var videoPage = 0

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

  suspend fun getFilePathFromUri(uri: Uri, outPutFileName: String) : File? {
      return getFilePathUseCase.execute(uri, outPutFileName)
  }

  fun getFileNameFromCursor(uri: Uri): Pair<String?, Long> {
      return getFileNameUseCase.execute(uri)
  }

}
