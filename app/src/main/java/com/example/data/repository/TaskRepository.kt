package com.example.data.repository

import com.example.data.local.TaskDao
import com.example.data.local.TaskEntity
import com.example.data.model.Priority
import com.example.data.model.Subtask
import com.example.data.model.TaskItem
import com.example.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class TaskRepository(private val taskDao: TaskDao) {

  val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks().map { entities ->
    entities.map { it.toTaskItem() }
  }

  suspend fun insertTask(task: TaskItem) {
    taskDao.insertTask(TaskEntity.fromTaskItem(task))
  }

  suspend fun updateTask(task: TaskItem) {
    val updated = task.copy(updatedAt = System.currentTimeMillis())
    taskDao.updateTask(TaskEntity.fromTaskItem(updated))
  }

  suspend fun deleteTask(id: String) {
    taskDao.deleteTaskById(id)
  }

  suspend fun duplicateTask(task: TaskItem) {
    val duplicated = TaskItem(
      id = UUID.randomUUID().toString(),
      title = "${task.title} (Copy)",
      description = task.description,
      status = TaskStatus.TODO,
      priority = task.priority,
      category = task.category,
      dueDate = task.dueDate,
      createdAt = System.currentTimeMillis(),
      updatedAt = System.currentTimeMillis(),
      completedAt = null,
      subtasks = task.subtasks.map { it.copy(id = UUID.randomUUID().toString(), isCompleted = false) }
    )
    taskDao.insertTask(TaskEntity.fromTaskItem(duplicated))
  }

  suspend fun moveTask(task: TaskItem, newStatus: TaskStatus) {
    val now = System.currentTimeMillis()
    val accumulated = task.currentElapsedSeconds(now)

    val updated = when (newStatus) {
      TaskStatus.IN_PROGRESS -> {
        task.copy(
          status = newStatus,
          startedAt = now,
          elapsedSeconds = accumulated,
          isTimerRunning = true,
          completedAt = null,
          updatedAt = now
        )
      }
      TaskStatus.COMPLETED -> {
        task.copy(
          status = newStatus,
          startedAt = null,
          elapsedSeconds = accumulated,
          isTimerRunning = false,
          completedAt = task.completedAt ?: now,
          updatedAt = now
        )
      }
      TaskStatus.TODO -> {
        task.copy(
          status = newStatus,
          startedAt = null,
          elapsedSeconds = accumulated,
          isTimerRunning = false,
          completedAt = null,
          updatedAt = now
        )
      }
    }
    taskDao.updateTask(TaskEntity.fromTaskItem(updated))
  }

  suspend fun toggleTimer(task: TaskItem) {
    val now = System.currentTimeMillis()
    val updated = if (task.isTimerRunning && task.startedAt != null) {
      val accumulated = task.currentElapsedSeconds(now)
      task.copy(
        isTimerRunning = false,
        startedAt = null,
        elapsedSeconds = accumulated,
        updatedAt = now
      )
    } else {
      task.copy(
        isTimerRunning = true,
        startedAt = now,
        status = if (task.status == TaskStatus.TODO) TaskStatus.IN_PROGRESS else task.status,
        updatedAt = now
      )
    }
    taskDao.updateTask(TaskEntity.fromTaskItem(updated))
  }

  suspend fun clearAllTasks() {
    taskDao.deleteAllTasks()
  }

  suspend fun exportToJson(tasks: List<TaskItem>): String {
    val root = JSONObject()
    root.put("version", 1)
    root.put("exportedAt", System.currentTimeMillis())
    val array = JSONArray()
    tasks.forEach { array.put(it.toJson()) }
    root.put("tasks", array)
    return root.toString(2)
  }

  suspend fun importFromJson(jsonString: String): Result<Int> {
    return try {
      val root = JSONObject(jsonString)
      val tasksArray = if (root.has("tasks")) {
        root.getJSONArray("tasks")
      } else {
        JSONArray(jsonString)
      }

      val importedTasks = mutableListOf<TaskEntity>()
      for (i in 0 until tasksArray.length()) {
        val obj = tasksArray.getJSONObject(i)
        val taskItem = TaskItem.fromJson(obj)
        importedTasks.add(TaskEntity.fromTaskItem(taskItem))
      }

      if (importedTasks.isNotEmpty()) {
        taskDao.deleteAllTasks()
        taskDao.insertAll(importedTasks)
      }
      Result.success(importedTasks.size)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun purgeMockDataIfPresent() {
    val mockTitles = setOf(
      "Prepare presentation",
      "Buy groceries",
      "Read a book",
      "Call family",
      "Design mobile app",
      "Exercise (30 minutes)",
      "Learn Arabic",
      "Pay electricity bill",
      "Morning walk",
      "Call friend",
      "Organize photos"
    )
    val existingTasks = taskDao.getAllTasksSync()
    existingTasks.filter { it.title in mockTitles }.forEach {
      taskDao.deleteTaskById(it.id)
    }
  }

  suspend fun seedInitialDataIfNeeded() {
    // No mock data - production deployment starts completely clean
    purgeMockDataIfPresent()
  }
}
