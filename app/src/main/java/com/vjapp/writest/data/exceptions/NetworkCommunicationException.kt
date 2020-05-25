package com.vjapp.writest.data.exceptions

class NetworkCommunicationException(cause: Throwable?) : Exception(cause) {
    constructor() : this(null)
}