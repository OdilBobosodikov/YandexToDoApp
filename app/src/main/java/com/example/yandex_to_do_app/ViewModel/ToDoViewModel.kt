package com.example.yandex_to_do_app.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.repository.ToDoRepository
import com.example.yandex_to_do_app.ui.theme.Importance
import java.util.Date

class ToDoViewModel : ViewModel() {
    private val repository = ToDoRepository()
    val todoItems = mutableStateOf(repository.getAllToDoItems().toList())

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

    fun getItemById(itemId: Int): ToDoItem {
        return repository.getItemById(itemId)
    }

    fun deleteToDoItem(item: ToDoItem) {
        repository.deleteToDoItem(item)
        todoItems.value = repository.getAllToDoItems().toList()
    }

    val completedItemCount: Int
        get() = todoItems.value.count { it.isCompleted }
}