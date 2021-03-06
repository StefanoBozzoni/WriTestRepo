package com.vjapp.writest.data.remote

import com.vjapp.writest.data.remote.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AppService {

    @GET("https://us-central1-writest-f1f05.cloudfunctions.net/getToken")
    suspend fun getToken(): Response<TokenResponse>

    @POST("sendfiles")
    fun sendFiles(@Body request:String): Call<String>

    //Demo api, just to see if retrofit is working
    @GET("https://httpbin.org/get")
    suspend fun httpBinGetDemo(): Response<Resphttpbin>

    /* without suspending functions
    @GET("https://httpbin.org/get")
    fun httpBinGetDemo(): Call<Resphttpbin>
    */
    @GET("https://writest-f1f05.firebaseio.com/tabellaScuole.json")
    suspend fun getSchools(): Response<SchoolsResponse>

    @GET("https://writest-f1f05.firebaseio.com/tabellaClassi.json")
    suspend fun getClasses(): Response<ClassesResponse>

    @Multipart
    @POST("https://localhost:8080/inviofiles")
    suspend fun uploadFilesToServer(
        @Part fileImg   : MultipartBody.Part,
        @Part fileVideo : MultipartBody.Part,
        @Part("Token") token: RequestBody
    ): Response<UploadFilesResponse>

    @POST("https://localhost:8080/authenticate")
    @FormUrlEncoded
    suspend fun loginAuthentication(
        @Field("username") username : String,
        @Field("password") pswd     : String
    ): Response<EsitoResponse>

    @POST("https://localhost:8080/userRegistration")
    @FormUrlEncoded
    suspend fun userRegistration(
        @Field("username") username : String,
        @Field("password") pswd     : String
    ): Response<EsitoResponse>

}