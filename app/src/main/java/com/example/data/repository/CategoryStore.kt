package com.example.data.repository

import android.content.SharedPreferences
import org.json.JSONArray

object CategoryStore {
  private const val PREF_KEY_CATEGORIES = "task_categories_json"
  const val MAX_CATEGORY_LENGTH = 30

  val DEFAULT_CATEGORIES = listOf("Personal", "Work", "Family", "Shopping", "Others")

  fun loadCategories(prefs: SharedPreferences): List<String> {
    val raw = prefs.getString(PREF_KEY_CATEGORIES, null) ?: return DEFAULT_CATEGORIES
    return try {
      val array = JSONArray(raw)
      val loaded = mutableListOf<String>()
      for (i in 0 until array.length()) {
        val item = array.optString(i, "").trim()
        if (item.isNotEmpty() && loaded.none { it.equals(item, ignoreCase = true) }) {
          loaded.add(item)
        }
      }
      if (loaded.isEmpty()) DEFAULT_CATEGORIES else loaded
    } catch (_: Exception) {
      DEFAULT_CATEGORIES
    }
  }

  fun saveCategories(prefs: SharedPreferences, categories: List<String>) {
    val array = JSONArray()
    categories.forEach { array.put(it) }
    prefs.edit().putString(PREF_KEY_CATEGORIES, array.toString()).apply()
  }

  fun validateCategoryName(
    rawName: String,
    existing: List<String>,
    excludingName: String? = null
  ): Result<String> {
    val trimmed = rawName.trim().replace(Regex("\\s+"), " ")
    if (trimmed.isEmpty()) {
      return Result.failure(IllegalArgumentException("Category name cannot be empty"))
    }
    if (trimmed.length > MAX_CATEGORY_LENGTH) {
      return Result.failure(
        IllegalArgumentException("Category name must be $MAX_CATEGORY_LENGTH characters or less")
      )
    }
    val duplicate = existing.any {
      !it.equals(excludingName, ignoreCase = true) && it.equals(trimmed, ignoreCase = true)
    }
    if (duplicate) {
      return Result.failure(IllegalArgumentException("Category \"$trimmed\" already exists"))
    }
    return Result.success(trimmed)
  }

  fun addCategory(
    prefs: SharedPreferences,
    current: List<String>,
    rawName: String
  ): Result<Pair<List<String>, String>> {
    val valid = validateCategoryName(rawName, current)
    return valid.map { cleanName ->
      val updated = current + cleanName
      saveCategories(prefs, updated)
      updated to cleanName
    }
  }

  fun renameCategory(
    prefs: SharedPreferences,
    current: List<String>,
    oldName: String,
    rawNewName: String
  ): Result<Pair<List<String>, String>> {
    if (current.none { it.equals(oldName, ignoreCase = true) }) {
      return Result.failure(IllegalArgumentException("Category not found"))
    }
    val valid = validateCategoryName(rawNewName, current, excludingName = oldName)
    return valid.map { cleanNewName ->
      val updated = current.map {
        if (it.equals(oldName, ignoreCase = true)) cleanNewName else it
      }
      saveCategories(prefs, updated)
      updated to cleanNewName
    }
  }

  fun deleteCategory(
    prefs: SharedPreferences,
    current: List<String>,
    targetName: String
  ): Result<Pair<List<String>, String>> {
    if (current.size <= 1) {
      return Result.failure(IllegalStateException("Keep at least one category"))
    }
    if (current.none { it.equals(targetName, ignoreCase = true) }) {
      return Result.failure(IllegalArgumentException("Category not found"))
    }
    val updated = current.filterNot { it.equals(targetName, ignoreCase = true) }
    val fallback = updated.find { it.equals("Others", ignoreCase = true) } ?: updated.first()
    saveCategories(prefs, updated)
    return Result.success(updated to fallback)
  }
}
