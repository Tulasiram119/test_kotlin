package com.example.todo.data.repository

import com.example.todo.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodos(): Flow<List<TodoItem>>
    suspend fun addTodo(item: TodoItem): Long
    suspend fun updateTodo(item: TodoItem)
    suspend fun deleteTodo(item: TodoItem)
    suspend fun toggleComplete(item: TodoItem)
    suspend fun clearCompleted()
}
