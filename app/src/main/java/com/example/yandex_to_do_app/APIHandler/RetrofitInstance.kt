package com.example.yandex_to_do_app.APIHandler

import com.example.yandex_to_do_app.interfaces.ToDoApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitInstance {
    private const val BASE_URL = "https://hive.mrdekk.ru/todo/"

    private val okHttpClient = okHttpInstance.okHttpClient
    private val json = Json { ignoreUnknownKeys = true }


    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val api: ToDoApiService by lazy {
        retrofit.create(ToDoApiService::class.java)
    }
}
