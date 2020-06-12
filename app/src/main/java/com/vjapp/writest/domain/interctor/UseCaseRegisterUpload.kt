package com.vjapp.writest.domain.interctor

import com.vjapp.writest.data.remote.model.SchoolsResponse
import com.vjapp.writest.domain.IRepository

class UseCaseRegisterUpload(private val remoteRepository: IRepository) {
    fun execute(params:Params) {
        return remoteRepository.registerUpload(params.token)
    }
    class Params(val token:String)
}