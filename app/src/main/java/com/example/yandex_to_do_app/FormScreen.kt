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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.yandex_to_do_app.ViewModel.ToDoViewModel
import com.example.yandex_to_do_app.model.FormState
import com.example.yandex_to_do_app.ui.theme.AppTypography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar


@Composable
fun FormScreen(
    navController: NavController,
    toDoItemId: String = "",
    viewModel: ToDoViewModel
) {
    LaunchedEffect(toDoItemId) {
        viewModel.getFormState(toDoItemId)
    }
    val isButtonClicked = remember { mutableStateOf(false) }
    val formState = viewModel.formState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
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
                        viewModel.getFormState(toDoItemId)
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
                                viewModel.saveItem(toDoItemId).join()
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
        DeleteSection(toDoItemId, navController, viewModel, coroutineScope)
    }
}

@Composable
fun DateSection(viewModel: ToDoViewModel, formState: State<FormState>) {

    if (formState.value.deadline != null) {
        viewModel.updateFormState(dateState = FormState.DateState(true,
            true,
            formState.value.deadline?.let { viewModel.appDateFormat.format(it) }.toString()
        )
        )
    }
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            viewModel.updateFormState(
                deadline = calendar.time,
                dateState = FormState.DateState(
                    true,
                    true,
                    viewModel.appDateFormat.format(calendar.time)
                )
            )
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(formState.value.dateState.isToggleOn && !formState.value.dateState.initialDateSet) {
        if (formState.value.dateState.isToggleOn && !formState.value.dateState.initialDateSet) {
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
            if (formState.value.dateState.isToggleOn) {
                Text(
                    text = formState.value.dateState.selectedDate,
                    style = AppTypography().bodySmall,
                    color = colorResource(R.color.blue)
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Switch(
            checked = formState.value.dateState.isToggleOn,
            onCheckedChange = {
                if (it) {
                    viewModel.updateFormState(
                        dateState = FormState.DateState(
                            isToggleOn = true,
                            initialDateSet = false
                        )
                    )
                } else {
                    viewModel.updateFormState(
                        deadline = null,
                        dateState = FormState.DateState(isToggleOn = false, selectedDate = "")
                    )
                }
            }
        )
    }
}

@Composable
fun DeleteSection(
    itemId: String,
    navController: NavController,
    viewModel: ToDoViewModel,
    coroutineScope: CoroutineScope
) {
    if (itemId != "") {
        Row(
            modifier = Modifier.clickable {
                coroutineScope.launch {
                    viewModel.deleteToDoItemById(itemId).join()
                    navController.popBackStack()
                }
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

        when (formState.value.importance) {
            "important" -> {
                viewModel.updateFormState(
                    importanceState = FormState.ImportanceState(
                        "!! Высокий",
                        R.color.red
                    )
                )
            }

            "basic" -> {
                viewModel.updateFormState(importanceState = FormState.ImportanceState())
            }

            else -> {
                viewModel.updateFormState(importanceState = FormState.ImportanceState("Низкий"))
            }
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
