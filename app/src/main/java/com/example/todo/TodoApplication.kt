package com.example.todo

import android.app.Application
import com.example.todo.data.local.TodoDatabase
import com.example.todo.data.repository.TodoRepository
import com.example.todo.data.repository.TodoRepositoryImpl

class TodoApplication : Application() {
    val database: TodoDatabase by lazy {
        TodoDatabase.getInstance(this)
    }

    val repository: TodoRepository by lazy {
        TodoRepositoryImpl(database.todoDao)
    }
}
