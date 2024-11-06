package com.example.yandex_to_do_app

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.repository.ToDoRepository

private val toDoItemRepository = ToDoRepository()
@Composable
fun FormScreen(navController : NavController, toDoItemId :Int = -1) {

    var taskText = remember { mutableStateOf("") }
    if(toDoItemId != -1)
    {
        val item : ToDoItem? = toDoItemRepository.getItemById(toDoItemId)
        if(item != null)
        {
            taskText = remember { mutableStateOf(item.text) }
        }
    }



    Column(modifier = Modifier.fillMaxSize())
    {
        Row(modifier = Modifier.padding(vertical = 30.dp, horizontal = 20.dp).fillMaxWidth())
        {
            IconButton({navController.popBackStack()})
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            BasicTextField(
                value = taskText.value,
                onValueChange = { taskText.value = it },
                textStyle = TextStyle(fontSize = 18.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, MaterialTheme.shapes.medium)
                    .padding(8.dp)
                    .height(200.dp)
            )
        }
    }
}