package com.vjapp.writest.domain

import android.net.Uri
import com.vjapp.writest.data.model.UploadFilesRequest
import com.vjapp.writest.data.model.UploadFilesResponse
import java.io.File

interface IRepository {
    fun getToken():String
    fun sendFiles():Boolean
    suspend fun httpBinGetDemo(): String
    suspend fun uploadFilesToServer(params: UploadFilesRequest): UploadFilesResponse
    fun getFileNameFromCursor(uri: Uri): Pair<String?, Long>
    fun getFilePathFromUri(uri: Uri, outPutFileName: String): File?
    suspend fun loginAuthentication(username: String, password: String):Boolean
    suspend fun userRegistration(username: String, password: String):Boolean
}