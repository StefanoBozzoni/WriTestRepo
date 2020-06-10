package com.vjapp.writest.domain.interctor

import com.vjapp.writest.domain.IRepository
import com.vjapp.writest.domain.model.TestEntity

class UseCaseGetSingleTest(private val remoteRepository: IRepository) {
    suspend fun execute(params:Params): TestEntity{
        return remoteRepository.getSingleTest(params.id)
    }
    class Params(val id:Int)
}
