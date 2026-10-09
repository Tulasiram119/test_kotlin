package com.example.todo.data

import com.example.todo.data.local.TodoDao
import com.example.todo.data.local.TodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTodoDao : TodoDao {
    private val todosMap = mutableMapOf<Long, TodoEntity>()
    private val todosFlow = MutableStateFlow<List<TodoEntity>>(emptyList())
    private var nextId = 1L

    private fun updateFlow() {
        todosFlow.value = todosMap.values.sortedByDescending { it.createdAt }
    }

    override fun getAllTodos(): Flow<List<TodoEntity>> = todosFlow

    override suspend fun insert(todo: TodoEntity): Long {
        val id = if (todo.id == 0L) nextId++ else todo.id
        val entity = todo.copy(id = id)
        todosMap[id] = entity
        updateFlow()
        return id
    }

    override suspend fun update(todo: TodoEntity) {
        todosMap[todo.id] = todo
        updateFlow()
    }

    override suspend fun delete(todo: TodoEntity) {
        todosMap.remove(todo.id)
        updateFlow()
    }

    override suspend fun deleteCompleted() {
        val toRemove = todosMap.filter { it.value.isCompleted }.keys
        toRemove.forEach { todosMap.remove(it) }
        updateFlow()
    }

    override suspend fun getTodoById(id: Long): TodoEntity? {
        return todosMap[id]
    }
}
