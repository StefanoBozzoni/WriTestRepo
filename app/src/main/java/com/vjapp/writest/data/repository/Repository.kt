package com.vjapp.writest.data.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.vjapp.writest.data.model.UploadFilesRequest
import com.vjapp.writest.data.model.UploadFilesResponse
import com.vjapp.writest.data.repository.datasource.RemoteDataSource
import com.vjapp.writest.domain.IRepository
import java.io.File
import java.io.FileOutputStream

class Repository(private val remoteDataSource: RemoteDataSource,
                 private val context: Context): IRepository {
    override fun getToken(): String {
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
                fileCursor = context.getContentResolver()
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
        var file: File? = null

        if (uri.toString().startsWith("content://")) {
            val fileName: String = this.getFileNameFromCursor(uri).first!!
            val outputDir: File = context.cacheDir

            file = File.createTempFile(
                File(outPutFileName).nameWithoutExtension,
                File(outPutFileName).absoluteFile.extension,
                outputDir
            )

            FileOutputStream(file).use { outputStream ->
                context.getContentResolver().openInputStream(uri).use { inputStream ->
                    inputStream?.copyTo(outputStream)
                }
                outputStream.flush()
            }
        }

        if (uri.toString().startsWith("content://")) {
            file = File(uri.path)
        }

        return file
    }
}