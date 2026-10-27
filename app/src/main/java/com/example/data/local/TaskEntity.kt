package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Priority
import com.example.data.model.Subtask
import com.example.data.model.TaskItem
import com.example.data.model.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey val id: String,
  val title: String,
  val description: String,
  val status: String,
  val priority: String,
  val category: String,
  val dueDate: String?,
  val createdAt: Long,
  val updatedAt: Long,
  val completedAt: Long?,
  val subtasksJson: String,
  val startedAt: Long? = null,
  val elapsedSeconds: Long = 0L,
  val isTimerRunning: Boolean = false
) {
  fun toTaskItem(): TaskItem {
    return TaskItem(
      id = id,
      title = title,
      description = description,
      status = TaskStatus.fromString(status),
      priority = Priority.fromString(priority),
      category = category,
      dueDate = dueDate,
      createdAt = createdAt,
      updatedAt = updatedAt,
      completedAt = completedAt,
      subtasks = Subtask.listFromJson(subtasksJson),
      startedAt = startedAt,
      elapsedSeconds = elapsedSeconds,
      isTimerRunning = isTimerRunning
    )
  }

  companion object {
    fun fromTaskItem(task: TaskItem): TaskEntity {
      return TaskEntity(
        id = task.id,
        title = task.title,
        description = task.description,
        status = task.status.value,
        priority = task.priority.value,
        category = task.category,
        dueDate = task.dueDate,
        createdAt = task.createdAt,
        updatedAt = task.updatedAt,
        completedAt = task.completedAt,
        subtasksJson = Subtask.listToJson(task.subtasks),
        startedAt = task.startedAt,
        elapsedSeconds = task.elapsedSeconds,
        isTimerRunning = task.isTimerRunning
      )
    }
  }
}
