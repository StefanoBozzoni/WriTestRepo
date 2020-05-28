package com.vjapp.writest

import android.app.Application
import com.vjapp.writest.cdi.domainModule
import com.vjapp.writest.cdi.repositoryModule
import com.vjapp.writest.cdi.viewModelModule
import org.koin.android.ext.android.startKoin
import remoteModule

class UploadFilesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this,listOf(viewModelModule, repositoryModule, remoteModule, domainModule),logger= KoinLogger())
    }
}
