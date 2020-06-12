package com.vjapp.writest.domain.interctor

import com.vjapp.writest.domain.IRepository

class UseCaseAddMessageToQueue(private val remoteRepository: IRepository) {
    suspend fun execute(params:Params) {
        return remoteRepository.addDiagnosisToQueue(params.token,params.email, params.diagnosis)
    }
    class Params(val token:String, val email: String, val diagnosis:String)
}