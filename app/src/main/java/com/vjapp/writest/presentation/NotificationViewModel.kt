package com.vjapp.writest.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.vjapp.writest.domain.interctor.UseCaseInitializeFirbaseSubscription
import kotlinx.coroutines.Dispatchers

class NotificationViewModel(
    private val initializeNotificationUseCase: UseCaseInitializeFirbaseSubscription
) : ViewModel() {

    val notificationSubscriptionLiveData = liveData(Dispatchers.IO) {
        val retrivedToken :Resource<String> = try {
            Resource.success(initializeNotificationUseCase.execute())
        } catch (e:Throwable) {
            Resource.error(e.message?:"","")
        }
        this.emit(retrivedToken)
    }
}
