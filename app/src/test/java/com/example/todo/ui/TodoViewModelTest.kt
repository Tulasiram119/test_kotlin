package com.example.todo.ui

import com.example.todo.data.FakeTodoRepository
import com.example.todo.domain.model.Priority
import com.example.todo.domain.model.TodoItem
import com.example.todo.ui.screens.TodoFilter
import com.example.todo.ui.viewmodel.TodoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTodoRepository
    private lateinit var viewModel: TodoViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTodoRepository()
        viewModel = TodoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(0, state.items.size)
        assertEquals(TodoFilter.ALL, state.selectedFilter)
        assertEquals("", state.searchQuery)
        assertFalse(state.isAddSheetOpen)
    }

    @Test
    fun testAddTodoAndCounts() = runTest {
        advanceUntilIdle()
        viewModel.onAddTodo("Grocery shopping", "Buy fruits", Priority.HIGH)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals(1, state.allCount)
        assertEquals(1, state.activeCount)
        assertEquals(0, state.completedCount)
        assertEquals("Grocery shopping", state.items[0].title)
    }

    @Test
    fun testToggleCompleteAndFilters() = runTest {
        advanceUntilIdle()
        viewModel.onAddTodo("Task 1", "", Priority.LOW)
        viewModel.onAddTodo("Task 2", "", Priority.MEDIUM)
        advanceUntilIdle()

        val task1 = viewModel.uiState.value.items.first { it.title == "Task 1" }
        viewModel.onToggleComplete(task1)
        advanceUntilIdle()

        var state = viewModel.uiState.value
        assertEquals(2, state.allCount)
        assertEquals(1, state.activeCount)
        assertEquals(1, state.completedCount)

        // Filter ACTIVE
        viewModel.onFilterSelected(TodoFilter.ACTIVE)
        advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals("Task 2", state.items[0].title)

        // Filter COMPLETED
        viewModel.onFilterSelected(TodoFilter.COMPLETED)
        advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals("Task 1", state.items[0].title)
    }

    @Test
    fun testSearchQueryFilter() = runTest {
        advanceUntilIdle()
        viewModel.onAddTodo("Learn Kotlin", "Basics of Kotlin", Priority.HIGH)
        viewModel.onAddTodo("Cook Dinner", "Pasta with sauce", Priority.LOW)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("kotlin")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals("Learn Kotlin", state.items[0].title)
    }

    @Test
    fun testDeleteAndUndo() = runTest {
        advanceUntilIdle()
        viewModel.onAddTodo("Task to delete", "", Priority.LOW)
        advanceUntilIdle()

        val item = viewModel.uiState.value.items[0]
        viewModel.onDeleteTodo(item)
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.items.size)

        viewModel.onUndoDelete(item)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.items.size)
        assertEquals("Task to delete", viewModel.uiState.value.items[0].title)
    }
}
