package com.example.yandex_to_do_app.interfaces

import com.example.yandex_to_do_app.APIHandler.NetworkModule
import com.example.yandex_to_do_app.MainActivity
import com.example.yandex_to_do_app.ViewModel.ViewModelModule
import com.example.yandex_to_do_app.repository.RepositoryModule
import dagger.Component

@Component(modules = [NetworkModule::class, RepositoryModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}