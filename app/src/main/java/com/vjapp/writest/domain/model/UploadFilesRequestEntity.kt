package com.vjapp.writest.domain.model

data class UploadFilesRequestEntity(
    val fileImgPath: String,
    val fileVideoPath: String,
    val token: String
)
