package com.example.yandex_to_do_app.repository

import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.ui.theme.Importance
import java.time.LocalDate
import java.util.Date

interface ToDoItemRepository {
    fun getItemById(userId: Int): ToDoItem?
    fun getAllToDoItems(): List<ToDoItem>
    fun addToDoItem(item: ToDoItem)
    fun deleteToDoItem(item: ToDoItem)
}

class ToDoRepository() : ToDoItemRepository
{
    private val items : MutableList<ToDoItem> = mutableListOf(
        ToDoItem(1, "Купить что-то", Importance.Medium, Date(2024, 4, 30),
            false , Date(2024, 4, 25), Date(2024, 4, 30)),

        ToDoItem(2, "Купить что-то, где-то, зачем-то, но зачем не очень понятно", Importance.Low, Date(2023, 4, 30),
            false , Date(2024, 4, 20), Date(2024, 4, 20)),

        ToDoItem(3, "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрабатываются большое количество слов",
            Importance.High, Date(2024, 4, 30),
            false , Date(2024, 4, 28), Date(2024, 4, 28)),

        ToDoItem(4, "Купить что-то", Importance.Medium, Date(2024, 11, 8),
            false , Date(2024, 11, 1), Date(2024, 11, 1))
    )

    override fun getItemById(userId: Int): ToDoItem? {
        return items.get(userId)
    }

    override fun getAllToDoItems(): List<ToDoItem> {
        return items
    }

    override fun addToDoItem(item: ToDoItem) {
        items.add(item)
    }

    override fun deleteToDoItem(item: ToDoItem) {
        items.remove(item)
    }

    fun isDeadlineAlmostOver(item: ToDoItem) : Boolean
    {
        val current = LocalDate.now()

        val monthDiff = current.monthValue- item.deadline.month
        val dayDiff =  current.dayOfMonth- item.deadline.day
        val yearDiff = current.year - item.deadline.year

        if(monthDiff == 0 && yearDiff == 0 && dayDiff < 7)
        {
            return true
        }

        return false
    }

}