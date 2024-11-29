package com.example.yandex_to_do_app.APIHandler

import com.example.yandex_to_do_app.interfaces.ToDoApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
object NetworkModule {
    private const val AUTH_TOKEN = "Bearer Eldarion"
    private const val BASE_URL = "https://hive.mrdekk.ru/todo/"
    private val json = Json { ignoreUnknownKeys = true }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .certificatePinner(
                CertificatePinner.Builder()
                    .add(
                        "hive.mrdekk.ru",
                        "sha256/NYbU7PBwV4y9J67c4guWTki8FJ+uudrXL0a4V4aRcrg="
                    )
                    .build()
            )
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("Authorization", AUTH_TOKEN)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    fun provideToDoApiService(retrofit: Retrofit): ToDoApiService {
        return retrofit.create(ToDoApiService::class.java)
    }
}
