package com.vjapp.writest.data.setup

import com.vjapp.writest.data.remote.AppService

class AppServiceFactory(private val httpClientFactory: HttpClientFactory) {

    fun getAppService(serviceFactory : ServiceFactory): AppService {
        val ariaHttpClient = httpClientFactory.abstractClient.newBuilder()
            //.addInterceptor(NetworkModule.createHeadersAriaInterceptor())
            .build()
        val appService = serviceFactory.retrofitInstance.newBuilder().client(ariaHttpClient).build()
        return appService.create(AppService::class.java)
    }

}

