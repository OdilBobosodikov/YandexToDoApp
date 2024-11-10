package com.example.yandex_to_do_app.repository

import com.example.yandex_to_do_app.interfaces.ToDoItemRepository
import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.ui.theme.Importance
import java.util.Date

class ToDoItemRepositoryImp() : ToDoItemRepository {
    private val items = mutableListOf<ToDoItem>(
        ToDoItem(
            0,
            "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрабатываются большое количество слов",
            Importance.None,
            Date(),
            false,
            Date(),
            Date()
        ),

        ToDoItem(
            1, "Купить что-то, где-то, зачем-то, но зачем не очень понятно", Importance.Low, Date(),
            true, Date(), Date()
        ),

        ToDoItem(
            2,
            "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрабатываются большое количество слов",
            Importance.High,
            Date(),
            true,
            Date(),
            Date()
        ),

        ToDoItem(
            3, "Купить что-то", Importance.None, Date(),
            false, Date(), Date()
        ),

        ToDoItem(
            4, "Купить что-то, где-то, зачем-то, но зачем не очень понятно", Importance.Low, Date(),
            false, Date(), Date()
        ),

        ToDoItem(
            5,
            "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрабатываются большое количество слов",
            Importance.High,
            Date(),
            false,
            Date(),
            Date()
        ),

        ToDoItem(
            6, "Купить что-то", Importance.None, Date(),
            false, Date(), Date()
        ),

        ToDoItem(
            7, "Купить что-то, где-то, зачем-то, но зачем не очень понятно", Importance.Low, Date(),
            true, Date(), Date()
        ),

        ToDoItem(
            8,
            "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрабатываются большое количество слов",
            Importance.High,
            Date(),
            true,
            Date(),
            Date()
        ),

        ToDoItem(
            9, "Купить что-то", Importance.None, Date(),
            false, Date(), Date()
        )
    )

    override fun getItemById(userId: Int): ToDoItem? {
        return items.find { it.id == userId }
    }

    override fun getAllToDoItems(): MutableList<ToDoItem> {
        return items
    }

    override fun addToDoItem(item: ToDoItem) {
        items.add(item)
    }

    override fun deleteToDoItemById(id: Int) {
        val removedItem = getItemById(id)
        if (removedItem != null) {
            items.remove(removedItem)
        }
        items.remove(removedItem)
    }

    override fun updateToDoItem(item: ToDoItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1)
        {
            items[index] = item.copy(modifiedAt = Date())
        }
    }

    fun updateItemCompletionStatus(item: ToDoItem, completionStatus: Boolean) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1)
        {
            items[index] = item.copy(modifiedAt = Date(), isCompleted = completionStatus)
        }
    }
}