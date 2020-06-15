package com.vjapp.writest.domain.interctor

import com.vjapp.writest.domain.IRepository
import com.vjapp.writest.domain.model.TestEntity

class UseCaseGetSingleTestFromRemote(private val remoteRepository: IRepository) {
    //return a test whit the provided id or uploadToken
    suspend fun execute(params:Params): TestEntity{
       return remoteRepository.getSingleTestFromRemote(params.uploadToken)
    }
    class Params(val uploadToken:String)
}
