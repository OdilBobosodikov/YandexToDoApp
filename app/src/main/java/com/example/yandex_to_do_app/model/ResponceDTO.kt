package com.example.yandex_to_do_app.model

import com.example.yandex_to_do_app.model.TodoListResponse.TodoItemResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TodoListResponse(
    @SerialName("status")
    val status: String,

    @SerialName("list")
    val list: List<TodoItemResponse>,

    @SerialName("revision")
    val revision: Int
)
{
    @Serializable
    data class TodoItemResponse(
        @SerialName("id")
        val id: String,

        @SerialName("text")
        val text: String,

        @SerialName("importance")
        val importance: String,

        @SerialName("deadline")
        val deadline: Long? = null,

        @SerialName("done")
        val done: Boolean,

        @SerialName("color")
        val color: String? = null,

        @SerialName("created_at")
        val createdAt: Long,

        @SerialName("changed_at")
        val changedAt: Long,

        @SerialName("last_updated_by")
        val lastUpdatedBy: String
    )
}

@Serializable
data class TodoOneItemResponse(
    @SerialName("status")
    val status: String,

    @SerialName("element")
    val element: TodoItemResponse,

    @SerialName("revision")
    val revision: Int
)

@Serializable
data class TodoPostPutDeleteItemRequest(
    @SerialName("status")
    val status: String,

    @SerialName("element")
    val element: TodoItemResponse,
)

@Serializable
data class UpdateListRequest(
    @SerialName("status")
    val status: String,
    @SerialName("list")
    val list: List<TodoItemResponse>
)