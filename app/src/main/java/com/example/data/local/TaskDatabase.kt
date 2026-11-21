package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

@Database(entities = [TaskEntity::class], version = 4, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
  abstract fun taskDao(): TaskDao

  companion object {
    @Volatile
    private var INSTANCE: TaskDatabase? = null

    /**
     * Add explicit Migration(oldVersion, newVersion) objects here whenever `version` increases
     * so existing users keep all tasks across app updates (never use destructive fallback).
     * Example for v4 -> v5:
     * val MIGRATION_4_5 = object : Migration(4, 5) {
     *   override fun migrate(db: SupportSQLiteDatabase) {
     *     db.execSQL("ALTER TABLE tasks ADD COLUMN newColumn TEXT NOT NULL DEFAULT ''")
     *   }
     * }
     */
    val ALL_MIGRATIONS: Array<Migration> = emptyArray()

    fun getDatabase(context: Context): TaskDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TaskDatabase::class.java,
          "taskflow_database"
        )
          .addMigrations(*ALL_MIGRATIONS)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
