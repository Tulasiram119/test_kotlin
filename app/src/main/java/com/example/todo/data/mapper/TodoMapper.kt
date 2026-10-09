package com.example.todo.data.mapper

import com.example.todo.data.local.TodoEntity
import com.example.todo.domain.model.TodoItem

fun TodoEntity.toDomain(): TodoItem = TodoItem(
    id = id,
    title = title,
    description = description,
    priority = priority,
    isCompleted = isCompleted,
    createdAt = createdAt
)

fun TodoItem.toEntity(): TodoEntity = TodoEntity(
    id = id,
    title = title,
    description = description,
    priority = priority,
    isCompleted = isCompleted,
    createdAt = createdAt
)
