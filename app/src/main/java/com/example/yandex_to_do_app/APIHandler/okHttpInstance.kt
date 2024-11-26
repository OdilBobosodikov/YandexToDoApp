package com.example.yandex_to_do_app.APIHandler


import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

object okHttpInstance {

    private const val AUTH_TOKEN = "Bearer Eldarion"
    val okHttpClient : OkHttpClient
        get()
        {
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
}