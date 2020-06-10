package com.vjapp.writest.domain.interctor

import com.vjapp.writest.domain.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class UseCaseUserRegistration(private val remoteRepository: IRepository) {
    suspend fun execute(params:Params): Boolean {
        return coroutineScope {
            val isRegistered = async<Boolean>(Dispatchers.IO) {
                remoteRepository.userRegistration(
                    params.username,
                    params.password
                )
            }
            isRegistered.await()
        }
    }
    class Params(val username: String, val password: String)
}