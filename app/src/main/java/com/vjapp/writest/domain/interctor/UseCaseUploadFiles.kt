package com.vjapp.writest.domain.interctor

import com.vjapp.writest.data.mapper.UploadFilesMapper
import com.vjapp.writest.domain.IRepository
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.domain.model.UploadFilesResponseEntity

class UseCaseUploadFiles(private val remoteRepository: IRepository) {
    suspend fun execute(params:Params): UploadFilesResponseEntity {
        val x= remoteRepository.uploadFilesToServer(UploadFilesMapper.mapToModel(params.uploadfilesrequest))
        return UploadFilesMapper.mapToEntity(x)
    }
    class Params(val uploadfilesrequest: UploadFilesRequestEntity)
}