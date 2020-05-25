package com.vjapp.writest.domain.model


import com.google.gson.annotations.SerializedName

data class UploadFilesResponseEntity(
    @SerializedName("esito")
    val esito: String
)