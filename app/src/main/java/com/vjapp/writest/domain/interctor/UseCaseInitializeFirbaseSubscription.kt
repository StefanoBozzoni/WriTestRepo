package com.vjapp.writest.domain.interctor

import com.vjapp.writest.domain.IRepository

class UseCaseInitializeFirbaseSubscription(private val remoteRepository: IRepository) {
    suspend fun execute():String {
        return remoteRepository.getFireBaseToken()
    }
    companion object {
        val TAG =  UseCaseInitializeFirbaseSubscription::class.java.simpleName
    }
}