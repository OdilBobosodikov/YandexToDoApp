package com.example.yandex_to_do_app.model

import com.example.yandex_to_do_app.R
import java.util.Date

data class FormState(
    val text: String = "",
    val importance: String = "basic",
    val done: Boolean = false,
    val deadline: Date? = null,
    val createdAt: Long = Date().time,
    val dateState: DateState = DateState(),
    val importanceState: ImportanceState = ImportanceState()
) {
    data class DateState(
        val isToggleOn: Boolean = false,
        val initialDateSet: Boolean = false,
        val selectedDate: String = ""
    )

    data class ImportanceState(
        val textOfImportanceStatus : String = "Нет",
        val colorOfImportanceStatus : Int = R.color.tertiary,
    )
}