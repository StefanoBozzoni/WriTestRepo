package com.vjapp.writest.data.model


import com.google.gson.annotations.SerializedName

data class ClassesResponse(
    @SerializedName("classi")
    val classList: List<ClassResponseObj>
)

data class ClassResponseObj(
    @SerializedName("tipo")
    val type: String
)