package com.example.todo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todo.ui.screens.TodoFilter

@Composable
fun FilterChipRow(
    selectedFilter: TodoFilter,
    allCount: Int,
    activeCount: Int,
    completedCount: Int,
    onFilterSelected: (TodoFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TodoFilter.entries.forEach { filter ->
            val count = when (filter) {
                TodoFilter.ALL -> allCount
                TodoFilter.ACTIVE -> activeCount
                TodoFilter.COMPLETED -> completedCount
            }
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text("${filter.label} ($count)") }
            )
        }
    }
}
