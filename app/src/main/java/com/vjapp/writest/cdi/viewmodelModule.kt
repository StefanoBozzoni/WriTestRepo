package com.vjapp.writest.cdi

import com.vjapp.writest.presentation.SendFilesViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel {
        SendFilesViewModel(
            get(),
            get(),
            get()
        )
    }
}
