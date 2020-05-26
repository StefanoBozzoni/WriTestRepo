package com.vjapp.writest.domain.interctor

import com.vjapp.writest.domain.IRepository

class UseCaseUserRegistration(private val remoteRepository: IRepository) {
    suspend fun execute(params:Params): Boolean {
        return remoteRepository.userRegistration(params.username,params.password)
    }
    class Params(val username: String, val password: String)
}