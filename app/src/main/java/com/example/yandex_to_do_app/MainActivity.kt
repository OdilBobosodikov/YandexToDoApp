package com.example.yandex_to_do_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.yandex_to_do_app.ui.theme.YandexToDoAppTheme
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.yandex_to_do_app.viewModel.ToDoViewModel


class MainActivity : ComponentActivity() {

    object Route {
        const val mainScreen = "mainScreen"
        const val formScreen = "formScreen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel = ToDoViewModel()
            YandexToDoAppTheme {
                NavHost(navController, Route.mainScreen)
                {
                    composable(route = Route.mainScreen)
                    {
                        MainScreen(
                            createTask = {
                                navController.navigate(Route.formScreen)
                            },
                            updateTask = {
                                navController.navigate("formScreen/${it.id}")
                            },
                            viewModel
                        )
                    }

                    composable(
                        "${Route.formScreen}/{toDoItemId}",
                        arguments = listOf(navArgument("toDoItemId") { defaultValue = "" })
                    ) {
                        val toDoItemId = it.arguments?.getString("toDoItemId") ?: ""
                        FormScreen(
                            navController = navController,
                            toDoItemId = toDoItemId,
                            viewModel
                        )
                    }
                    composable(Route.formScreen) {
                        FormScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }


}


