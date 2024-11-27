package com.example.yandex_to_do_app.repository

import android.view.WindowManager.BadTokenException
import com.example.yandex_to_do_app.interfaces.ToDoApiService
import com.example.yandex_to_do_app.interfaces.ToDoItemRepository
import com.example.yandex_to_do_app.model.TodoListResponse
import com.example.yandex_to_do_app.model.TodoOneItemResponse
import com.example.yandex_to_do_app.model.TodoPostPutDeleteItemRequest
import com.example.yandex_to_do_app.model.UpdateListRequest
import jakarta.inject.Inject

class ToDoItemRepositoryImp @Inject constructor(
    private val apiService: ToDoApiService
) : ToDoItemRepository {

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

    override suspend fun addToDoItem(
        todoPostPutDeleteItemRequest: TodoPostPutDeleteItemRequest,
        revision: Int
    ): Result<TodoPostPutDeleteItemRequest> {
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

    override suspend fun deleteToDoItemById(
        id: String,
        revision: Int
    ): Result<TodoPostPutDeleteItemRequest> {
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
        revision: Int
    ): Result<TodoPostPutDeleteItemRequest> {
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

    override suspend fun updateList(
        updateListRequest: UpdateListRequest,
        revision: Int
    ): Result<TodoListResponse> {
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