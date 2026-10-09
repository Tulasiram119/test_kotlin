package com.example.todo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo.domain.model.Priority
import com.example.todo.ui.theme.PriorityHighContainer
import com.example.todo.ui.theme.PriorityHighOnContainer
import com.example.todo.ui.theme.PriorityLowContainer
import com.example.todo.ui.theme.PriorityLowOnContainer
import com.example.todo.ui.theme.PriorityMediumContainer
import com.example.todo.ui.theme.PriorityMediumOnContainer

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (priority) {
        Priority.HIGH -> PriorityHighContainer to PriorityHighOnContainer
        Priority.MEDIUM -> PriorityMediumContainer to PriorityMediumOnContainer
        Priority.LOW -> PriorityLowContainer to PriorityLowOnContainer
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = priority.displayName,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
