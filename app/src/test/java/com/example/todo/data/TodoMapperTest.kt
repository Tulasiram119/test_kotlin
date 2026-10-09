package com.example.todo.data

import com.example.todo.data.local.PriorityConverters
import com.example.todo.data.local.TodoEntity
import com.example.todo.data.mapper.toDomain
import com.example.todo.data.mapper.toEntity
import com.example.todo.domain.model.Priority
import com.example.todo.domain.model.TodoItem
import org.junit.Assert.assertEquals
import org.junit.Test

class TodoMapperTest {

    private val converter = PriorityConverters()

    @Test
    fun testPriorityConverters() {
        val stringValue = converter.fromPriority(Priority.HIGH)
        assertEquals("HIGH", stringValue)
        val enumValue = converter.toPriority("HIGH")
        assertEquals(Priority.HIGH, enumValue)
        val defaultFallback = converter.toPriority("INVALID")
        assertEquals(Priority.MEDIUM, defaultFallback)
    }

    @Test
    fun testMappingEntityToDomainAndBack() {
        val entity = TodoEntity(
            id = 42L,
            title = "Test Task",
            description = "Some description",
            priority = Priority.HIGH,
            isCompleted = true,
            createdAt = 123456789L
        )

        val domain = entity.toDomain()
        assertEquals(entity.id, domain.id)
        assertEquals(entity.title, domain.title)
        assertEquals(entity.description, domain.description)
        assertEquals(entity.priority, domain.priority)
        assertEquals(entity.isCompleted, domain.isCompleted)
        assertEquals(entity.createdAt, domain.createdAt)

        val mappedBack = domain.toEntity()
        assertEquals(entity, mappedBack)
    }
}
