package com.example.yandex_to_do_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandex_to_do_app.model.ToDoItem
import com.example.yandex_to_do_app.repository.ToDoRepository
import com.example.yandex_to_do_app.ui.theme.AppTypography
import com.example.yandex_to_do_app.ui.theme.YandexToDoAppTheme
import com.example.yandex_to_do_app.ui.theme.robotoFontFamily

private val toDoItemRepository = ToDoRepository()
private val allToDoItems = toDoItemRepository.getAllToDoItems()

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YandexToDoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    WholeApp(modifier = Modifier.padding(it))
                }
            }
        }
    }
}

@Composable
fun WholeApp(modifier: Modifier) {
    val isVisible = remember { mutableStateOf(false) }
    val numberOfCompletedTasks = remember { mutableStateOf (toDoItemRepository.activeItemCount) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.back_primary))
    )
    {
        Column(modifier = modifier.fillMaxSize())
        {
            Header(isVisible, numberOfCompletedTasks)
            ListOfItems(isVisible, numberOfCompletedTasks)
        }
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
            .padding(end = 25.dp)
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
fun ListOfItems(isVisibleState: MutableState<Boolean>, numberOfCompletedTasks: MutableState<Int>) {
    LazyColumn(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
    )
    {
        itemsIndexed(allToDoItems.value,
            key = { _, item -> item.id }) { i, item ->
            if (!isVisibleState.value && !item.isCompleted) {
                ListItem(item, numberOfCompletedTasks)
                Spacer(modifier = Modifier.height(5.dp))
            } else if (isVisibleState.value) {
                ListItem(item, numberOfCompletedTasks)
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

@Composable
fun ListItem(toDoItem: ToDoItem, numberOfCompletedTasks : MutableState<Int>) {
    val item = remember { mutableStateOf(toDoItem) }
    val iconResId = remember { mutableStateOf(R.drawable.ic_unchecked) }
    val colorResId = remember { mutableStateOf(R.color.support_separator) }
    val textColorResId = remember { mutableStateOf(R.color.primary) }
    val textDecoration = remember { mutableStateOf(TextDecoration.None) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        IconButton(onClick = {
            item.value.isCompleted = !item.value.isCompleted
            if(toDoItemRepository.isDeadlineAlmostOver(item.value) && !item.value.isCompleted)
            {
                iconResId.value = R.drawable.ic_unchecked
                colorResId.value = R.color.red
                textColorResId.value = R.color.red
                textDecoration.value = TextDecoration.None
                numberOfCompletedTasks.value -= 1
            }
            else if (item.value.isCompleted) {
                iconResId.value = R.drawable.ic_checked
                colorResId.value = R.color.green
                textColorResId.value = R.color.tertiary
                textDecoration.value = TextDecoration.LineThrough
                numberOfCompletedTasks.value += 1
            }
            else {
                iconResId.value = R.drawable.ic_unchecked
                colorResId.value = R.color.support_separator
                textColorResId.value = R.color.primary
                textDecoration.value = TextDecoration.None
                numberOfCompletedTasks.value -= 1
            }
        })
        {
            CheckboxIconLogic(item, iconResId, colorResId)
        }
        if (toDoItemRepository.isDeadlineAlmostOver(item.value) && !item.value.isCompleted) {
            textColorResId.value = R.color.red
            Row()
            {
                Text(
                    text = "!!",
                    style = AppTypography().bodyMedium,
                    color = colorResource(textColorResId.value)
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = item.value.text,
                    style = AppTypography().bodyMedium,
                    color = colorResource(R.color.primary)
                )
            }
        } else if (item.value.isCompleted) {
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
                )
            )
        } else {
            Text(
                text = item.value.text,
                style = AppTypography().bodyMedium,
                color = colorResource(textColorResId.value)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = {})
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
fun CheckboxIconLogic(item: MutableState<ToDoItem>, pointerResId: MutableState<Int>, colorResId: MutableState<Int>) {
    if (toDoItemRepository.isDeadlineAlmostOver(item.value) && !item.value.isCompleted) {
        colorResId.value = R.color.red
        Icon(
            painter = painterResource(id = pointerResId.value),
            contentDescription = "Deadline almost over",
            tint = colorResource(id = colorResId.value)
        )
    } else if (!item.value.isCompleted) {
        Icon(
            painter = painterResource(id = pointerResId.value),
            contentDescription = "Task not completed",
            tint = colorResource(id = colorResId.value)
        )
    } else {
        colorResId.value = R.color.green
        pointerResId.value = R.drawable.ic_checked
        Icon(
            painter = painterResource(id = pointerResId.value),
            contentDescription = "Task completed",
            tint = colorResource(id = colorResId.value)
        )
    }


}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainActivityPreview() {
    YandexToDoAppTheme {
        WholeApp(modifier = Modifier)
    }
}
