package com.vjapp.writest.data.remote.model

data class UploadFilesRequest(
    val fileImgPath: String,
    val fileVideoPath: String,
    val token: String
)
