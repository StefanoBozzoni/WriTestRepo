package com.vjapp.writest.domain.model

data class SchoolsEntity(
    val schoolList: List<SchoolObjEntity>
)

data class SchoolObjEntity(
    val codSchool: String,
    val nameSchool: String
)