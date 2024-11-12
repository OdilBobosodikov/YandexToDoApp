package com.example.yandex_to_do_app.interfaces

import com.example.yandex_to_do_app.model.TodoListResponse
import retrofit2.Response
import retrofit2.http.GET

interface ToDoApiService {
    @GET("list")
    suspend fun getToDoList(): TodoListResponse
}