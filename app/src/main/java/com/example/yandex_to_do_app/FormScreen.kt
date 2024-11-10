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
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.ui.theme.AppTypography
import com.example.yandex_to_do_app.ui.theme.Importance
import com.example.yandex_to_do_app.ui.theme.YandexToDoAppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun FormScreen(navController: NavController,
               toDoItemId: Int = -1,
               viewModel: ToDoViewModel = ToDoViewModel()) {

    val textState = remember { mutableStateOf("") }
    val importanceState = remember { mutableStateOf(Importance.None) }
    val completionState = remember { mutableStateOf(false) }
    val deadlineDateState: MutableState<Date?> = remember { mutableStateOf(null) }
    var toDoItemId = toDoItemId

    if (toDoItemId != -1) {
        val item: ToDoItem? = viewModel.getItemById(toDoItemId)
        if(item != null)
        {
            textState.value = item.text
            importanceState.value = item.importance
            deadlineDateState.value = item.deadline
            toDoItemId = item.id
            completionState.value = item.isCompleted
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 50.dp, horizontal = 20.dp)
    )
    {
        Row(modifier = Modifier.fillMaxWidth())
        {
            IconButton(
                { navController.popBackStack() },
                modifier = Modifier.size(24.dp)
            )
            {
                Icon(Icons.Filled.Close, contentDescription = "Close")
            }
            Spacer(Modifier.weight(1f))
            Text(
                "CОХРАНИТЬ",
                color = colorResource(R.color.blue),
                modifier = Modifier.align(Alignment.CenterVertically)
                    .clickable {
                        if(toDoItemId != -1)
                        {
                            viewModel.updateToDoItem(
                                ToDoItem(
                                    id = toDoItemId,
                                    text = textState.value,
                                    importance = importanceState.value,
                                    deadline = deadlineDateState.value,
                                    isCompleted = completionState.value,
                                    createdAt = Date(),
                                    modifiedAt = Date()
                                )
                            )
                            navController.popBackStack()
                        }
                        else
                        {
                            viewModel.addToDoItem(
                                text = textState.value,
                                importance = importanceState.value,
                                deadline = deadlineDateState.value,
                            )
                            navController.popBackStack()
                        }
                    }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        TextSection(textState)
        Spacer(modifier = Modifier.height(20.dp))
        ImportanceSection(importanceState)
        Spacer(modifier = Modifier.height(20.dp))
        Divider()
        Spacer(modifier = Modifier.height(20.dp))
        DateSection(deadlineDateState)
        Spacer(modifier = Modifier.height(20.dp))
        Divider()
        Spacer(modifier = Modifier.height(20.dp))
        DeleteSection(toDoItemId, navController, viewModel)
    }
}


@Composable
fun DateSection(dateState: MutableState<Date?>) {
    val isToggleOn = remember { mutableStateOf(false) }
    val initialDateSet = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf("") }
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

    if (dateState.value != null) {
        selectedDate.value = dateFormat.format(dateState.value)
        isToggleOn.value = true
        initialDateSet.value = true
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            selectedDate.value = dateFormat.format(calendar.time)
            dateState.value = calendar.time
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
                    dateState.value = null
                }
            }
        )
    }
}


@Composable
fun DeleteSection(itemId: Int, navController: NavController, viewModel: ToDoViewModel) {
    if (itemId != -1) {
        Row(
            modifier = Modifier.clickable {
                val item = viewModel.getItemById(itemId)
                viewModel.deleteToDoItem(item)
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
fun TextSection(taskState: MutableState<String>) {
    Box(
        modifier = Modifier
            .shadow(4.dp)
            .clip(RoundedCornerShape(20f))
    ) {

        BasicTextField(
            value = taskState.value,
            onValueChange = { taskState.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(10f))
                .padding(8.dp)
                .height(150.dp)
        )
    }
}

@Composable
fun ImportanceSection(importanceState: MutableState<Importance>) {
    val textOfImportanceStatus = remember { mutableStateOf("Нет") }
    val colorOfImportanceStatus = remember { mutableStateOf(R.color.tertiary) }
    val expanded = remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Важность",
            style = AppTypography().bodyMedium,
        )

        UpdateImportanceValues(importanceState, textOfImportanceStatus, colorOfImportanceStatus)

        Text(
            text = textOfImportanceStatus.value,
            color = colorResource(colorOfImportanceStatus.value),
            modifier = Modifier.clickable { expanded.value = true }
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.background(color = Color.White)
        ) {
            DropdownMenuItem(onClick = {
                importanceState.value = Importance.None
                UpdateImportanceValues(
                    importanceState,
                    textOfImportanceStatus,
                    colorOfImportanceStatus
                )
                expanded.value = false
            }, text = {
                Text(
                    text = "Нет",
                    color = colorResource(R.color.primary)
                )
            })
            DropdownMenuItem(onClick = {
                importanceState.value = Importance.Low
                UpdateImportanceValues(
                    importanceState,
                    textOfImportanceStatus,
                    colorOfImportanceStatus
                )
                expanded.value = false
            }, text = {
                Text(
                    text = "Низкий",
                    color = colorResource(R.color.primary)
                )
            })
            DropdownMenuItem(onClick = {
                importanceState.value = Importance.High
                UpdateImportanceValues(
                    importanceState,
                    textOfImportanceStatus,
                    colorOfImportanceStatus
                )
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

fun UpdateImportanceValues(
    importanceState: MutableState<Importance>,
    textOfImportanceStatus: MutableState<String>,
    colorOfImportanceStatus: MutableState<Int>
) {
    if (importanceState.value == Importance.High) {
        textOfImportanceStatus.value = "!! Высокий"
        colorOfImportanceStatus.value = R.color.red
    } else if (importanceState.value == Importance.None) {
        textOfImportanceStatus.value = "Нет"
        colorOfImportanceStatus.value = R.color.tertiary
    } else {
        textOfImportanceStatus.value = "Низкий"
        colorOfImportanceStatus.value = R.color.tertiary
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