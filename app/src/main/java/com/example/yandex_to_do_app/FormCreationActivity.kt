package com.example.yandex_to_do_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandex_to_do_app.ui.theme.YandexToDoAppTheme
import java.text.SimpleDateFormat
import java.util.*


class FormCreationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YandexToDoAppTheme {
                setContent {
                    Scaffold(modifier = Modifier.fillMaxSize()) {
                        TopHeader(modifier = Modifier.padding(it))
                    }

                }
            }
        }
    }

    @Composable
    fun TopHeader(modifier: Modifier) {
        var taskText by remember { mutableStateOf(TextFieldValue("Hello World")) }

        Column(modifier = Modifier.fillMaxSize())
        {
            Row(modifier = Modifier.padding(vertical = 30.dp, horizontal = 20.dp).fillMaxWidth())
            {
                IconButton({})
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
                    value = taskText,
                    onValueChange = { taskText = it },
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

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun GreetingPreview() {
        YandexToDoAppTheme {
            TopHeader(Modifier)
        }
    }

}
