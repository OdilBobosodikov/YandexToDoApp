package com.example.yandex_to_do_app.interfaces

import com.example.yandex_to_do_app.model.TodoListResponse
import com.example.yandex_to_do_app.model.TodoOneItemResponse
import com.example.yandex_to_do_app.model.TodoPostPutDeleteItemRequest
import com.example.yandex_to_do_app.model.UpdateListRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ToDoApiService {
    @GET("list")
    suspend fun getToDoList(): TodoListResponse

    @GET("list/{id}")
    suspend fun getItemById(@Path("id") id: String): TodoOneItemResponse

    @POST("list")
    suspend fun postItem(
        @Body todo: TodoPostPutDeleteItemRequest,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int
    ): TodoPostPutDeleteItemRequest

    @PUT("list/{id}")
    suspend fun updateItemById(
        @Path("id") id: String,
        @Body todo: TodoPostPutDeleteItemRequest,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int
    ): TodoPostPutDeleteItemRequest

    @DELETE("list/{id}")
    suspend fun deleteItemById(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int
    ): TodoPostPutDeleteItemRequest

    @PATCH("list")
    suspend fun updateList(
        @Body items: UpdateListRequest,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int
    ): TodoListResponse
}