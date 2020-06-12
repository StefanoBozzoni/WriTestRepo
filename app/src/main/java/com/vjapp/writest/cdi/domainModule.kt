package com.vjapp.writest.cdi

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
    factory { UseCaseSaveTest(get()) }
    factory { UseCaseGetTests(get()) }
    factory { UseCaseGetSingleTest(get()) }
    factory {UseCaseInitializeFirbaseSubscription(get()) }
    factory { UseCaseSynchLocalDB(get()) }
    factory { UseCaseAddMessageToQueue(get()) }
    factory { UseCaseRegisterUpload(get()) }
}
