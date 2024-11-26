package com.example.yandex_to_do_app.interfaces

import com.example.yandex_to_do_app.model.TodoListResponse
import com.example.yandex_to_do_app.model.TodoOneItemResponse
import com.example.yandex_to_do_app.model.TodoPostPutDeleteItemRequest
import com.example.yandex_to_do_app.model.UpdateListRequest

interface ToDoItemRepository {
    suspend fun getItemById(userId: String): Result<TodoOneItemResponse>
    suspend fun getAllToDoItems(): Result<TodoListResponse>
    suspend fun addToDoItem(
        todoPostPutDeleteItemRequest: TodoPostPutDeleteItemRequest,
        revision: Int
    ): Result<TodoPostPutDeleteItemRequest>

    suspend fun deleteToDoItemById(id: String, revision: Int): Result<TodoPostPutDeleteItemRequest>
    suspend fun updateToDoItemById(
        id: String,
        todoPostPutDeleteItemRequest: TodoPostPutDeleteItemRequest,
        revision: Int
    ): Result<TodoPostPutDeleteItemRequest>

    suspend fun updateList(
        updateListRequest: UpdateListRequest,
        revision: Int
    ): Result<TodoListResponse>
}