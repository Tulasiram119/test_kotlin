package com.example.todo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todo.data.repository.TodoRepository
import com.example.todo.domain.model.Priority
import com.example.todo.domain.model.TodoItem
import com.example.todo.ui.screens.TodoFilter
import com.example.todo.ui.screens.TodoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TodoFilter.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _isAddSheetOpen = MutableStateFlow(false)
    private val _recentlyDeletedItem = MutableStateFlow<TodoItem?>(null)

    val uiState: StateFlow<TodoUiState> = combine(
        repository.getTodos(),
        _selectedFilter,
        _searchQuery,
        _isAddSheetOpen,
        _recentlyDeletedItem
    ) { allTodos, filter, query, isSheetOpen, deletedItem ->
        val trimmedQuery = query.trim().lowercase()

        val filteredBySearchAndStatus = allTodos.filter { item ->
            val matchesFilter = when (filter) {
                TodoFilter.ALL -> true
                TodoFilter.ACTIVE -> !item.isCompleted
                TodoFilter.COMPLETED -> item.isCompleted
            }
            val matchesQuery = if (trimmedQuery.isEmpty()) {
                true
            } else {
                item.title.lowercase().contains(trimmedQuery) ||
                        item.description.lowercase().contains(trimmedQuery)
            }
            matchesFilter && matchesQuery
        }

        TodoUiState(
            items = filteredBySearchAndStatus,
            allCount = allTodos.size,
            activeCount = allTodos.count { !it.isCompleted },
            completedCount = allTodos.count { it.isCompleted },
            selectedFilter = filter,
            searchQuery = query,
            isAddSheetOpen = isSheetOpen,
            isLoading = false,
            recentlyDeletedItem = deletedItem
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoUiState(isLoading = true)
    )

    fun onFilterSelected(filter: TodoFilter) {
        _selectedFilter.value = filter
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setAddSheetVisible(visible: Boolean) {
        _isAddSheetOpen.value = visible
    }

    fun onAddTodo(title: String, description: String = "", priority: Priority = Priority.MEDIUM) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) return

        viewModelScope.launch {
            val newItem = TodoItem(
                title = trimmedTitle,
                description = description.trim(),
                priority = priority
            )
            repository.addTodo(newItem)
            _isAddSheetOpen.value = false
        }
    }

    fun onToggleComplete(item: TodoItem) {
        viewModelScope.launch {
            repository.toggleComplete(item)
        }
    }

    fun onDeleteTodo(item: TodoItem) {
        viewModelScope.launch {
            _recentlyDeletedItem.value = item
            repository.deleteTodo(item)
        }
    }

    fun onUndoDelete(item: TodoItem) {
        viewModelScope.launch {
            repository.addTodo(item)
            _recentlyDeletedItem.value = null
        }
    }

    fun onClearRecentlyDeleted() {
        _recentlyDeletedItem.value = null
    }

    fun onClearCompleted() {
        viewModelScope.launch {
            repository.clearCompleted()
        }
    }

    companion object {
        fun provideFactory(repository: TodoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TodoViewModel(repository) as T
                }
            }
    }
}
