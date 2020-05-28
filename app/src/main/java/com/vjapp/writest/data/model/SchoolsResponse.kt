package com.vjapp.writest.data.model


import com.google.gson.annotations.SerializedName

data class SchoolsResponse(
    @SerializedName("scuole")
    val schoolList: List<SchoolResponseObj>
)

data class SchoolResponseObj(
    @SerializedName("codiceScuola")
    val codSchool: String,
    @SerializedName("denominazione")
    val nameSchool: String
)