package com.example.yandex_to_do_app.model

import com.example.yandex_to_do_app.ui.theme.Importance
import java.util.Date


data class ToDoItem(
    val id: Int,
    val text: String,
    val importance: Importance,
    val deadline: Date,
    var isCompleted: Boolean,
    val createdAt: Date,
    val modifiedAt: Date
)
