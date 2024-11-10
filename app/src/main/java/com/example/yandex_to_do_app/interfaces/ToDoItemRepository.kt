package com.example.yandex_to_do_app.interfaces

import com.example.yandex_to_do_app.model.ToDoItem

interface ToDoItemRepository {
    fun getItemById(userId: Int): ToDoItem?
    fun getAllToDoItems(): MutableList<ToDoItem>
    fun addToDoItem(item: ToDoItem)
    fun deleteToDoItemById(id: Int)
    fun updateToDoItem(item: ToDoItem)
}