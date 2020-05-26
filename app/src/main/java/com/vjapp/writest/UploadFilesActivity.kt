package com.vjapp.writest

import android.app.Application
import com.vjapp.writest.cdi.domainModule
import com.vjapp.writest.cdi.repositoryModule
import com.vjapp.writest.cdi.viewModelModule
import org.koin.standalone.StandAloneContext.startKoin
import remoteModule

class UploadFilesActivity : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(listOf(viewModelModule, repositoryModule, remoteModule, domainModule),logger= KoinLogger())
    }
}
