package com.vjapp.writest.cdi

import com.vjapp.writest.data.repository.Repository
import com.vjapp.writest.data.repository.datasource.RemoteDataSourceFactory
import com.vjapp.writest.domain.IRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val repositoryModule = module {

    factory { RemoteDataSourceFactory(get()) }
    single { Repository(get(), androidContext()) as IRepository }

}