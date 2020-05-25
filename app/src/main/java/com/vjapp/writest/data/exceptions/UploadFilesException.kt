package com.vjapp.writest.data.exceptions

class UploadFilesException(cause: Throwable?) : Exception(cause) {
    constructor() : this(null)
}