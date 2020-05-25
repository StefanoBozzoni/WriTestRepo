package com.vjapp.writest.cdi

import com.vjapp.writest.domain.interctor.UseCaseGetToken
import com.vjapp.writest.domain.interctor.UseCaseHttpBinDemo
import org.koin.dsl.module.module

val domainModule = module {
    factory { UseCaseGetToken(get()) }
    factory { UseCaseHttpBinDemo(get()) }
}
