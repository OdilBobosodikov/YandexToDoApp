package com.example.yandex_to_do_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.yandex_to_do_app.ui.theme.YandexToDoAppTheme
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.yandex_to_do_app.ViewModel.ToDoViewModel
import jakarta.inject.Inject


class MainActivity : ComponentActivity() {

    object Route {
        const val mainScreen = "mainScreen"
        const val formScreen = "formScreen"
    }

    @Inject
    lateinit var toDoViewModel: ToDoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as ToDoApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
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
                            toDoViewModel
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
                            toDoViewModel
                        )
                    }
                    composable(Route.formScreen) {
                        FormScreen(
                            navController = navController,
                            viewModel = toDoViewModel
                        )
                    }
                }
            }
        }
    }


}


