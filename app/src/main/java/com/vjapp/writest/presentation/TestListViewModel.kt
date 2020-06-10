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

class TestListViewModel(
    private val getTestsUseCase: UseCaseGetTests
) : ViewModel() {
    var testsLiveData = MutableLiveData<Resource<List<TestEntity>>>()

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tests = getTestsUseCase.execute()
                testsLiveData.postValue(Resource.success(tests))
            }
            catch(e:Throwable) {
                testsLiveData.postValue(Resource.error("Errore ricezione test"))
            }
        }
    }
}
