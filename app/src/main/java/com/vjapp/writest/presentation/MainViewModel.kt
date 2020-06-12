package com.vjapp.writest.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.vjapp.writest.domain.interctor.UseCaseInitializeFirbaseSubscription
import com.vjapp.writest.domain.interctor.UseCaseSaveTest
import com.vjapp.writest.domain.interctor.UseCaseSynchLocalDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val synchDBUseCase: UseCaseSynchLocalDB
) : ViewModel() {
    /*
    val notificationSubscriptionLiveData = liveData(Dispatchers.IO) {
        val retrivedToken :Resource<String> = try {
            Resource.success(initializeNotificationUseCase.execute())
        } catch (e:Throwable) {
            Resource.error(e.message?:"","")
        }
        this.emit(retrivedToken)
    }
    */

    val syncDBLiveData = liveData(Dispatchers.IO) {
        val retrivedToken :Resource<Boolean> = try {
            Resource.success(synchDBUseCase.execute())
        } catch (e:Throwable) {
            Resource.error(e.message?:"",false)
        }
        this.emit(retrivedToken)
    }


}
