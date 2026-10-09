package com.example.todo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.todo.TodoApplication
import com.example.todo.ui.screens.TodoListScreen
import com.example.todo.ui.theme.TodoAppTheme
import com.example.todo.ui.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TodoViewModel by viewModels {
        val app = application as TodoApplication
        TodoViewModel.provideFactory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoAppTheme {
                TodoListScreen(viewModel = viewModel)
            }
        }
    }
}
