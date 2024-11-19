package com.example.yandex_to_do_app

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.yandex_to_do_app.ViewModel.ToDoViewModel
import com.example.yandex_to_do_app.model.FormState
import com.example.yandex_to_do_app.model.TodoListResponse
import com.example.yandex_to_do_app.model.TodoPostPutDeleteItemRequest
import com.example.yandex_to_do_app.ui.theme.AppTypography
import com.example.yandex_to_do_app.ui.theme.YandexToDoAppTheme
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date


@Composable
fun FormScreen(
    navController: NavController,
    toDoItemId: String = "",
    viewModel: ToDoViewModel = ToDoViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val formState = viewModel.formState.collectAsState()
    val isButtonClicked = remember { mutableStateOf(false) }
    viewModel.getFormState(toDoItemId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 50.dp, horizontal = 20.dp)
    )
    {
        Row(modifier = Modifier.fillMaxWidth())
        {
            IconButton(
                {
                    if (!isButtonClicked.value) {
                        isButtonClicked.value = true
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.size(24.dp)
            )
            {
                Icon(Icons.Filled.Close, contentDescription = "Close")
            }
            Spacer(Modifier.weight(1f))
            Text(
                "CОХРАНИТЬ",
                color = colorResource(R.color.blue),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable {
                        if (!isButtonClicked.value) {
                            isButtonClicked.value = true
                            coroutineScope.launch {
                                if (toDoItemId != "") {
                                    viewModel
                                        .updateItemById(
                                            toDoItemId,
                                            TodoPostPutDeleteItemRequest(
                                                status = "ok",
                                                element = TodoListResponse.TodoItemResponse(
                                                    id = toDoItemId,
                                                    text = formState.value.text,
                                                    importance = formState.value.importance,
                                                    deadline = formState.value.deadline?.time,
                                                    done = formState.value.done,
                                                    createdAt = formState.value.createdAt,
                                                    changedAt = Date().time,
                                                    lastUpdatedBy = "qwe"
                                                )
                                            )
                                        )
                                        .join()
                                    viewModel
                                        .getToDoItems()
                                        .join()
                                } else {
                                    viewModel.postToDoItem(
                                        text = formState.value.text,
                                        importance = formState.value.importance,
                                        deadline = formState.value.deadline,
                                    )
                                }
                                navController.popBackStack()
                            }
                        }
                    }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        TextSection(viewModel, formState)
        Spacer(modifier = Modifier.height(20.dp))
        ImportanceSection(viewModel, formState)
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))
        DateSection(viewModel, formState)
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))
        DeleteSection(toDoItemId, navController, viewModel)
    }
}

@Composable
fun DateSection(viewModel: ToDoViewModel, formState: State<FormState>) {
    //тут баг был когда State обновлялся раз 5-6 и Switch значение менял, поэтому оставил пока так
    val isToggleOn = remember { mutableStateOf(false) }
    val initialDateSet = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf("") }

    if (formState.value.deadline != null) {
        selectedDate.value =
            formState.value.deadline?.let { viewModel.appDateFormat.format(it) }.toString()
        isToggleOn.value = true
        initialDateSet.value = true
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            selectedDate.value = viewModel.appDateFormat.format(calendar.time)
            viewModel.updateFormState(deadline = calendar.time)
            isToggleOn.value = true
            initialDateSet.value = true
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(isToggleOn.value && !initialDateSet.value) {
        if (isToggleOn.value && !initialDateSet.value) {
            datePickerDialog.show()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = "Сделать до",
                style = AppTypography().bodyMedium,
            )
            if (isToggleOn.value) {
                Text(
                    text = selectedDate.value,
                    style = AppTypography().bodySmall,
                    color = colorResource(R.color.blue)
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Switch(
            checked = isToggleOn.value,
            onCheckedChange = {
                if (it) {
                    isToggleOn.value = true
                    initialDateSet.value = false
                } else {
                    isToggleOn.value = false
                    selectedDate.value = ""
                    viewModel.updateFormState(deadline = null)
                }
            }
        )
    }
}

@Composable
fun DeleteSection(itemId: String, navController: NavController, viewModel: ToDoViewModel) {
    if (itemId != "") {
        Row(
            modifier = Modifier.clickable {
                viewModel.deleteToDoItemById(itemId)
                navController.popBackStack()
            },
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Icon(
                painter = painterResource(R.drawable.ic_trash),
                tint = colorResource(R.color.red),
                contentDescription = "Delete Task"
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Удалить",
                style = AppTypography().bodyMedium,
                color = colorResource(R.color.red)
            )
        }
    } else {
        Row(verticalAlignment = Alignment.CenterVertically)
        {
            Icon(
                painter = painterResource(R.drawable.ic_trash),
                tint = colorResource(R.color.disable),
                contentDescription = "Delete Task"
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Удалить",
                style = AppTypography().bodyMedium,
                color = colorResource(R.color.disable)
            )
        }
    }
}

@Composable
fun TextSection(viewModel: ToDoViewModel, formState: State<FormState>) {
    Box(
        modifier = Modifier
            .shadow(4.dp)
            .clip(RoundedCornerShape(20f))
    ) {

        BasicTextField(
            value = formState.value.text,
            onValueChange = { viewModel.updateFormState(text = it) },
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(10f))
                .padding(8.dp)
                .height(150.dp)
        )
    }
}

@Composable
fun ImportanceSection(viewModel: ToDoViewModel, formState: State<FormState>) {
    val expanded = remember { mutableStateOf(false) }
    Column {
        Text(
            text = "Важность",
            style = AppTypography().bodyMedium,
        )

        if (formState.value.importance == "important") {
            viewModel.updateFormState(
                importanceState = FormState.ImportanceState(
                    "!! Высокий",
                    R.color.red
                )
            )
        } else if (formState.value.importance == "basic") {
            viewModel.updateFormState(importanceState = FormState.ImportanceState())
        } else {
            viewModel.updateFormState(importanceState = FormState.ImportanceState("Низкий"))
        }

        Text(
            text = formState.value.importanceState.textOfImportanceStatus,
            color = colorResource(formState.value.importanceState.colorOfImportanceStatus),
            modifier = Modifier.clickable {
                expanded.value = true
            }
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            },
            modifier = Modifier.background(color = Color.White)
        ) {
            DropdownMenuItem(onClick = {
                viewModel.updateFormState(importance = "basic")
                expanded.value = false
            }, text = {
                Text(
                    text = "Нет",
                    color = colorResource(R.color.primary)
                )
            })
            DropdownMenuItem(onClick = {
                viewModel.updateFormState(importance = "low")
                expanded.value = false
            }, text = {
                Text(
                    text = "Низкий",
                    color = colorResource(R.color.primary)
                )
            })
            DropdownMenuItem(onClick = {
                viewModel.updateFormState(importance = "important")
                expanded.value = false
            }, text = {
                Text(
                    text = "!! Высокий",
                    color = colorResource(R.color.red)
                )
            })
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FormScreen() {
    YandexToDoAppTheme {
        val navController = rememberNavController()
        YandexToDoAppTheme {
            FormScreen(navController)
        }
    }
}