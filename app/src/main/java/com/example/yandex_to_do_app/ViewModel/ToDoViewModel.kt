package com.example.yandex_to_do_app.ViewModel

import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandex_to_do_app.R
import com.example.yandex_to_do_app.interfaces.ToDoItemRepository
import com.example.yandex_to_do_app.model.FormState
import com.example.yandex_to_do_app.model.ListItemState
import com.example.yandex_to_do_app.model.TodoListResponse
import com.example.yandex_to_do_app.model.TodoPostPutDeleteItemRequest
import com.example.yandex_to_do_app.model.UpdateListRequest
import jakarta.inject.Inject
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

class ToDoViewModel @Inject constructor(
    private val repository: ToDoItemRepository
) : ViewModel() {

    init {
        getToDoItems()
    }

    val appDateFormat: SimpleDateFormat
        get() = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

    private val _toDoList =
        MutableStateFlow<MutableList<TodoListResponse.TodoItemResponse>>(mutableListOf())
    val toDoList = _toDoList.asStateFlow()

    private val _revision = MutableStateFlow(0)

    private val _isVisible = MutableStateFlow(false)
    val isVisible = _isVisible.asStateFlow()

    private val _numberOfCheckedItems = MutableStateFlow(0)
    val numberOfCheckedItems = _numberOfCheckedItems.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _formState = MutableStateFlow(FormState())
    val formState = _formState.asStateFlow()

    private val _currentId = MutableStateFlow("")

    private fun updateCounterOfCheckedItems(increase: Boolean = true) {
        if (increase) {
            _numberOfCheckedItems.value += 1
        } else {
            _numberOfCheckedItems.value -= 1
        }
    }

    fun getFormattedDeadline(date: Date?): String {
        if (date != null) return appDateFormat.format(date)
        return ""
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun updateVisibleState() {
        _isVisible.value = !_isVisible.value
    }

    fun getTaskState(task: TodoListResponse.TodoItemResponse): ListItemState {
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

    fun getFormState(id: String) {
        if (id.isEmpty()) {
            _formState.value = FormState()
        } else {
            _currentId.value = id
            getItemById(id) {
                _formState.value = _formState.value.copy(
                    text = it?.text ?: "",
                    importance = it?.importance ?: "basic",
                    deadline = it?.deadline?.let { deadline -> Date(deadline) },
                    createdAt = it?.createdAt ?: Date().time,
                    done = it?.done ?: false,
                )
            }
        }
    }

    fun updateFormState(
        text: String? = null,
        importance: String? = null,
        deadline: Date? = Date(0),
        done: Boolean? = null,
        dateState: FormState.DateState? = null,
        importanceState: FormState.ImportanceState? = null
    ) {
        _formState.value = _formState.value.copy(
            text = text ?: _formState.value.text,
            importance = importance ?: _formState.value.importance,
            deadline = if (deadline == Date(0)) _formState.value.deadline else deadline,
            done = done ?: _formState.value.done,
            dateState = dateState ?: _formState.value.dateState,
            importanceState = importanceState ?: _formState.value.importanceState
        )
    }

    private fun getToDoItems(): Job {
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

    private fun updateItemById(
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

    fun saveItem(toDoItemId: String) : Job {
        return viewModelScope.launch {
            if (toDoItemId != "") {
                updateItemById(
                    toDoItemId,
                    TodoPostPutDeleteItemRequest(
                        status = "ok",
                        element = TodoListResponse.TodoItemResponse(
                            id = toDoItemId,
                            text = _formState.value.text,
                            importance = _formState.value.importance,
                            deadline = _formState.value.deadline?.time,
                            done = _formState.value.done,
                            createdAt = _formState.value.createdAt,
                            changedAt = Date().time,
                            lastUpdatedBy = "qwe"
                        )
                    )
                ).join()
                getToDoItems().join()
            } else {
                postToDoItem(
                    text = formState.value.text,
                    importance = formState.value.importance,
                    deadline = formState.value.deadline
                )
            }
        }
    }

    private fun getItemById(
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

    private fun postToDoItem(
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