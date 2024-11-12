package com.example.yandex_to_do_app.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.model.TodoListResponse
import com.example.yandex_to_do_app.repository.ToDoItemRepositoryImp
import com.example.yandex_to_do_app.ui.theme.Importance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ToDoViewModel : ViewModel() {
    private val repository = ToDoItemRepositoryImp()
    val todoItems = mutableStateOf(repository.getAllToDoItems().toList())

    private val _toDoList = MutableStateFlow<List<TodoListResponse.TodoItemResponse>>(emptyList())
    val toDoList = _toDoList.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun getToDoItems() {
        viewModelScope.launch {
            val result = repository.fetchToDoList()
            result.onSuccess {
                _toDoList.value = it.list
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun addToDoItem(text: String, importance: Importance, deadline: Date?) {
        val newItem = ToDoItem(
            id = todoItems.value.size,
            text = text,
            importance = importance,
            deadline = deadline,
            isCompleted = false,
            createdAt = Date(),
            modifiedAt = null
        )
        repository.addToDoItem(newItem)
        todoItems.value = repository.getAllToDoItems().toList()
    }

    fun updateToDoItem(item: ToDoItem) {
        repository.updateToDoItem(item)
        todoItems.value = repository.getAllToDoItems().toList()
    }

    fun updateItemCompletionStatus(item: ToDoItem, completionStatus: Boolean) {
        repository.updateItemCompletionStatus(item, completionStatus)
        todoItems.value = repository.getAllToDoItems().toList()
    }

    fun getItemById(itemId: Int): ToDoItem? {
        return repository.getItemById(itemId)
    }

    fun deleteToDoItemById(id: Int) {
        repository.deleteToDoItemById(id)
        todoItems.value = repository.getAllToDoItems().toList()
    }

    val completedItemCount: Int
        get() = todoItems.value.count { it.isCompleted }

    val appDateFormat :SimpleDateFormat
        get() = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

    fun getFormattedDeadline(date: Date?) : String
    {
        if(date != null) return appDateFormat.format(date)
        return ""
    }
}