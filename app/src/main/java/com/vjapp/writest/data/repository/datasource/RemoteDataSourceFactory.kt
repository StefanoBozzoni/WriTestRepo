package com.vjapp.writest.data.repository.datasource

class RemoteDataSourceFactory(private val remoteDataSource: RemoteDataSource) {
    fun retrieveRemoteDataSource() = remoteDataSource
}