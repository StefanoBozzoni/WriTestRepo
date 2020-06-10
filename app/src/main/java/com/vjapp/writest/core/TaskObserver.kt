package com.vjapp.writest.core

abstract class TaskObserver<T> {
    abstract fun onSuccess(value: T)

    abstract fun onError(t: Throwable)
}
