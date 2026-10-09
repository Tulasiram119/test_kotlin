package com.example.todo.domain.model

enum class Priority(val displayName: String, val weight: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3);

    companion object {
        fun fromString(value: String): Priority {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}
