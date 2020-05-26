package com.vjapp.writest.domain.interctor

import com.vjapp.writest.data.model.Resphttpbin
import com.vjapp.writest.data.mpper.UploadFilesMapper
import com.vjapp.writest.domain.IRepository
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.domain.model.UploadFilesResponseEntity
import retrofit2.Call

class UseCaseLoginautenthication(private val remoteRepository: IRepository) {
    suspend fun execute(params:Params): Boolean {
        return remoteRepository.loginAuthentication(params.username,params.password)
    }
    class Params(val username: String, val password: String)
}