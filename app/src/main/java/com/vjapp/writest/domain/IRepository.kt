package com.vjapp.writest.domain

import android.net.Uri
import com.vjapp.writest.data.remote.model.ClassesResponse
import com.vjapp.writest.data.remote.model.SchoolsResponse
import com.vjapp.writest.data.remote.model.UploadFilesRequest
import com.vjapp.writest.data.remote.model.UploadFilesResponse
import com.vjapp.writest.domain.model.ClassesEntity
import com.vjapp.writest.domain.model.TestEntity
import java.io.File

interface IRepository {
    suspend fun getToken(): String
    fun sendFiles(): Boolean
    suspend fun httpBinGetDemo(): String
    suspend fun uploadFilesToServer(params: UploadFilesRequest): UploadFilesResponse
    fun getFileNameFromCursor(uri: Uri): Pair<String?, Long>
    fun getFilePathFromUri(uri: Uri, outPutFileName: String): File?
    suspend fun loginAuthentication(username: String, password: String): Boolean
    suspend fun userRegistration(username: String, password: String): Boolean
    suspend fun getSchools(): SchoolsResponse
    suspend fun getClasses(): ClassesEntity
    suspend fun getTests() : List<TestEntity>
    suspend fun getTestsFromRemote() : List<TestEntity>
    suspend fun getSingleTest(id:Int) : TestEntity
    suspend fun getSingleTest(uploadToken:String) : TestEntity
    suspend fun getSingleTestFromRemote(uploadToken:String) : TestEntity
    suspend fun saveTest(t : TestEntity): Long
    suspend fun getFireBaseToken() : String
    suspend fun syncDB() : Boolean
    fun registerUpload(token:String)
    suspend fun addDiagnosisToQueue(token:String,email:String, diagnosis:String)
    suspend fun getDiagnosisUrl(uploadToken:String) : Uri
}