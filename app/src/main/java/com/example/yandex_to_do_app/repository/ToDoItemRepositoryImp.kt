package com.example.yandex_to_do_app.repository

import android.view.WindowManager.BadTokenException
import com.example.yandex_to_do_app.interfaces.ToDoApiService
import com.example.yandex_to_do_app.interfaces.ToDoItemRepository
import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.model.TodoListResponse
import com.example.yandex_to_do_app.model.TodoOneItemResponse
import com.example.yandex_to_do_app.model.TodoPostPutDeleteItemRequest
import com.example.yandex_to_do_app.model.UpdateListRequest
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType

object RetrofitInstance {
    private const val BASE_URL = "https://hive.mrdekk.ru/todo/"
    private const val AUTH_TOKEN = "Bearer Eldarion"

    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient = OkHttpClient.Builder()
        .certificatePinner(
            CertificatePinner.Builder()
                .add("hive.mrdekk.ru", "sha256/NYbU7PBwV4y9J67c4guWTki8FJ+uudrXL0a4V4aRcrg=")
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

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val api: ToDoApiService by lazy {
        retrofit.create(ToDoApiService::class.java)
    }
}


class ToDoItemRepositoryImp() : ToDoItemRepository {
    private val apiService = RetrofitInstance.api

    override suspend fun getAllToDoItems(): Result<TodoListResponse> {
        return try {
            val response = apiService.getToDoList()
            if (response.status == "ok") {
                Result.success(response)
            } else {
                throw BadTokenException()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToDoItem(todoPostPutDeleteItemRequest: TodoPostPutDeleteItemRequest,
                                     revision: Int): Result<TodoPostPutDeleteItemRequest> {
        return try {
            val response = apiService.postItem(todoPostPutDeleteItemRequest, revision)
            if (response.status == "ok") {
                Result.success(response)
            } else {
                throw BadTokenException()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteToDoItemById(id: String, revision: Int): Result<TodoPostPutDeleteItemRequest> {
        return try {
            val response = apiService.deleteItemById(id, revision)
            if (response.status == "ok") {
                Result.success(response)
            } else {
                throw BadTokenException()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateToDoItemById(
        id: String,
        todoPostPutDeleteItemRequest: TodoPostPutDeleteItemRequest,
        revision: Int): Result<TodoPostPutDeleteItemRequest> {
        return try {
            val response = apiService.updateItemById(id, todoPostPutDeleteItemRequest, revision)
            if (response.status == "ok") {
                Result.success(response)
            } else {
                throw BadTokenException()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateList(updateListRequest: UpdateListRequest, revision: Int): Result<TodoListResponse> {
        return try {
            val response = apiService.updateList(updateListRequest, revision)
            if (response.status == "ok") {
                Result.success(response)
            } else {
                throw BadTokenException()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getItemById(userId: String): Result<TodoOneItemResponse> {
        return try {
            val response = apiService.getItemById(userId)
            if (response.status == "ok") {
                Result.success(response)
            } else {
                throw BadTokenException()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}