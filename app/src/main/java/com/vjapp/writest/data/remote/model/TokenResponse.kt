package com.vjapp.writest.data.remote.model

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("token")
    val token:String
)