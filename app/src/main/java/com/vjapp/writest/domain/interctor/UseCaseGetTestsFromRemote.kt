package com.vjapp.writest.domain.interctor

import com.vjapp.writest.data.remote.model.SchoolsResponse
import com.vjapp.writest.domain.IRepository
import com.vjapp.writest.domain.model.TestEntity

class UseCaseGetTestsFromRemote(private val remoteRepository: IRepository) {
    suspend fun execute(): List<TestEntity> {
        return remoteRepository.getTestsFromRemote()
    }
    class Params()
}