package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
  fun getAllTasks(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
  suspend fun getAllTasksSync(): List<TaskEntity>

  @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
  suspend fun getTaskById(id: String): TaskEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(tasks: List<TaskEntity>)

  @Update
  suspend fun updateTask(task: TaskEntity)

  @Query("DELETE FROM tasks WHERE id = :id")
  suspend fun deleteTaskById(id: String)

  @Query("DELETE FROM tasks")
  suspend fun deleteAllTasks()

  @Query("UPDATE tasks SET category = :newCategory, updatedAt = :updatedAt WHERE LOWER(category) = LOWER(:oldCategory)")
  suspend fun updateTasksCategory(oldCategory: String, newCategory: String, updatedAt: Long)

  @Query("SELECT COUNT(*) FROM tasks")
  suspend fun getTaskCount(): Int
}
