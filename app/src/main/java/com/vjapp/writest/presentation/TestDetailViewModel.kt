package com.vjapp.writest.presentation

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask.execute
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vjapp.writest.domain.interctor.*
import com.vjapp.writest.domain.model.TestEntity
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.domain.model.UploadFilesResponseEntity
import kotlinx.coroutines.*
import org.koin.dsl.module.applicationContext
import java.io.File
import java.util.*

class TestDetailViewModel(
    private val getsingleTestUseCase: UseCaseGetSingleTestFromRemote,
    private val getDiagnosisUrlUseCase: UseCaseGetDiagnosisUrl
) : ViewModel() {

    var testsLiveData = MutableLiveData<Resource<TestEntity>>()
    var diagnosisLiveData = MutableLiveData<Resource<Uri>>()

    fun init(uploadToken:String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val test = getsingleTestUseCase.execute(UseCaseGetSingleTestFromRemote.Params(uploadToken))
                testsLiveData.postValue(Resource.success(test))
            }
            catch(e:Throwable) {
                testsLiveData.postValue(Resource.error("Errore ricezione test"))
            }
        }
    }

    fun showDiagnosis(uploadToken:String) {
        viewModelScope.launch(Dispatchers.IO) {
            diagnosisLiveData.postValue(Resource.loading())
            try {
                val uri = getDiagnosisUrlUseCase.execute(UseCaseGetDiagnosisUrl.Params(uploadToken))
                diagnosisLiveData.postValue(Resource.success(uri))
            }
            catch(e:Throwable) {
                diagnosisLiveData.postValue(Resource.error("Errore recupero diagnosi"))
            }

        }
    }
}
