package com.vjapp.writest.domain.utils

import com.google.android.gms.tasks.Task
import java.lang.reflect.TypeVariable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

const val DATETIME_ITALIAN_FORMAT: String = "dd/MM/yyyy HH:mm:ss"
const val DATE_ITALIAN_FORMAT: String = "dd/MM/yyyy"

fun Date.toFullString(pattern: String = DATETIME_ITALIAN_FORMAT): String {
    val format = SimpleDateFormat(pattern, Locale.ITALIAN)
    return format.format(this)
}

fun String.toDate(pattern: String = "dd/MM/yyyy"): Date {
    val format = SimpleDateFormat(pattern, Locale.ITALIAN)
    return format.parse(this)
}

//to pass from a Firebase / google Task to a suspending function
suspend fun <T> Task<T>.await(): T = suspendCoroutine { continuation ->
    //addOnSuccessListener(continuation::resume)
    //addOnFailureListener(continuation::resumeWithException)
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(this.result!!)
        } else {
            continuation.resumeWithException(this.exception ?: RuntimeException("Unknown task exception"))
        }
    }

    /*
    addOnFailureListener {
        continuation.resumeWithException(this.exception ?: RuntimeException("Unknown task exception"))
    }
    addOnSuccessListener {
        continuation.resume(this.result!!)
    }
   */
}
