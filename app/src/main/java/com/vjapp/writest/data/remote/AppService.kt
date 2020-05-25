package com.vjapp.writest.data.remote

import com.vjapp.writest.data.model.Resphttpbin
import com.vjapp.writest.data.model.UploadFilesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AppService {

    @GET("getToken/echo")
    fun getToken(): Call<String>

    @POST("sendfiles")
    fun sendFiles(@Body request:String): Call<String>

    @GET("https://httpbin.org/get")
    suspend fun httpBinGetDemo(): Response<Resphttpbin>

    /*
    @GET("https://httpbin.org/get")
    fun httpBinGetDemo(): Call<Resphttpbin>
    */

    @Multipart
    @POST("https://localhost:8080/inviofiles")
    suspend fun uploadFilesToServer(
        @Part fileImg   : MultipartBody.Part,
        @Part fileVideo : MultipartBody.Part,
        @Part("Token") token: RequestBody
    ): Response<UploadFilesResponse>
}