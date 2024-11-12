package com.example.yandex_to_do_app.repository

import android.view.WindowManager.BadTokenException
import com.example.yandex_to_do_app.interfaces.ToDoApiService
import com.example.yandex_to_do_app.interfaces.ToDoItemRepository
import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.model.TodoListResponse
import com.example.yandex_to_do_app.ui.theme.Importance
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType
import java.util.Date

object RetrofitInstance {
    private const val BASE_URL = "https://beta.mrdekk.ru/todo/"
    private const val AUTH_TOKEN = "Bearer Eldarion"

    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient = OkHttpClient.Builder()
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
    private val items = mutableListOf<ToDoItem>(
        ToDoItem(
            0,
            "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрабатываются большое количество слов",
            Importance.None,
            Date(),
            false,
            Date(),
            Date()
        ),

        ToDoItem(
            1, "Купить что-то, где-то, зачем-то, но зачем не очень понятно", Importance.Low, Date(),
            true, Date(), Date()
        ),

        ToDoItem(
            2,
            "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрабатываются большое количество слов",
            Importance.High,
            Date(),
            true,
            Date(),
            Date()
        ),

        ToDoItem(
            3, "Купить что-то", Importance.None, Date(),
            false, Date(), Date()
        ),

        ToDoItem(
            4, "Купить что-то, где-то, зачем-то, но зачем не очень понятно", Importance.Low, Date(),
            false, Date(), Date()
        ),

        ToDoItem(
            5,
            "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрабатываются большое количество слов",
            Importance.High,
            Date(),
            false,
            Date(),
            Date()
        ),

        ToDoItem(
            6, "Купить что-то", Importance.None, Date(),
            false, Date(), Date()
        ),

        ToDoItem(
            7, "Купить что-то, где-то, зачем-то, но зачем не очень понятно", Importance.Low, Date(),
            true, Date(), Date()
        ),

        ToDoItem(
            8,
            "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрабатываются большое количество слов",
            Importance.High,
            Date(),
            true,
            Date(),
            Date()
        ),

        ToDoItem(
            9, "Купить что-то", Importance.None, Date(),
            false, Date(), Date()
        )
    )

    private val apiService = RetrofitInstance.api

    suspend fun fetchToDoList(): Result<TodoListResponse> {
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

    override fun getItemById(userId: Int): ToDoItem? {
        return items.find { it.id == userId }
    }

    override fun getAllToDoItems(): MutableList<ToDoItem> {
        return items
    }

    override fun addToDoItem(item: ToDoItem) {
        items.add(item)
    }

    override fun deleteToDoItemById(id: Int) {
        val removedItem = getItemById(id)
        if (removedItem != null) {
            items.remove(removedItem)
        }
        items.remove(removedItem)
    }

    override fun updateToDoItem(item: ToDoItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1)
        {
            items[index] = item.copy(modifiedAt = Date())
        }
    }

    fun updateItemCompletionStatus(item: ToDoItem, completionStatus: Boolean) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1)
        {
            items[index] = item.copy(modifiedAt = Date(), isCompleted = completionStatus)
        }
    }
}