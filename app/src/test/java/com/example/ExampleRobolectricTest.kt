package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TaskEasy", appName)
  }

  @Test
  fun `task item computes elapsed time correctly`() {
    val now = 1000000L
    val taskRunning = com.example.data.model.TaskItem(
      id = "test-1",
      title = "Test Task",
      status = com.example.data.model.TaskStatus.IN_PROGRESS,
      startedAt = now - 65000L, // started 65s ago
      elapsedSeconds = 10L,
      isTimerRunning = true
    )
    val elapsed = taskRunning.currentElapsedSeconds(now)
    assertEquals(75L, elapsed)
  }

  @Test
  fun `task item json serialization preserves timer fields`() {
    val task = com.example.data.model.TaskItem(
      id = "test-2",
      title = "Timer Task",
      startedAt = 5000L,
      elapsedSeconds = 120L,
      isTimerRunning = true
    )
    val json = task.toJson()
    val restored = com.example.data.model.TaskItem.fromJson(json)
    assertEquals(task.startedAt, restored.startedAt)
    assertEquals(task.elapsedSeconds, restored.elapsedSeconds)
    assertEquals(task.isTimerRunning, restored.isTimerRunning)
  }

  @Test
  fun `exportAllTasksToJson and importFromJson round trip preserves all task data`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.TaskDatabase::class.java
    ).allowMainThreadQueries().build()

    try {
      val repo = com.example.data.repository.TaskRepository(db.taskDao())
      val originalTasks = listOf(
        com.example.data.model.TaskItem(
          id = "task-1",
          title = "Quarterly Report",
          description = "Compile numbers and charts",
          status = com.example.data.model.TaskStatus.IN_PROGRESS,
          priority = com.example.data.model.Priority.HIGH,
          category = "Work",
          dueDate = "2026-12-01",
          startedAt = 1700000000000L,
          elapsedSeconds = 245L,
          isTimerRunning = true,
          subtasks = listOf(
            com.example.data.model.Subtask(id = "sub-1", text = "Gather CSVs", isCompleted = true),
            com.example.data.model.Subtask(id = "sub-2", text = "Write summary", isCompleted = false)
          )
        ),
        com.example.data.model.TaskItem(
          id = "task-2",
          title = "Grocery Run",
          description = "",
          status = com.example.data.model.TaskStatus.COMPLETED,
          priority = com.example.data.model.Priority.LOW,
          category = "Shopping",
          completedAt = 1700001000000L
        )
      )

      originalTasks.forEach { repo.insertTask(it) }

      val exportedJson = repo.exportAllTasksToJson()
      val root = org.json.JSONObject(exportedJson)
      assertEquals("TaskEasy", root.getString("app"))
      assertEquals(2, root.getInt("taskCount"))
      assertEquals(2, root.getJSONArray("tasks").length())

      repo.clearAllTasks()
      assertEquals(0, db.taskDao().getTaskCount())

      val importResult = repo.importFromJson("\uFEFF$exportedJson")
      org.junit.Assert.assertTrue(importResult.isSuccess)
      assertEquals(2, importResult.getOrNull())

      val restored = db.taskDao().getAllTasksSync().map { it.toTaskItem() }.associateBy { it.id }
      assertEquals(2, restored.size)

      val restoredFirst = restored["task-1"]!!
      assertEquals("Quarterly Report", restoredFirst.title)
      assertEquals("Compile numbers and charts", restoredFirst.description)
      assertEquals(com.example.data.model.TaskStatus.IN_PROGRESS, restoredFirst.status)
      assertEquals(com.example.data.model.Priority.HIGH, restoredFirst.priority)
      assertEquals("Work", restoredFirst.category)
      assertEquals("2026-12-01", restoredFirst.dueDate)
      assertEquals(1700000000000L, restoredFirst.startedAt)
      assertEquals(245L, restoredFirst.elapsedSeconds)
      assertEquals(true, restoredFirst.isTimerRunning)
      assertEquals(2, restoredFirst.subtasks.size)
      assertEquals(true, restoredFirst.subtasks[0].isCompleted)
      assertEquals(false, restoredFirst.subtasks[1].isCompleted)
    } finally {
      db.close()
    }
  }

  @Test
  fun `importFromJson supports raw array and rejects empty or invalid json`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.TaskDatabase::class.java
    ).allowMainThreadQueries().build()

    try {
      val repo = com.example.data.repository.TaskRepository(db.taskDao())

      val emptyResult = repo.importFromJson("   ")
      org.junit.Assert.assertTrue(emptyResult.isFailure)

      val invalidResult = repo.importFromJson("""{"version": 1}""")
      org.junit.Assert.assertTrue(invalidResult.isFailure)

      val rawArrayJson = org.json.JSONArray().apply {
        put(com.example.data.model.TaskItem(id = "raw-1", title = "Raw Array Task").toJson())
      }.toString()
      val rawResult = repo.importFromJson(rawArrayJson)
      org.junit.Assert.assertTrue(rawResult.isSuccess)
      assertEquals(1, rawResult.getOrNull())
      assertEquals(1, db.taskDao().getTaskCount())
    } finally {
      db.close()
    }
  }

  @Test
  fun `taskPdfExporter builds structured report sections for tasks`() {
    val tasks = listOf(
      com.example.data.model.TaskItem(
        id = "t1",
        title = "Very Long Task Title That Should Wrap Cleanly Across Multiple Lines In PDF",
        description = "Detailed task notes for verification",
        status = com.example.data.model.TaskStatus.IN_PROGRESS,
        priority = com.example.data.model.Priority.HIGH,
        category = "Work",
        dueDate = "2026-12-01",
        elapsedSeconds = 180L,
        subtasks = listOf(
          com.example.data.model.Subtask("s1", "First subtask", isCompleted = true),
          com.example.data.model.Subtask("s2", "Second subtask", isCompleted = false)
        )
      ),
      com.example.data.model.TaskItem(
        id = "t2",
        title = "Finish Presentation",
        status = com.example.data.model.TaskStatus.COMPLETED,
        completedAt = 1700001000000L
      )
    )

    val report = com.example.util.TaskPdfExporter.buildReportModel(tasks = tasks, nowMillis = 1700002000000L)
    org.junit.Assert.assertTrue(report.summaryLine.contains("Total Tasks: 2"))
    assertEquals(2, report.sections.size)
    assertEquals("In Progress Tasks (1)", report.sections[0].heading)
    assertEquals("Completed Tasks (1)", report.sections[1].heading)
    org.junit.Assert.assertTrue(report.sections[0].entries[0].bodyLines.any { it.contains("[✓] First subtask") })
  }
}


