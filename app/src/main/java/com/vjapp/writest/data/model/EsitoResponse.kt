package com.vjapp.writest.data.model


import com.google.gson.annotations.SerializedName

data class EsitoResponse(
    @SerializedName("esito")
    val esito: String
)