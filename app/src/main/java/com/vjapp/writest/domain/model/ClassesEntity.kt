package com.vjapp.writest.domain.model

data class ClassesEntity(
    val classList: List<ClassObjEntity>
)

data class ClassObjEntity(
    val type: String
)