package com.example.todo.data.local

import androidx.room.TypeConverter
import com.example.todo.domain.model.Priority

class PriorityConverters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.fromString(value)
}
