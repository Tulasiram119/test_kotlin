package com.example.todo.ui.screens

import com.example.todo.domain.model.TodoItem

enum class TodoFilter(val label: String) {
    ALL("All"),
    ACTIVE("Active"),
    COMPLETED("Completed")
}

data class TodoUiState(
    val items: List<TodoItem> = emptyList(),
    val allCount: Int = 0,
    val activeCount: Int = 0,
    val completedCount: Int = 0,
    val selectedFilter: TodoFilter = TodoFilter.ALL,
    val searchQuery: String = "",
    val isAddSheetOpen: Boolean = false,
    val isLoading: Boolean = false,
    val recentlyDeletedItem: TodoItem? = null
)
