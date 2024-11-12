package com.example.yandex_to_do_app.ViewModel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandex_to_do_app.model.TodoListResponse
import com.example.yandex_to_do_app.model.TodoPostPutDeleteItemRequest
import com.example.yandex_to_do_app.model.UpdateListRequest
import com.example.yandex_to_do_app.repository.ToDoItemRepositoryImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ToDoViewModel : ViewModel() {
    private val repository = ToDoItemRepositoryImp()

    private val _toDoList = MutableStateFlow<List<TodoListResponse.TodoItemResponse>>(emptyList())
    val toDoList = _toDoList.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun getToDoItems() {
        viewModelScope.launch {
            val result = repository.getAllToDoItems()
            result.onSuccess {
                _toDoList.value = it.list
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun updateItemById(id: String, todoPostPutDeleteItemRequest: TodoPostPutDeleteItemRequest)
    {
        viewModelScope.launch {
            val result = repository.updateToDoItemById(id, todoPostPutDeleteItemRequest)
            result.onSuccess {
                val items = repository.getAllToDoItems()
                items.onSuccess {
                    _toDoList.value = it.list
                }
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun getItemById(toDoItemId: String,
                    onResult: (TodoListResponse.TodoItemResponse?) -> Unit) {
        viewModelScope.launch {
            repository.getItemById(toDoItemId).onSuccess {
                onResult(it.element)
            }.onFailure {
                _errorMessage.value = it.message
                onResult(null)
            }
        }
    }

    fun deleteToDoItemById(itemId: String) {
        viewModelScope.launch {
            val result = repository.deleteToDoItemById(itemId)
            result.onSuccess {
                val items = repository.getAllToDoItems()
                items.onSuccess {
                    _toDoList.value = it.list
                }
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }





    val completedItemCount: Int
        get() = _toDoList.value.count { it.done }

    val appDateFormat :SimpleDateFormat
        get() = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

    fun getFormattedDeadline(date: Date?) : String
    {
        if(date != null) return appDateFormat.format(date)
        return ""
    }


    fun postToDoItem(text: String, importance: String, deadline: Date?) {
        viewModelScope.launch {
            val result = repository.addToDoItem(TodoPostPutDeleteItemRequest("ok",
                TodoListResponse.TodoItemResponse(UUID.randomUUID().toString(), text = text,
                    importance = importance, deadline = deadline?.time, done = false, createdAt = Date().time,
                    changedAt = Date().time, lastUpdatedBy = "qwe")))
            result.onSuccess {
                val items = repository.getAllToDoItems()
                items.onSuccess {
                    _toDoList.value = it.list
                }
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

}