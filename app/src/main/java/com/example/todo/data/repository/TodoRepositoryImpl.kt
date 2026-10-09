package com.example.todo.data.repository

import com.example.todo.data.local.TodoDao
import com.example.todo.data.mapper.toDomain
import com.example.todo.data.mapper.toEntity
import com.example.todo.domain.model.TodoItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TodoRepository {

    override fun getTodos(): Flow<List<TodoItem>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTodo(item: TodoItem): Long = withContext(ioDispatcher) {
        todoDao.insert(item.toEntity())
    }

    override suspend fun updateTodo(item: TodoItem) = withContext(ioDispatcher) {
        todoDao.update(item.toEntity())
    }

    override suspend fun deleteTodo(item: TodoItem) = withContext(ioDispatcher) {
        todoDao.delete(item.toEntity())
    }

    override suspend fun toggleComplete(item: TodoItem) = withContext(ioDispatcher) {
        val updated = item.copy(isCompleted = !item.isCompleted)
        todoDao.update(updated.toEntity())
    }

    override suspend fun clearCompleted() = withContext(ioDispatcher) {
        todoDao.deleteCompleted()
    }
}
