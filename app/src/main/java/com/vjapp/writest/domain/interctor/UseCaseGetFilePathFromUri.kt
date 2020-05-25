package com.vjapp.writest.domain.interctor

import android.net.Uri
import com.vjapp.writest.domain.IRepository
import java.io.File

class UseCaseGetFilePathFromUri(private val remoteRepository: IRepository) {
    fun execute(uri: Uri, outPutFileName: String): File? {
        return remoteRepository.getFilePathFromUri(uri,outPutFileName)
    }
}