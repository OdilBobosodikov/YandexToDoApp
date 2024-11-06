package com.example.yandex_to_do_app

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.repository.ToDoRepository
import com.example.yandex_to_do_app.ui.theme.AppTypography
import com.example.yandex_to_do_app.ui.theme.Importance
import com.example.yandex_to_do_app.ui.theme.YandexToDoAppTheme

private val toDoItemRepository = ToDoRepository()

@Composable
fun FormScreen(navController: NavController, toDoItemId: Int = -1) {

    var textState = remember { mutableStateOf("") }
    var importanceState = remember { mutableStateOf(Importance.None) }
    if (toDoItemId != -1) {
        val item: ToDoItem? = toDoItemRepository.getItemById(toDoItemId)
        if (item != null) {
            textState.value = item.text
            importanceState.value = item.importance
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(vertical = 50.dp, horizontal = 20.dp))
    {
        Header(navController)
        Spacer(modifier = Modifier.height(20.dp))
        TextSection(textState)
        Spacer(modifier = Modifier.height(20.dp))
        ImportanceSection(importanceState)
        Spacer(modifier = Modifier.height(20.dp))
        Divider()
    }
}

@Composable
fun Header(navController: NavController) {
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
        )
    }
}

@Composable
fun TextSection(taskState: MutableState<String>) {
    Box(modifier = Modifier
        .shadow(4.dp)
        .clip(RoundedCornerShape(20f))) {

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

       UpdateValues(importanceState, textOfImportanceStatus, colorOfImportanceStatus)

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
               UpdateValues(importanceState, textOfImportanceStatus, colorOfImportanceStatus)
               expanded.value = false
           }, text = {
               Text(
                   text = "Нет",
                   color = colorResource(R.color.primary)
               )
           })
           DropdownMenuItem(onClick = {
               importanceState.value = Importance.Low
               UpdateValues(importanceState, textOfImportanceStatus, colorOfImportanceStatus)
               expanded.value = false
           }, text = {
               Text(
                   text = "Низкий",
                   color = colorResource(R.color.primary)
               )
           })
           DropdownMenuItem(onClick = {
               importanceState.value = Importance.High
               UpdateValues(importanceState, textOfImportanceStatus, colorOfImportanceStatus)
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

fun UpdateValues(importanceState: MutableState<Importance>,
                 textOfImportanceStatus: MutableState<String>,
                 colorOfImportanceStatus: MutableState<Int>)
{
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