package com.vjapp.writest.domain.interctor

import com.vjapp.writest.data.remote.model.ClassesResponse
import com.vjapp.writest.domain.IRepository
import com.vjapp.writest.domain.model.ClassesEntity
import com.vjapp.writest.domain.model.TestEntity

class UseCaseSaveTest(private val remoteRepository: IRepository) {
    suspend fun execute(param:Params) : Long {
       return remoteRepository.saveTest(param.aTest)
    }
    class Params(val aTest: TestEntity)
}