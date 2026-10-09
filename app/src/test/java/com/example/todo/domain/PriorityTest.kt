package com.example.todo.domain

import com.example.todo.domain.model.Priority
import com.example.todo.domain.model.TodoItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PriorityTest {

    @Test
    fun testPriorityOrderAndWeights() {
        assertTrue(Priority.HIGH.weight > Priority.MEDIUM.weight)
        assertTrue(Priority.MEDIUM.weight > Priority.LOW.weight)
        assertEquals("High", Priority.HIGH.displayName)
        assertEquals("Medium", Priority.MEDIUM.displayName)
        assertEquals("Low", Priority.LOW.displayName)
    }

    @Test
    fun testPriorityFromString() {
        assertEquals(Priority.HIGH, Priority.fromString("HIGH"))
        assertEquals(Priority.MEDIUM, Priority.fromString("medium"))
        assertEquals(Priority.LOW, Priority.fromString("low"))
        assertEquals(Priority.MEDIUM, Priority.fromString("UNKNOWN"))
    }

    @Test
    fun testTodoItemDefaultValues() {
        val item = TodoItem(
            id = 1L,
            title = "Buy groceries",
            description = "Milk and eggs",
            priority = Priority.HIGH,
            isCompleted = false,
            createdAt = 1000L
        )
        assertEquals(1L, item.id)
        assertEquals("Buy groceries", item.title)
        assertEquals("Milk and eggs", item.description)
        assertFalse(item.isCompleted)
        assertEquals(Priority.HIGH, item.priority)
        assertEquals(1000L, item.createdAt)
    }
}
