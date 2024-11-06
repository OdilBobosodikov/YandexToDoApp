package com.example.yandex_to_do_app

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.yandex_to_do_app.MainActivity.Route
import com.example.yandex_to_do_app.ViewModel.ToDoViewModel
import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.ui.theme.AppTypography
import com.example.yandex_to_do_app.ui.theme.Importance
import com.example.yandex_to_do_app.ui.theme.YandexToDoAppTheme
import com.example.yandex_to_do_app.ui.theme.robotoFontFamily


@Composable
fun MainScreen(createTask: () -> Unit,
               updateTask: (ToDoItem) -> Unit,
               viewModel: ToDoViewModel = ToDoViewModel()) {
    val isVisible = remember { mutableStateOf(false) }

    val numberOfCompletedTasks = remember { mutableStateOf(viewModel.completedItemCount) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.back_primary))
    )
    {
        Column(modifier = Modifier.fillMaxSize())
        {
            Header(isVisible, numberOfCompletedTasks)
            ListOfItems(isVisible, numberOfCompletedTasks, updateTask, viewModel)
        }
        CreateNewTaskBottom(createTask)
    }
}

@Composable
fun Header(isVisibleState: MutableState<Boolean>, numberOfCompletedTasks: MutableState<Int>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 82.dp, start = 60.dp)
    ) {
        Text(
            text = "Мои дела",
            style = AppTypography().titleLarge
        )
        ToolBar(isVisibleState, numberOfCompletedTasks)
    }
}

@Composable
fun ToolBar(isVisibleState: MutableState<Boolean>, numberOfCompletedTasks: MutableState<Int>) {
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
        IconButton({ isVisibleState.value = !isVisibleState.value })
        {
            if (isVisibleState.value) {
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
fun ListOfItems(isVisibleState: MutableState<Boolean>,
                numberOfCompletedTasks: MutableState<Int>,
                updateTask: (ToDoItem) -> Unit,
                viewModel: ToDoViewModel) {

    val todoItems = viewModel.todoItems.value

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
    )
    {

        itemsIndexed(
            todoItems,
            key = { _, item -> item.id }) { i, item ->
            if (!isVisibleState.value && !item.isCompleted || isVisibleState.value) {
                ListItem(item, numberOfCompletedTasks, updateTask, viewModel)
            }
            if (i == todoItems.count() - 1 || todoItems.count() == 0) {
                Text(
                    text = "Новое",
                    modifier = Modifier
                        .padding(horizontal = 48.dp, vertical = 15.dp),
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
    toDoItem: ToDoItem,
    numberOfCompletedTasks: MutableState<Int>,
    updateTask: (ToDoItem) -> Unit,
    viewModel: ToDoViewModel
) {

    val item = remember { mutableStateOf(toDoItem) }
    val iconResId = remember { mutableStateOf(R.drawable.ic_unchecked) }
    val iconColorId = remember { mutableStateOf(R.color.support_separator) }
    val textColorResId = remember { mutableStateOf(R.color.primary) }
    val textDecoration = remember { mutableStateOf(TextDecoration.None) }
    val isLowImportanceSet = remember { mutableStateOf(false) }
    val isTaskCompleted = remember { mutableStateOf(item.value.isCompleted) }
    if (item.value.importance == Importance.Low) {
        isLowImportanceSet.value = true
    }

    if (item.value.importance == Importance.High && !isTaskCompleted.value) {
        iconResId.value = R.drawable.ic_high_importance
        textColorResId.value = R.color.red
        textDecoration.value = TextDecoration.None
        iconColorId.value = R.color.red
    } else if (isTaskCompleted.value) {
        iconResId.value = R.drawable.ic_checked
        textColorResId.value = R.color.tertiary
        textDecoration.value = TextDecoration.LineThrough
        iconColorId.value = R.color.green
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        IconButton(onClick =
        {
            isTaskCompleted.value = !isTaskCompleted.value
            viewModel.updateItemCompletionStatus(item.value, isTaskCompleted.value)
            if (item.value.importance == Importance.High && !isTaskCompleted.value) {
                iconResId.value = R.drawable.ic_high_importance
                textColorResId.value = R.color.red
                iconColorId.value = R.color.red
                textDecoration.value = TextDecoration.None
                numberOfCompletedTasks.value -= 1
            } else if (isTaskCompleted.value) {
                isLowImportanceSet.value = false
                iconResId.value = R.drawable.ic_checked
                textColorResId.value = R.color.tertiary
                iconColorId.value = R.color.green
                textDecoration.value = TextDecoration.LineThrough
                numberOfCompletedTasks.value += 1
            } else {
                iconResId.value = R.drawable.ic_unchecked
                textColorResId.value = R.color.primary
                iconColorId.value = R.color.support_separator
                textDecoration.value = TextDecoration.None
                numberOfCompletedTasks.value -= 1
            }
        })
        {
            Icon(
                painter = painterResource(id = iconResId.value),
                contentDescription = "Check as completed",
                tint = colorResource(iconColorId.value)
            )
        }
        if (isTaskCompleted.value) {
            textDecoration.value = TextDecoration.LineThrough
            textColorResId.value = R.color.tertiary
            Text(
                text = item.value.text,
                style = TextStyle(
                    fontFamily = robotoFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(textColorResId.value),
                    textDecoration = textDecoration.value
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(270.dp)
            )
        } else {
            if (item.value.importance == Importance.High && !isTaskCompleted.value) {
                Row()
                {
                    textColorResId.value = R.color.red
                    Text(
                        text = "!!",
                        style = AppTypography().bodyMedium,
                        color = colorResource(textColorResId.value),
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = item.value.text,
                        style = AppTypography().bodyMedium,
                        color = colorResource(R.color.primary),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(270.dp)
                    )
                }
            } else if (isLowImportanceSet.value) {
                Row()
                {
                    textColorResId.value = R.color.red
                    Icon(
                        painter = painterResource(id = R.drawable.ic_low_importance),
                        contentDescription = "Low Importance",
                        tint = colorResource(id = R.color.grey_light),
                        modifier = Modifier.padding(top = 3.dp)
                    )
                    Text(
                        text = item.value.text,
                        style = AppTypography().bodyMedium,
                        color = colorResource(R.color.primary),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(270.dp)
                    )
                }
            }
            else
            {
                Text(
                    text = item.value.text,
                    style = AppTypography().bodyMedium,
                    color = colorResource(R.color.primary),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(270.dp)
                )
            }
        }
        val isUpdatingTask  = remember { mutableStateOf(false) }

        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = {
            if(!isUpdatingTask.value)
            {
                isUpdatingTask.value = true
                updateTask(item.value)
            }
        },
            enabled = !isUpdatingTask.value)
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
fun CreateNewTaskBottom(createTask: () -> Unit) {
    val isCreatingTask = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = {
                if (!isCreatingTask.value) {
                    isCreatingTask.value = true
                    createTask()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 25.dp, end = 10.dp)
                .size(56.dp)
                .then(
                    if (isCreatingTask.value) Modifier.pointerInput(Unit) { } else Modifier
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
        val navController = rememberNavController()
        YandexToDoAppTheme {
            NavHost(navController, Route.mainScreen)
            {
                composable(route = Route.mainScreen)
                {
                    MainScreen(
                        createTask = {
                            navController.navigate("formScreen") },
                        updateTask = { toDoItem ->
                            navController.navigate("formScreen/${toDoItem.id}")
                        }
                    )
                }

                composable("formScreen/{toDoItemId}", arguments = listOf(navArgument("toDoItemId") { defaultValue = -1 })) {
                    val toDoItemId = it.arguments?.getInt("toDoItemId") ?: -1
                    FormScreen(
                        navController = navController,
                        toDoItemId = toDoItemId
                    )
                }
            }
        }
    }
}