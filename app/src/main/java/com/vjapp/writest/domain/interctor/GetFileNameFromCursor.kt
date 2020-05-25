package com.vjapp.writest.domain.interctor

import android.net.Uri
import com.vjapp.writest.domain.IRepository

class UseCaseGetFileNameFromCursor(val repository: IRepository) {
    fun execute(uri: Uri): Pair<String?, Long> {
        return repository.getFileNameFromCursor(uri)
    }
}