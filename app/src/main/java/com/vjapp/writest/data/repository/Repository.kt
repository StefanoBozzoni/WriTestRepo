package com.vjapp.writest.data.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.vjapp.writest.data.remote.model.SchoolsResponse
import com.vjapp.writest.data.remote.model.UploadFilesRequest
import com.vjapp.writest.data.remote.model.UploadFilesResponse
import com.vjapp.writest.data.repository.datasource.LocalDataSource
import com.vjapp.writest.data.repository.datasource.RemoteDataSource
import com.vjapp.writest.domain.IRepository
import com.vjapp.writest.domain.interctor.UseCaseInitializeFirbaseSubscription
import com.vjapp.writest.domain.mapper.DatabaseMapper
import com.vjapp.writest.domain.mapper.ServiceMapper
import com.vjapp.writest.domain.model.ClassesEntity
import com.vjapp.writest.domain.model.TestEntity
import com.vjapp.writest.domain.utils.await
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileOutputStream

class Repository(private val remoteDataSource: RemoteDataSource,
                 private val localDataSource : LocalDataSource,
                 private val context: Context): IRepository {

    override suspend fun getToken(): String {
        return remoteDataSource.getToken()
    }
    override fun sendFiles(): Boolean {
        return remoteDataSource.sendFiles()
    }

    override suspend fun httpBinGetDemo(): String {
        return remoteDataSource.httpBinDemo()
    }

    override suspend fun uploadFilesToServer(params: UploadFilesRequest): UploadFilesResponse {
        return remoteDataSource.uploadFilesToServer(params)
    }

    override fun getFileNameFromCursor(uri: Uri): Pair<String?, Long> {
        var fileName: String? = null
        var fileSize: Long = 0
        var fileCursor: Cursor? = null

        if (uri.toString().startsWith("content://")) {
            try {
                fileCursor = context.contentResolver
                    .query(
                        uri,
                        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
                        null,
                        null,
                        null
                    )
                if (fileCursor != null && fileCursor.moveToFirst()) {
                    val cIndex = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cIndex != -1) {
                        fileName = fileCursor.getString(cIndex)
                        fileSize =
                            fileCursor.getLong(fileCursor.getColumnIndex(OpenableColumns.SIZE))
                    }
                }
            } finally {
                fileCursor?.close()
            }
        }

        if (uri.toString().startsWith("file://")) {
            fileName = File(uri.toString()).name
            fileSize = File(uri.toString()).length()
        }
        return Pair(fileName, fileSize)
    }

    override fun getFilePathFromUri(uri: Uri, outPutFileName: String): File? {
        val file: File?

        if (uri.toString().startsWith("content://")) {
            //val fileName: String = this.getFileNameFromCursor(uri).first!!
            val outputDir: File = context.cacheDir

            file = File.createTempFile(
                File(outPutFileName).nameWithoutExtension,
                "."+File(outPutFileName).absoluteFile.extension,
                outputDir
            )

            FileOutputStream(file).use { outputStream ->
                context.contentResolver.openInputStream(uri).use { inputStream ->
                    inputStream?.copyTo(outputStream)
                }
                outputStream.flush()
            }
        }
        else if (uri.toString().startsWith("file://")) {
            file = File(uri.path)
        }
        else throw Exception("Invalid file uri type in getFilePathFromUri")

        return file
    }

    override suspend fun loginAuthentication(username: String, password: String):Boolean {
        return remoteDataSource.loginAuthentication(username,password)
    }

    override suspend fun userRegistration(username: String, password: String):Boolean {
        return remoteDataSource.userRegistration(username, password)
    }

    override suspend fun getSchools(): SchoolsResponse {
        /* mock schools
        val raw: InputStream = context.resources.openRawResource(R.raw.schools)
        val rd: Reader = BufferedReader(InputStreamReader(raw))
        val gson = Gson()
        return gson.fromJson(rd, SchoolsResponse::class.java)
        */
        return remoteDataSource.getSchools()
    }

    override suspend fun getClasses(): ClassesEntity {
        /* mock classes
        val raw: InputStream = context.resources.openRawResource(R.raw.classes)
        val rd: Reader = BufferedReader(InputStreamReader(raw))
        val gson = Gson()
        return gson.fromJson(rd, ClassesResponse::class.java)
        */
        return ClassesEntity(remoteDataSource.getClasses().classList.map { el -> ServiceMapper.mapToEntity(el) })
    }

    override suspend fun getTests(): List<TestEntity> {
        return localDataSource.getTests().map {el -> DatabaseMapper.mapToEntity(el)}
    }

    override suspend fun getTestsFromRemote(): List<TestEntity> {
        return localDataSource.getTestsFromRemote().map {el -> DatabaseMapper.mapToEntity(el)}
    }

    override suspend fun getSingleTest(id:Int) : TestEntity {
        return DatabaseMapper.mapToEntity(localDataSource.getTest(id)!!)
    }

    override suspend fun getSingleTest(uploadToken:String) : TestEntity {
        val id = localDataSource.findIdFromToken(uploadToken)
        return DatabaseMapper.mapToEntity(localDataSource.getTest(id)!!)
    }

    override suspend fun getSingleTestFromRemote(uploadToken:String) : TestEntity {
        return DatabaseMapper.mapToEntity(localDataSource.getTestFromRemote(uploadToken)!!)
    }

    override suspend fun saveTest(t : TestEntity): Long {
        return localDataSource.saveTest(DatabaseMapper.mapToModel(t))
    }

    override suspend fun getFireBaseToken(): String {
        val instanceResult = FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(UseCaseInitializeFirbaseSubscription.TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token
                // Log and toast
                val msg = "il nuovo token è ${token}"
                Log.d(UseCaseInitializeFirbaseSubscription.TAG, msg)
                val database = Firebase.database.reference
                val user = Firebase.auth.currentUser
                database.child("users").child(user?.email!!.replace(".",",").toString()).child("token").setValue(token)
            }).await()
        return instanceResult.token

        return ""
    }

    override suspend fun syncDB() : Boolean {
        try {
            coroutineScope {
                localDataSource.deleteTests()
                val cachedTests = localDataSource.getTestsFromRemote()
                localDataSource.insertAllTests(cachedTests)
            }
            return true
        } catch (e:Throwable) {
          return false
        }
    }

    override fun registerUpload(token:String) {
        localDataSource.registerUpload(token)
    }

    override suspend fun addDiagnosisToQueue(token:String,email:String, diagnosis:String) {
        localDataSource.addDiagnosisToQueue(token,email, diagnosis)
    }

    override suspend fun getDiagnosisUrl(uploadToken: String):Uri {
        return localDataSource.getDiagnosisUrl(uploadToken)
    }

}