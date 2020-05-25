package com.vjapp.writest.data.repository.datasource

import com.vjapp.writest.data.model.UploadFilesRequest

class RemoteDataSourceFactory(private val remoteDataSource: RemoteDataSource) {
    fun retrieveRemoteDataSource( uploadRequest: UploadFilesRequest) = remoteDataSource
}