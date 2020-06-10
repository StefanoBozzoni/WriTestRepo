package com.vjapp.writest.domain.interctor

import com.vjapp.writest.domain.IRepository

class UseCaseGetToken(private val remoteRepository: IRepository) {
    suspend fun execute():String {
        //Thread.sleep(1000)
        //val rnds = (0..20).random()
        //return rnds.toString()
        return remoteRepository.getToken()
    }
}