package com.example.todo.data

import com.example.todo.data.repository.TodoRepository
import com.example.todo.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeTodoRepository : TodoRepository {
    private val todosMap = mutableMapOf<Long, TodoItem>()
    private val _todosFlow = MutableStateFlow<List<TodoItem>>(emptyList())
    private var nextId = 1L

    private fun refresh() {
        _todosFlow.value = todosMap.values.sortedByDescending { it.createdAt }
    }

    override fun getTodos(): Flow<List<TodoItem>> = _todosFlow.asStateFlow()

    override suspend fun addTodo(item: TodoItem): Long {
        val id = if (item.id == 0L) nextId++ else item.id
        val newItem = item.copy(id = id)
        todosMap[id] = newItem
        refresh()
        return id
    }

    override suspend fun updateTodo(item: TodoItem) {
        todosMap[item.id] = item
        refresh()
    }

    override suspend fun deleteTodo(item: TodoItem) {
        todosMap.remove(item.id)
        refresh()
    }

    override suspend fun toggleComplete(item: TodoItem) {
        val current = todosMap[item.id] ?: item
        todosMap[item.id] = current.copy(isCompleted = !current.isCompleted)
        refresh()
    }

    override suspend fun clearCompleted() {
        val toRemove = todosMap.filter { it.value.isCompleted }.keys
        toRemove.forEach { todosMap.remove(it) }
        refresh()
    }
}
