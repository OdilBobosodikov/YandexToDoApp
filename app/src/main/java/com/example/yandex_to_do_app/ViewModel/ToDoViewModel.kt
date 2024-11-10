package com.example.yandex_to_do_app.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.repository.ToDoItemRepositoryImp
import com.example.yandex_to_do_app.ui.theme.Importance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ToDoViewModel : ViewModel() {
    private val repository = ToDoItemRepositoryImp()
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