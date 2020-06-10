package com.vjapp.writest.cdi

import com.vjapp.writest.presentation.NotificationViewModel
import com.vjapp.writest.presentation.UploadFilesViewModel
import com.vjapp.writest.presentation.TestDetailViewModel
import com.vjapp.writest.presentation.TestListViewModel
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
            androidContext()
        )
    }

    viewModel {TestListViewModel(get()) }

    viewModel { TestDetailViewModel(get()) }

    viewModel { NotificationViewModel(get()) }

}
