package com.vjapp.writest.domain.interctor

import com.vjapp.writest.data.remote.model.SchoolsResponse
import com.vjapp.writest.domain.IRepository

class UseCaseSynchLocalDB(private val remoteRepository: IRepository) {
    suspend fun execute(): Boolean {
        return remoteRepository.syncDB()
    }
    class Params()
}