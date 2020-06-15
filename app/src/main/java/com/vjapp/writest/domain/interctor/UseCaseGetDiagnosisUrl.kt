package com.vjapp.writest.domain.interctor

import android.net.Uri
import com.vjapp.writest.domain.IRepository
import com.vjapp.writest.domain.model.TestEntity

class UseCaseGetDiagnosisUrl(private val remoteRepository: IRepository) {
    //return a test whit the provided id or uploadToken
    suspend fun execute(params:Params): Uri {
       return remoteRepository.getDiagnosisUrl(params.uploadToken)
    }
    class Params(val uploadToken:String)
}
