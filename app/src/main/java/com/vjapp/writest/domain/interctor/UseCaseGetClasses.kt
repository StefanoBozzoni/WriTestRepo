package com.vjapp.writest.domain.interctor

import com.vjapp.writest.data.model.ClassesResponse
import com.vjapp.writest.data.model.SchoolsResponse
import com.vjapp.writest.domain.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class UseCaseGetClasses(private val remoteRepository: IRepository) {
    suspend fun execute(): ClassesResponse {
        return remoteRepository.getClasses()
        /*
        return coroutineScope {
            val x = async(Dispatchers.IO) {
                remoteRepository.getClasses()
            }
            x.await()
        }
        */
    }
}