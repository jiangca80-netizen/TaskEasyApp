package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

enum class TaskStatus(val value: String, val displayName: String) {
  TODO("todo", "To Do"),
  IN_PROGRESS("in_progress", "In Progress"),
  COMPLETED("completed", "Completed");

  companion object {
    fun fromString(value: String?): TaskStatus {
      return entries.find { it.value.equals(value, ignoreCase = true) } ?: TODO
    }
  }
}

enum class Priority(val value: String, val displayName: String) {
  HIGH("high", "High"),
  MEDIUM("medium", "Medium"),
  LOW("low", "Low");

  companion object {
    fun fromString(value: String?): Priority {
      return entries.find { it.value.equals(value, ignoreCase = true) } ?: MEDIUM
    }
  }
}

data class Subtask(
  val id: String,
  val text: String,
  val isCompleted: Boolean = false
) {
  fun toJson(): JSONObject {
    val obj = JSONObject()
    obj.put("id", id)
    obj.put("text", text)
    obj.put("isCompleted", isCompleted)
    return obj
  }

  companion object {
    fun fromJson(obj: JSONObject): Subtask {
      return Subtask(
        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
        text = obj.optString("text", ""),
        isCompleted = obj.optBoolean("isCompleted", false)
      )
    }

    fun listToJson(list: List<Subtask>): String {
      val array = JSONArray()
      list.forEach { array.put(it.toJson()) }
      return array.toString()
    }

    fun listFromJson(jsonString: String?): List<Subtask> {
      if (jsonString.isNullOrBlank()) return emptyList()
      return try {
        val array = JSONArray(jsonString)
        val result = mutableListOf<Subtask>()
        for (i in 0 until array.length()) {
          result.add(fromJson(array.getJSONObject(i)))
        }
        result
      } catch (e: Exception) {
        emptyList()
      }
    }
  }
}

data class TaskItem(
  val id: String,
  val title: String,
  val description: String = "",
  val status: TaskStatus = TaskStatus.TODO,
  val priority: Priority = Priority.MEDIUM,
  val category: String = "Personal",
  val dueDate: String? = null, // "YYYY-MM-DD"
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val completedAt: Long? = null,
  val subtasks: List<Subtask> = emptyList(),
  val startedAt: Long? = null,
  val elapsedSeconds: Long = 0L,
  val isTimerRunning: Boolean = false
) {
  fun currentElapsedSeconds(now: Long = System.currentTimeMillis()): Long {
    return if (isTimerRunning && startedAt != null) {
      elapsedSeconds + ((now - startedAt) / 1000L).coerceAtLeast(0L)
    } else {
      elapsedSeconds
    }
  }

  fun toJson(): JSONObject {
    val obj = JSONObject()
    obj.put("id", id)
    obj.put("title", title)
    obj.put("description", description)
    obj.put("status", status.value)
    obj.put("priority", priority.value)
    obj.put("category", category)
    obj.put("dueDate", dueDate ?: JSONObject.NULL)
    obj.put("createdAt", createdAt)
    obj.put("updatedAt", updatedAt)
    obj.put("completedAt", completedAt ?: JSONObject.NULL)
    obj.put("startedAt", startedAt ?: JSONObject.NULL)
    obj.put("elapsedSeconds", elapsedSeconds)
    obj.put("isTimerRunning", isTimerRunning)
    val subtasksArray = JSONArray()
    subtasks.forEach { subtasksArray.put(it.toJson()) }
    obj.put("subtasks", subtasksArray)
    return obj
  }

  companion object {
    fun fromJson(obj: JSONObject): TaskItem {
      val subtasksList = mutableListOf<Subtask>()
      val subtasksArray = obj.optJSONArray("subtasks")
      if (subtasksArray != null) {
        for (i in 0 until subtasksArray.length()) {
          subtasksList.add(Subtask.fromJson(subtasksArray.getJSONObject(i)))
        }
      }

      val dueDateVal = if (obj.isNull("dueDate")) null else obj.optString("dueDate", null)
      val completedAtVal = if (obj.isNull("completedAt")) null else obj.optLong("completedAt")
      val startedAtVal = if (obj.isNull("startedAt")) null else obj.optLong("startedAt")

      return TaskItem(
        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
        title = obj.optString("title", "Untitled Task"),
        description = obj.optString("description", ""),
        status = TaskStatus.fromString(obj.optString("status")),
        priority = Priority.fromString(obj.optString("priority")),
        category = obj.optString("category", "Personal"),
        dueDate = dueDateVal,
        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
        updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
        completedAt = completedAtVal,
        subtasks = subtasksList,
        startedAt = startedAtVal,
        elapsedSeconds = obj.optLong("elapsedSeconds", 0L),
        isTimerRunning = obj.optBoolean("isTimerRunning", false)
      )
    }
  }
}
