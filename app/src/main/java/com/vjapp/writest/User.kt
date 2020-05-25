package com.vjapp.writest

data class User(
    val name: String,
    val avatar: String,
    val friends: List<User> = emptyList()
)