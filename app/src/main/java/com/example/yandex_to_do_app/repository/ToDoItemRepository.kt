package com.example.yandex_to_do_app.repository

import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.ui.theme.Importance
import java.time.LocalDate
import java.util.Date

interface ToDoItemRepository {
    fun getItemById(userId: Int): ToDoItem?
    fun getAllToDoItems(): MutableList<ToDoItem>
    fun addToDoItem(item: ToDoItem)
    fun deleteToDoItem(item: ToDoItem)
    fun updateItem(item:  ToDoItem)
}

class ToDoRepository() : ToDoItemRepository
{
    private val items = mutableListOf(
        ToDoItem(0, "Купить что-то",
            Importance.None, Date(),
           false ,Date(), Date()),

        ToDoItem(1, "Купить что-то, где-то, зачем-то, но зачем не очень понятно",
            Importance.Low, Date(),
            false , Date(), Date()),

        ToDoItem(2, "Купить что-то, где-то",
            Importance.High, Date(),
            false , Date(), Date()),

        ToDoItem(3, "Купить что-то, где-то, зачем-то, но зачем не очень понятно",
            Importance.None, Date(),
            true , Date(), Date()),

        ToDoItem(4, "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрабатываются большое количество слов",
            Importance.None, Date(),
            true , Date(), Date()),

    )

    override fun getItemById(userId: Int): ToDoItem? {
        return items.get(userId)
    }

    override fun getAllToDoItems(): MutableList<ToDoItem> {
        return items
    }

    override fun addToDoItem(item: ToDoItem) {
        items.add(item)
    }

    override fun deleteToDoItem(item: ToDoItem) {
        items.remove(item)
    }

    val activeItemCount: Int
        get() = items.count() {it.isCompleted}

    override fun updateItem(item: ToDoItem) {
        items.set(item.id, ToDoItem(id = item.id, text = item.text, importance = item.importance,
            deadline = item.deadline, isCompleted = item.isCompleted,
            createdAt = item.createdAt, modifiedAt = item.modifiedAt))
    }

    fun updateItemCompletionStatus(value: ToDoItem, completionStatus: Boolean) {
        items.set(value.id, ToDoItem(id = value.id, text = value.text, importance = value.importance,
            deadline = value.deadline, isCompleted = completionStatus,
            createdAt = value.createdAt, modifiedAt = value.modifiedAt))
    }

}