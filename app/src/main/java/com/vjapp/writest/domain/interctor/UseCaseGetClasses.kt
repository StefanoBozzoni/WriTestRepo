package com.vjapp.writest.domain.interctor

import com.vjapp.writest.data.remote.model.ClassesResponse
import com.vjapp.writest.domain.IRepository
import com.vjapp.writest.domain.model.ClassesEntity

class UseCaseGetClasses(private val remoteRepository: IRepository) {
    suspend fun execute(): ClassesEntity {
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