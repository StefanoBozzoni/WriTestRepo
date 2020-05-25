package com.vjapp.writest.domain.interctor

import com.vjapp.writest.data.model.Resphttpbin
import com.vjapp.writest.domain.IRepository
import retrofit2.Call

class UseCaseHttpBinDemo(private val remoteRepository: IRepository) {
    suspend fun execute(): String {
        return remoteRepository.httpBinGetDemo()
        //return true
    }
}