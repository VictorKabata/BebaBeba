package com.vickikbt.bebabeba.di

import com.vickikbt.bebabeba.data.repository.AuthRepository
import com.vickikbt.bebabeba.data.repository.RequestRepository
import com.vickikbt.bebabeba.ui.viewmodel.AuthViewModel
import com.vickikbt.bebabeba.ui.viewmodel.RequestViewModel
import org.koin.dsl.module
import org.koin.android.viewmodel.dsl.viewModel


val viewModelModule = module {
    viewModel {
        AuthViewModel(get())
    }
    viewModel {
        RequestViewModel()
    }
}

val repositoryModule = module {
    single {
        AuthRepository()
    }
    single {
        RequestRepository(get())
    }
}
