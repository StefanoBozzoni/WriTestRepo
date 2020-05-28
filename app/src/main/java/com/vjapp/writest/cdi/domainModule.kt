package com.vjapp.writest.cdi

import com.vjapp.writest.domain.IRepository
import com.vjapp.writest.domain.interctor.*
import org.koin.dsl.module.module

val domainModule = module {
    factory { UseCaseGetToken(get()) }
    factory { UseCaseHttpBinDemo(get()) }
    factory { UseCaseGetFilePathFromUri(get()) }
    factory { UseCaseGetFileNameFromCursor(get()) }
    factory { UseCaseUploadFiles(get()) }
    factory { UseCaseGetSchools(get()) }
    factory { UseCaseGetClasses(get()) }
}
