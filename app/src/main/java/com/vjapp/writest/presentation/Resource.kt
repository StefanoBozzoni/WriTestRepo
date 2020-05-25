package com.vjapp.writest.presentation

class Resource<out T> constructor(val status: ResourceState, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T): Resource<T> {
            return Resource(ResourceState.SUCCESS, data, null)
        }

        fun <T> error(message: String, data: T? = null): Resource<T> {
            return Resource(ResourceState.ERROR, data, message)
        }

        fun <T> loading(message: String = ""): Resource<T> {
            return Resource(ResourceState.LOADING, null, message)
        }
    }
}