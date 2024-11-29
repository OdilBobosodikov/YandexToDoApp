package com.example.yandex_to_do_app

import android.app.Application
import com.example.yandex_to_do_app.interfaces.DaggerAppComponent
import com.example.yandex_to_do_app.interfaces.AppComponent


class ToDoApplication : Application() {
    val appComponent : AppComponent by lazy {
        DaggerAppComponent.create()
    }
}