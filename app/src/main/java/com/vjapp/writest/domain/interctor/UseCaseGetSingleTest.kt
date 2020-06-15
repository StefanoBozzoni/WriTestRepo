package com.vjapp.writest.domain.interctor

import com.vjapp.writest.domain.IRepository
import com.vjapp.writest.domain.model.TestEntity

class UseCaseGetSingleTest(private val remoteRepository: IRepository) {
    //return a test whit the provided id or uploadToken
    suspend fun execute(params:Params): TestEntity{
        if (params.id!=-1)
           return remoteRepository.getSingleTest(params.id)
        else
           return remoteRepository.getSingleTest(params.uploadToken)

    }
    class Params(val id:Int, val uploadToken:String)
}
