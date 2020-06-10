package com.vjapp.writest.data.remote.model


import com.google.gson.annotations.SerializedName

data class EsitoResponse(
    @SerializedName("esito")
    val esito: String
)