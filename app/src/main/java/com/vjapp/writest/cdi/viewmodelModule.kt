package com.vjapp.writest.cdi

import com.vjapp.writest.presentation.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {

    viewModel {
        UploadFilesViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            androidContext()
        )
    }

    viewModel {TestListViewModel(get()) }

    viewModel { TestDetailViewModel(get(),get()) }

    viewModel { NotificationViewModel(get()) }

    viewModel { MainViewModel(get()) }



}
