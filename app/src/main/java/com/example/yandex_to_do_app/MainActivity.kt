package com.example.yandex_to_do_app

import android.graphics.pdf.models.ListItem
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandex_to_do_app.ui.theme.AppTypography
import com.example.yandex_to_do_app.ui.theme.TaskStatus
import com.example.yandex_to_do_app.ui.theme.YandexToDoAppTheme
import com.example.yandex_to_do_app.ui.theme.robotoFontFamily

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YandexToDoAppTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) {
                    WholeApp(modifier = Modifier.padding(it))
                }
            }
        }
    }
}
@Composable
fun WholeApp(modifier: Modifier)
{
    var isVisible = remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(colorResource(id = R.color.back_primary)))
    {
        Column(modifier = modifier.fillMaxSize())
        {
            Header(isVisible)
            ListOfItems(isVisible)
        }
    }
}

@Composable
fun Header(isVisibleState : MutableState<Boolean>)
{
    Column(modifier = Modifier.fillMaxWidth().padding(top = 82.dp, start = 60.dp)) {
        Text(
            text = "Мои дела",
            style = AppTypography().titleLarge
        )
        ToolBar(isVisibleState)
    }
}

@Composable
fun ToolBar(isVisibleState: MutableState<Boolean>)
{
    Row(modifier = Modifier.fillMaxWidth().padding(end = 25.dp)) {
        Text(
            text = "Выполнено — 5",
            color = colorResource(R.color.tertiary),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton({isVisibleState.value = !isVisibleState.value})
        {
            if(isVisibleState.value)
            {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eye_off),
                    contentDescription = "Don't display completed tasks",
                    tint = colorResource(id = R.color.blue)
                )
            }
            else
            {
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
fun ListOfItems(isVisibleState: MutableState<Boolean>)
{

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
    )
    {
        ListItem(text = "Купить что-то", taskStatus = TaskStatus.Completed)
        Spacer(modifier = Modifier.height(5.dp))
        ListItem(text = "Купить что-то", taskStatus = TaskStatus.NotCompleted)
        Spacer(modifier = Modifier.height(5.dp))
        ListItem(text = "Купить что-то", taskStatus = TaskStatus.DeadlineAlmostOver)
    }
}

@Composable
fun ListItem(text: String, taskStatus: TaskStatus)
{
    var isChecked : TaskStatus by remember { mutableStateOf(taskStatus) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        //Код переделаю по возможности чуть позже. Сокрачу If-ы
        if(isChecked == TaskStatus.NotCompleted || isChecked == TaskStatus.DeadlineAlmostOver)
        {
            IconButton(onClick = {isChecked = TaskStatus.Completed})
            {
                CheckboxIconLogic(isChecked)
            }
            if(isChecked == TaskStatus.DeadlineAlmostOver)
            {
                Row()
                {
                    Text(text = "!!",
                        style = AppTypography().bodyMedium,
                        color = colorResource(R.color.red))
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = text,
                        style = AppTypography().bodyMedium,
                        color = colorResource(R.color.primary)
                    )

                }
            }
            else
            {
                Text(text = text,
                    style = AppTypography().bodyMedium)
            }
        }
        else
        {
            IconButton(onClick = {isChecked = TaskStatus.NotCompleted})
            {
                CheckboxIconLogic(isChecked)
            }
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = robotoFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    textDecoration = TextDecoration.LineThrough
                ),
                color = colorResource(R.color.tertiary)
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
fun CheckboxIconLogic(isChecked: TaskStatus)
{
    if(isChecked == TaskStatus.Completed)
    {
        Icon(
            painter = painterResource(id = R.drawable.ic_checked),
            contentDescription = "Task completed",
            tint = colorResource(R.color.green)
        )
    }
    else if(isChecked == TaskStatus.NotCompleted)
    {
        Icon(
            painter = painterResource(id = R.drawable.ic_unchecked),
            contentDescription = "Task not completed",
            tint = colorResource(R.color.support_separator)
        )
    }
    else
    {
        Icon(
            painter = painterResource(id = R.drawable.ic_unchecked),
            contentDescription = "Deadline almost over",
            tint = colorResource(R.color.red)
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
