package com.vjapp.writest

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vjapp.writest.cdi.domainModule
import com.vjapp.writest.cdi.repositoryModule
import com.vjapp.writest.cdi.viewModelModule
import localModule
import org.koin.android.ext.android.startKoin
import remoteModule

class WriTestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().apply { setPersistenceEnabled(true) }

        startKoin(this,listOf(viewModelModule, repositoryModule,
                                           localModule, remoteModule, domainModule),logger= KoinLogger())
    }
}
