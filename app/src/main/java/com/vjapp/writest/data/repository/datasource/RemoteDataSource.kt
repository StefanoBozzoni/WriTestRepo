package com.vjapp.writest.data.repository.datasource

import com.vjapp.writest.data.exceptions.NetworkCommunicationException
import com.vjapp.writest.data.exceptions.UploadFilesException
import com.vjapp.writest.data.model.UploadFilesRequest
import com.vjapp.writest.data.model.UploadFilesResponse
import com.vjapp.writest.data.remote.AppService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URLConnection

class RemoteDataSource(
    private val appService: AppService
) {
    fun getToken():String {
        val resp =  appService.getToken().execute();
        val result:String

        if (resp.isSuccessful)
           result= resp.body().toString()
        else
           result = ""

        return result
    }

    fun sendFiles():Boolean {
        //call to appService.sendFiles...
        return true
    }

    suspend fun httpBinDemo(): String {
        val result = appService.httpBinGetDemo()
        //Log.d("RESPONSE", response.toString())
        return result.body().toString()
    }

    suspend fun uploadFilesToServer( uploadFilesRequest: UploadFilesRequest): UploadFilesResponse {

        val body1 = getMultiPartFromPath(uploadFilesRequest.fileImgPath)
        val body2 = getMultiPartFromPath(uploadFilesRequest.fileVideoPath)
        val token = RequestBody.create(okhttp3.MultipartBody.FORM, uploadFilesRequest.token)

        //val token = uploadContractServiceRequest.token
        val response = appService.uploadFilesToServer(body1, body2, token)

        if (response.isSuccessful) {
            val responseBody = response.body()
            responseBody?.apply {
                when (esito) {
                    "OK" -> return this
                    else -> throw UploadFilesException()
                }
            }
        }
        throw NetworkCommunicationException()
    }

    fun getMultiPartFromPath(filepath: String): MultipartBody.Part {
        val file = File(filepath)
        val fileNameMap = URLConnection.getFileNameMap()
        val mimeType = fileNameMap.getContentTypeFor(file.name)
        val requestFile = RequestBody.create(MediaType.parse(mimeType), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return body
    }

}