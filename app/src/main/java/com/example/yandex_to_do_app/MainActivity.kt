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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun WholeApp(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize().background(colorResource(id = R.color.back_primary)))
    {
        HeaderSection()
    }
}

@Composable
fun HeaderSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 82.dp, start = 60.dp)) {
        Text(
            text = "Мои дела",
            fontSize = 32.sp,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight(500),
        )
        ToolBar()

    }
}

@Composable
fun ToolBar() {
    Row(modifier = Modifier.fillMaxWidth().padding(end = 40.dp)) {
        Text(
            text = "Выполнено — 5",
            color = colorResource(R.color.tertiary)
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.eye),
            contentDescription = "Don't display completed tasks",
            tint = colorResource(id = R.color.blue),
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainActivityPreview() {
    YandexToDoAppTheme {
        HeaderSection()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PartialActivityPreview() {
    YandexToDoAppTheme {
        HeaderSection()
    }
}