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
}
