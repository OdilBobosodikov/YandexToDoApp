package com.example.yandex_to_do_app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.yandex_to_do_app.ViewModel.ToDoViewModel
import com.example.yandex_to_do_app.model.TodoListResponse
import com.example.yandex_to_do_app.ui.theme.AppTypography
import com.example.yandex_to_do_app.ui.theme.YandexToDoAppTheme
import com.example.yandex_to_do_app.ui.theme.robotoFontFamily
import kotlinx.coroutines.launch
import java.util.Date


@Composable
fun MainScreen(
    createTask: () -> Unit,
    updateTask: (TodoListResponse.TodoItemResponse) -> Unit,
    viewModel: ToDoViewModel = ToDoViewModel()
) {
    val errorMessage = viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val isButtonClicked = remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        errorMessage.value?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    actionLabel = "OK",
                    duration = SnackbarDuration.Short
                )
                viewModel.clearError()
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.updateList()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        it
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.back_primary))
        )
        {
            Column(modifier = Modifier.fillMaxSize())
            {
                Header(viewModel)
                ListOfItems(updateTask, createTask, viewModel, isButtonClicked)
            }
            CreateNewTaskBottom(viewModel, createTask, isButtonClicked)
        }
    }
}

@Composable
fun Header(viewModel: ToDoViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 82.dp, start = 60.dp)
    ) {
        Text(
            text = "Мои дела",
            style = AppTypography().titleLarge
        )
        ToolBar(viewModel)
    }
}

@Composable
fun ToolBar(viewModel: ToDoViewModel) {
    val isVisible = viewModel.isVisible.collectAsState()
    val numberOfCompletedTasks = viewModel.numberOfCheckedItems.collectAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 10.dp)
    ) {
        Text(
            text = "Выполнено — ${numberOfCompletedTasks.value}",
            color = colorResource(R.color.tertiary),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton({ viewModel.updateVisibleState() })
        {
            if (isVisible.value) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eye_off),
                    contentDescription = "Don't display completed tasks",
                    tint = colorResource(id = R.color.blue)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eye),
                    contentDescription = "Don't display completed tasks",
                    tint = colorResource(id = R.color.blue)
                )
            }
        }
    }
}

@Composable
fun ListOfItems(
    updateTask: (TodoListResponse.TodoItemResponse) -> Unit,
    createTask: () -> Unit,
    viewModel: ToDoViewModel,
    isButtonClicked: MutableState<Boolean>
) {
    val toDoList by viewModel.toDoList.collectAsState()
    val isVisible = viewModel.isVisible.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
    )
    {

        itemsIndexed(
            toDoList,
            key = { _, item -> item.id }) { i, item ->
            if (!isVisible.value && !item.done || isVisible.value) {
                ListItem(item, updateTask, viewModel, isButtonClicked)
            }
            if (i == toDoList.lastIndex || toDoList.isEmpty()) {
                Text(
                    text = "Новое",
                    modifier = Modifier
                        .padding(horizontal = 48.dp, vertical = 15.dp)
                        .clickable {
                            if (!isButtonClicked.value) {
                                isButtonClicked.value = true
                                viewModel.updateList()
                                createTask()
                            }
                        },
                    fontFamily = robotoFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(id = R.color.tertiary)
                )
            }
        }
    }
}

@Composable
fun ListItem(
    todoItemResponse: TodoListResponse.TodoItemResponse,
    updateTask: (TodoListResponse.TodoItemResponse) -> Unit,
    viewModel: ToDoViewModel,
    isButtonClicked: MutableState<Boolean>
) {
    val taskStyle = viewModel.getTaskState(todoItemResponse)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        IconButton(onClick =
        {
            viewModel.toggleTaskCompletion(todoItemResponse)
        })
        {
            Icon(
                painter = painterResource(id = taskStyle.iconResId),
                contentDescription = "Check as completed",
                tint = colorResource(taskStyle.iconColorId)
            )
        }
        Row()
        {
            if (todoItemResponse.importance == "important" && !todoItemResponse.done) {
                Text(
                    text = "!!",
                    style = AppTypography().bodyMedium,
                    color = colorResource(taskStyle.textColorResId),
                )
                Spacer(Modifier.width(3.dp))
            } else if (todoItemResponse.importance == "low" && !todoItemResponse.done) {
                Icon(
                    painter = painterResource(R.drawable.ic_low_importance),
                    contentDescription = "Low Importance",
                    tint = colorResource(taskStyle.iconColorId),
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
            Column {
                if (todoItemResponse.done) {
                    Text(
                        text = todoItemResponse.text,
                        style = TextStyle(
                            fontFamily = robotoFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(taskStyle.textColorResId),
                            textDecoration = taskStyle.textDecoration
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(270.dp)
                    )
                } else {
                    Text(
                        text = todoItemResponse.text,
                        style = AppTypography().bodyMedium,
                        color = colorResource(R.color.primary),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(270.dp)
                    )
                }
                if (todoItemResponse.deadline != null) {
                    Text(
                        text = viewModel.getFormattedDeadline(Date(todoItemResponse.deadline)),
                        style = AppTypography().headlineSmall,
                        color = colorResource(R.color.tertiary)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                if (!isButtonClicked.value) {
                    isButtonClicked.value = true
                    viewModel.updateList()
                    updateTask(todoItemResponse)
                }
            },
            enabled = !isButtonClicked.value
        )
        {
            Icon(
                painter = painterResource(id = R.drawable.ic_info),
                contentDescription = "Learn more about task",
                tint = colorResource(R.color.tertiary)
            )
        }
    }
}

@Composable
fun CreateNewTaskBottom(
    viewModel: ToDoViewModel,
    createTask: () -> Unit,
    isButtonClicked: MutableState<Boolean>
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = {
                if (!isButtonClicked.value) {
                    isButtonClicked.value = true
                    viewModel.updateList()
                    createTask()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 50.dp, end = 10.dp)
                .size(72.dp)
                .then(
                    if (isButtonClicked.value) Modifier.pointerInput(Unit) { } else Modifier
                ),
            containerColor = colorResource(R.color.blue),
            shape = CircleShape,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainActivityPreview() {
    YandexToDoAppTheme {
        YandexToDoAppTheme {
            MainScreen({}, {})
        }
    }
}