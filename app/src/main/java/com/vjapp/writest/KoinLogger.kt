package com.vjapp.writest

import android.util.Log
import org.koin.log.Logger

class KoinLogger : Logger {
    override fun debug(msg: String) {
        Log.d("KOIN",msg)
    }

    override fun err(msg: String) {
        Log.e("KOIN",msg)
    }

    override fun info(msg: String) {
        Log.i("KOIN",msg)
    }
}