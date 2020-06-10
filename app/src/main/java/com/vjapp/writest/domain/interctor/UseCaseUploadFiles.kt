package com.vjapp.writest.domain.interctor

import com.vjapp.writest.domain.IRepository
import com.vjapp.writest.domain.mapper.ServiceMapper
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.domain.model.UploadFilesResponseEntity

class UseCaseUploadFiles(private val remoteRepository: IRepository) {
    suspend fun execute(params:Params): UploadFilesResponseEntity {
        val x= remoteRepository.uploadFilesToServer(ServiceMapper.mapToModel(params.uploadfilesrequest))
        return ServiceMapper.mapToEntity(x)
    }
    class Params(val uploadfilesrequest: UploadFilesRequestEntity)
}