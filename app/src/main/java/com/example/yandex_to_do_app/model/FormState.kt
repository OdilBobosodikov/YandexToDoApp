package com.example.yandex_to_do_app.model

import java.util.Date

data class FormState(
    val text: String = "",
    val importance: String = "basic",
    val done: Boolean = false,
    val deadline: Date? = null,
    val createdAt: Long = Date().time
)