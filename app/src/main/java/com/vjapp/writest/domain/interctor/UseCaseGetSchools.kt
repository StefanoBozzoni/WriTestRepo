package com.vjapp.writest.domain.interctor

import com.vjapp.writest.data.model.SchoolsResponse
import com.vjapp.writest.domain.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class UseCaseGetSchools(private val remoteRepository: IRepository) {
    suspend fun execute(): SchoolsResponse {
        return remoteRepository.getSchools()
        /*
        return coroutineScope {
            val x = async(Dispatchers.IO) {
                remoteRepository.getSchools()
            }
            x.await()
        }
       */
    }
    class Params()
}