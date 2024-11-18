package com.example.yandex_to_do_app.ViewModel

import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandex_to_do_app.R
import com.example.yandex_to_do_app.model.ListItemState
import com.example.yandex_to_do_app.model.TodoListResponse
import com.example.yandex_to_do_app.model.TodoPostPutDeleteItemRequest
import com.example.yandex_to_do_app.model.UpdateListRequest
import com.example.yandex_to_do_app.repository.ToDoItemRepositoryImp
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ToDoViewModel : ViewModel() {
    private val repository = ToDoItemRepositoryImp()

    init {
        getToDoItems()
    }

    val appDateFormat: SimpleDateFormat
        get() = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

    private val _toDoList = MutableStateFlow<MutableList<TodoListResponse.TodoItemResponse>>(
        mutableListOf()
    )
    val toDoList = _toDoList.asStateFlow()

    private val _revision = MutableStateFlow<Int>(0)

    private val _isVisible = MutableStateFlow<Boolean>(false)
    val isVisible = _isVisible.asStateFlow()

    private val _numberOfCheckedItems = MutableStateFlow<Int>(0)
    val numberOfCheckedItems = _numberOfCheckedItems.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun getTaskStyle(task: TodoListResponse.TodoItemResponse): ListItemState {
        val isCompleted = task.done
        return when {
            task.importance == "important" && !isCompleted -> ListItemState(
                iconResId = R.drawable.ic_high_importance,
                iconColorId = R.color.red,
                textColorResId = R.color.red,
                textDecoration = TextDecoration.None,
                done = false
            )
            isCompleted -> ListItemState(
                iconResId = R.drawable.ic_checked,
                iconColorId = R.color.green,
                textColorResId = R.color.tertiary,
                textDecoration = TextDecoration.LineThrough,
                done = true
            )
            else -> ListItemState(
                iconResId = R.drawable.ic_unchecked,
                iconColorId = R.color.support_separator,
                textColorResId = R.color.primary,
                textDecoration = TextDecoration.None,
                done = false
            )
        }
    }
    fun toggleTaskCompletion(task: TodoListResponse.TodoItemResponse) {
        val updatedTask = task.copy(done = !task.done, changedAt = Date().time)
        _toDoList.value = _toDoList.value.map {
            if (it.id == task.id) updatedTask else it
        }.toMutableList()
        updateCounterOfCheckedItems(updatedTask.done)
    }

    private fun updateCounterOfCheckedItems(increase: Boolean = true) {
        if (increase) {
            _numberOfCheckedItems.value += 1
        } else {
            _numberOfCheckedItems.value -= 1
        }
    }
    fun updateVisibleState() {
        _isVisible.value = !_isVisible.value
    }
    fun getFormattedDeadline(date: Date?): String {
        if (date != null) return appDateFormat.format(date)
        return ""
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun getToDoItems(): Job {
        return viewModelScope.launch {
            val result = repository.getAllToDoItems()
            result.onSuccess {
                _toDoList.value = it.list.toMutableList()
                _revision.value = it.revision
                _numberOfCheckedItems.value = it.list.count { it.done }
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun updateItemById(
        id: String,
        todoPostPutDeleteItemRequest: TodoPostPutDeleteItemRequest
    ): Job {
        return viewModelScope.launch {
            val result =
                repository.updateToDoItemById(id, todoPostPutDeleteItemRequest, _revision.value)
            result.onSuccess {
                getToDoItems()
            }.onFailure {
                if (it is HttpException && it.code() == 400) {
                    _errorMessage.value = "Error: Wrong revision"
                } else if (it is HttpException && it.code() == 404) {
                    _errorMessage.value = "Error: No such value"
                } else {
                    _errorMessage.value = "Error: ${it.message}"
                }
            }
        }
    }

    fun getItemById(
        toDoItemId: String,
        onResult: (TodoListResponse.TodoItemResponse?) -> Unit
    ) {
        viewModelScope.launch {
            repository.getItemById(toDoItemId)
                .onSuccess {
                    onResult(it.element)
                }.onFailure {
                    when (it) {
                        is HttpException -> {
                            _errorMessage.value = "Error: Could not get Task by Id"
                            onResult(null)
                        }

                        is SerializationException -> {
                            _errorMessage.value = "Error: Wring data format"
                            onResult(null)
                        }

                        else -> {
                            _errorMessage.value = "Error: ${it.message}"
                            onResult(null)
                        }
                    }
                }
        }
    }

    fun deleteToDoItemById(itemId: String) {
        viewModelScope.launch {
            val result = repository.deleteToDoItemById(itemId, _revision.value)
            result.onSuccess {
                getToDoItems()
            }.onFailure {
                if (it is HttpException && it.code() == 400) {
                    _errorMessage.value = "Error: Wrong revision"
                } else if (it is HttpException && it.code() == 404) {
                    _errorMessage.value = "Error: No such value"
                } else {
                    _errorMessage.value = "Error: ${it.message}"
                }
            }
        }
    }

    fun postToDoItem(
        text: String,
        importance: String,
        deadline: Date?
    ) {
        viewModelScope.launch {
            val result = repository.addToDoItem(
                TodoPostPutDeleteItemRequest(
                    "ok",
                    TodoListResponse.TodoItemResponse(
                        UUID.randomUUID().toString(),
                        text = text,
                        importance = importance,
                        deadline = deadline?.time,
                        done = false,
                        createdAt = Date().time,
                        changedAt = Date().time,
                        lastUpdatedBy = "qwe"
                    )
                ), _revision.value
            )
            result.onSuccess {
                getToDoItems()
            }.onFailure {
                when (it) {
                    is HttpException -> {
                        _errorMessage.value = "Error: Wrong revision"
                    }

                    else -> {
                        _errorMessage.value = "Error: ${it.message}"
                    }
                }
            }
        }
    }

    fun updateList() {
        viewModelScope.launch {
            val result =
                repository.updateList(UpdateListRequest("ok", _toDoList.value), _revision.value)
            result.onSuccess {
                getToDoItems()
            }.onFailure {
                _errorMessage.value = "Error: ${it.message}"
            }
        }
    }

    override fun onCleared() {
        viewModelScope.coroutineContext.cancelChildren()
    }
}