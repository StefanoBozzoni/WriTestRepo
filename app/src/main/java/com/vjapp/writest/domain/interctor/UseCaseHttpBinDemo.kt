package com.vjapp.writest.domain.interctor

import com.vjapp.writest.domain.IRepository

class UseCaseHttpBinDemo(private val remoteRepository: IRepository) {
    suspend fun execute(): String {
        return remoteRepository.httpBinGetDemo()
        //return true
    }
}