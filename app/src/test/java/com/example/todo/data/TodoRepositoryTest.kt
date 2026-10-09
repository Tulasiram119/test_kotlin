package com.example.todo.data

import com.example.todo.data.repository.TodoRepositoryImpl
import com.example.todo.domain.model.Priority
import com.example.todo.domain.model.TodoItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TodoRepositoryTest {

    private lateinit var fakeDao: FakeTodoDao
    private lateinit var repository: TodoRepositoryImpl

    @Before
    fun setUp() {
        fakeDao = FakeTodoDao()
        repository = TodoRepositoryImpl(fakeDao)
    }

    @Test
    fun testAddAndGetTodos() = runTest {
        val item = TodoItem(title = "Task 1", priority = Priority.HIGH)
        val id = repository.addTodo(item)
        val list = repository.getTodos().first()

        assertEquals(1, list.size)
        assertEquals(id, list[0].id)
        assertEquals("Task 1", list[0].title)
    }

    @Test
    fun testToggleComplete() = runTest {
        val item = TodoItem(title = "Task 1", isCompleted = false)
        val id = repository.addTodo(item)
        val saved = repository.getTodos().first().first { it.id == id }

        repository.toggleComplete(saved)
        val updated = repository.getTodos().first().first { it.id == id }
        assertTrue(updated.isCompleted)

        repository.toggleComplete(updated)
        val toggledBack = repository.getTodos().first().first { it.id == id }
        assertFalse(toggledBack.isCompleted)
    }

    @Test
    fun testDeleteAndClearCompleted() = runTest {
        val id1 = repository.addTodo(TodoItem(title = "Task 1", isCompleted = true))
        val id2 = repository.addTodo(TodoItem(title = "Task 2", isCompleted = false))

        repository.clearCompleted()
        val remaining = repository.getTodos().first()

        assertEquals(1, remaining.size)
        assertEquals(id2, remaining[0].id)
    }
}
