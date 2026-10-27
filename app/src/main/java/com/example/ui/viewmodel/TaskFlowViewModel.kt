package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TaskDatabase
import com.example.data.model.Priority
import com.example.data.model.Subtask
import com.example.data.model.TaskItem
import com.example.data.model.TaskStatus
import com.example.data.repository.TaskRepository
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class AppScreen {
  SPLASH,
  DASHBOARD,
  TASKS,
  CALENDAR,
  SETTINGS,
  ADD_EDIT_TASK,
  TASK_DETAILS
}

enum class DueFilter {
  ALL,
  TODAY,
  OVERDUE
}

enum class SortOption(val displayName: String) {
  NEWEST("Newest First"),
  OLDEST("Oldest First"),
  DUE_DATE("Due Date"),
  PRIORITY("Priority (High to Low)")
}

class TaskFlowViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: TaskRepository
  private val prefs = application.getSharedPreferences("taskflow_prefs", Context.MODE_PRIVATE)

  val allTasks: StateFlow<List<TaskItem>>

  private val _currentScreen = MutableStateFlow(
    if (prefs.getBoolean("has_seen_splash", false)) AppScreen.DASHBOARD else AppScreen.SPLASH
  )
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  // For back navigation history
  private val screenHistory = mutableListOf<AppScreen>()

  private val _selectedTab = MutableStateFlow(TaskStatus.TODO)
  val selectedTab: StateFlow<TaskStatus> = _selectedTab.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _priorityFilter = MutableStateFlow<Priority?>(null)
  val priorityFilter: StateFlow<Priority?> = _priorityFilter.asStateFlow()

  private val _categoryFilter = MutableStateFlow<String?>(null)
  val categoryFilter: StateFlow<String?> = _categoryFilter.asStateFlow()

  private val _dueFilter = MutableStateFlow(DueFilter.ALL)
  val dueFilter: StateFlow<DueFilter> = _dueFilter.asStateFlow()

  private val _sortOption = MutableStateFlow(SortOption.NEWEST)
  val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

  private val _themeMode = MutableStateFlow(
    when (prefs.getString("theme_mode", "SYSTEM")) {
      "LIGHT" -> AppThemeMode.LIGHT
      "DARK" -> AppThemeMode.DARK
      else -> AppThemeMode.SYSTEM
    }
  )
  val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

  // Task being edited (null = new task)
  private val _editingTask = MutableStateFlow<TaskItem?>(null)
  val editingTask: StateFlow<TaskItem?> = _editingTask.asStateFlow()

  // Pre-selected date when adding task from calendar
  private val _presetDueDate = MutableStateFlow<String?>(null)
  val presetDueDate: StateFlow<String?> = _presetDueDate.asStateFlow()

  // Task in details screen
  private val _selectedDetailTaskId = MutableStateFlow<String?>(null)
  val selectedDetailTaskId: StateFlow<String?> = _selectedDetailTaskId.asStateFlow()

  // Task in bottom sheet
  private val _activeActionSheetTask = MutableStateFlow<TaskItem?>(null)
  val activeActionSheetTask: StateFlow<TaskItem?> = _activeActionSheetTask.asStateFlow()

  // Dialog states
  private val _taskToDelete = MutableStateFlow<TaskItem?>(null)
  val taskToDelete: StateFlow<TaskItem?> = _taskToDelete.asStateFlow()

  private val _showClearAllConfirm = MutableStateFlow(false)
  val showClearAllConfirm: StateFlow<Boolean> = _showClearAllConfirm.asStateFlow()

  private val _showFilterSheet = MutableStateFlow(false)
  val showFilterSheet: StateFlow<Boolean> = _showFilterSheet.asStateFlow()

  // Calendar State
  private val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  private val _calendarSelectedDate = MutableStateFlow(sdfDate.format(Date()))
  val calendarSelectedDate: StateFlow<String> = _calendarSelectedDate.asStateFlow()

  private val _calendarMonth = MutableStateFlow(Calendar.getInstance())
  val calendarMonth: StateFlow<Calendar> = _calendarMonth.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  init {
    val database = TaskDatabase.getDatabase(application)
    repository = TaskRepository(database.taskDao())

    allTasks = repository.allTasks.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    viewModelScope.launch {
      repository.seedInitialDataIfNeeded()
    }
  }

  fun dismissSplash() {
    prefs.edit().putBoolean("has_seen_splash", true).apply()
    _currentScreen.value = AppScreen.DASHBOARD
  }

  fun navigateTo(screen: AppScreen) {
    if (_currentScreen.value != screen) {
      if (_currentScreen.value != AppScreen.SPLASH && _currentScreen.value != screen) {
        screenHistory.add(_currentScreen.value)
      }
      _currentScreen.value = screen
    }
  }

  fun navigateBack(): Boolean {
    if (screenHistory.isNotEmpty()) {
      val prev = screenHistory.removeAt(screenHistory.size - 1)
      _currentScreen.value = prev
      return true
    } else if (_currentScreen.value != AppScreen.DASHBOARD) {
      _currentScreen.value = AppScreen.DASHBOARD
      return true
    }
    return false
  }

  fun selectTab(status: TaskStatus) {
    _selectedTab.value = status
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setPriorityFilter(priority: Priority?) {
    _priorityFilter.value = priority
  }

  fun setCategoryFilter(category: String?) {
    _categoryFilter.value = category
  }

  fun setDueFilter(filter: DueFilter) {
    _dueFilter.value = filter
  }

  fun setSortOption(option: SortOption) {
    _sortOption.value = option
  }

  fun clearFilters() {
    _priorityFilter.value = null
    _categoryFilter.value = null
    _dueFilter.value = DueFilter.ALL
    _searchQuery.value = ""
    _sortOption.value = SortOption.NEWEST
  }

  fun openAddTask(presetDate: String? = null) {
    _editingTask.value = null
    _presetDueDate.value = presetDate
    navigateTo(AppScreen.ADD_EDIT_TASK)
  }

  fun openEditTask(task: TaskItem) {
    _editingTask.value = task
    _presetDueDate.value = task.dueDate
    navigateTo(AppScreen.ADD_EDIT_TASK)
  }

  fun openTaskDetails(task: TaskItem) {
    _selectedDetailTaskId.value = task.id
    navigateTo(AppScreen.TASK_DETAILS)
  }

  fun openActionSheet(task: TaskItem) {
    _activeActionSheetTask.value = task
  }

  fun closeActionSheet() {
    _activeActionSheetTask.value = null
  }

  fun openFilterSheet() {
    _showFilterSheet.value = true
  }

  fun closeFilterSheet() {
    _showFilterSheet.value = false
  }

  fun confirmDeleteTask(task: TaskItem) {
    _taskToDelete.value = task
  }

  fun dismissDeleteDialog() {
    _taskToDelete.value = null
  }

  fun confirmClearAll(show: Boolean) {
    _showClearAllConfirm.value = show
  }

  // --- Core Task Workflow Actions ---

  fun startTask(task: TaskItem) {
    viewModelScope.launch {
      repository.moveTask(task, TaskStatus.IN_PROGRESS)
      showToast("Started \"${task.title}\" ⏱️")
    }
  }

  fun toggleTaskTimer(task: TaskItem) {
    viewModelScope.launch {
      repository.toggleTimer(task)
      if (task.isTimerRunning) {
        showToast("Timer paused for \"${task.title}\"")
      } else {
        showToast("Timer running for \"${task.title}\"")
      }
    }
  }

  fun moveBackToTodo(task: TaskItem) {
    viewModelScope.launch {
      repository.moveTask(task, TaskStatus.TODO)
      showToast("Moved back to To Do")
    }
  }

  fun completeTask(task: TaskItem) {
    viewModelScope.launch {
      repository.moveTask(task, TaskStatus.COMPLETED)
      showToast("Completed \"${task.title}\" ✓")
    }
  }

  fun reopenTask(task: TaskItem) {
    viewModelScope.launch {
      repository.moveTask(task, TaskStatus.IN_PROGRESS)
      showToast("Reopened \"${task.title}\"")
    }
  }

  fun duplicateTask(task: TaskItem) {
    viewModelScope.launch {
      repository.duplicateTask(task)
      showToast("Duplicated task")
    }
  }

  fun deleteTask(task: TaskItem) {
    viewModelScope.launch {
      repository.deleteTask(task.id)
      _taskToDelete.value = null
      if (_selectedDetailTaskId.value == task.id) {
        navigateBack()
      }
      showToast("Task deleted")
    }
  }

  fun clearAllData() {
    viewModelScope.launch {
      repository.clearAllTasks()
      _showClearAllConfirm.value = false
      showToast("All tasks cleared")
    }
  }

  fun saveTask(
    title: String,
    description: String,
    priority: Priority,
    category: String,
    dueDate: String?,
    subtasks: List<Subtask>
  ) {
    viewModelScope.launch {
      val existing = _editingTask.value
      if (existing != null) {
        val updated = existing.copy(
          title = title.trim(),
          description = description.trim(),
          priority = priority,
          category = category,
          dueDate = dueDate,
          subtasks = subtasks,
          updatedAt = System.currentTimeMillis()
        )
        repository.updateTask(updated)
        showToast("Task updated")
      } else {
        val newTask = TaskItem(
          id = UUID.randomUUID().toString(),
          title = title.trim(),
          description = description.trim(),
          status = TaskStatus.TODO,
          priority = priority,
          category = category,
          dueDate = dueDate,
          createdAt = System.currentTimeMillis(),
          updatedAt = System.currentTimeMillis(),
          completedAt = null,
          subtasks = subtasks
        )
        repository.insertTask(newTask)
        showToast("Task created")
      }
      navigateBack()
    }
  }

  fun toggleSubtask(task: TaskItem, subtaskId: String) {
    val updatedSubtasks = task.subtasks.map {
      if (it.id == subtaskId) it.copy(isCompleted = !it.isCompleted) else it
    }
    viewModelScope.launch {
      repository.updateTask(task.copy(subtasks = updatedSubtasks))
    }
  }

  fun addSubtaskToActiveTask(task: TaskItem, text: String) {
    if (text.isBlank()) return
    val newSubtask = Subtask(id = UUID.randomUUID().toString(), text = text.trim(), isCompleted = false)
    val updatedList = task.subtasks + newSubtask
    viewModelScope.launch {
      repository.updateTask(task.copy(subtasks = updatedList))
    }
  }

  // --- Calendar ---

  fun selectCalendarDate(dateStr: String) {
    _calendarSelectedDate.value = dateStr
  }

  fun nextCalendarMonth() {
    val cal = _calendarMonth.value.clone() as Calendar
    cal.add(Calendar.MONTH, 1)
    _calendarMonth.value = cal
  }

  fun prevCalendarMonth() {
    val cal = _calendarMonth.value.clone() as Calendar
    cal.add(Calendar.MONTH, -1)
    _calendarMonth.value = cal
  }

  fun resetCalendarToToday() {
    val cal = Calendar.getInstance()
    _calendarMonth.value = cal
    _calendarSelectedDate.value = sdfDate.format(cal.time)
  }

  // --- Settings & Theme ---

  fun setThemeMode(mode: AppThemeMode) {
    _themeMode.value = mode
    prefs.edit().putString("theme_mode", mode.name).apply()
  }

  fun toggleTheme() {
    val nextMode = if (_themeMode.value == AppThemeMode.DARK) AppThemeMode.LIGHT else AppThemeMode.DARK
    setThemeMode(nextMode)
  }

  suspend fun exportDataJson(): String {
    return repository.exportToJson(allTasks.value)
  }

  fun importDataJson(jsonStr: String, onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
    viewModelScope.launch {
      val result = repository.importFromJson(jsonStr)
      result.onSuccess { count ->
        showToast("Successfully imported $count tasks")
        onSuccess(count)
      }.onFailure { err ->
        onError(err.localizedMessage ?: "Invalid JSON backup file")
      }
    }
  }

  fun showToast(msg: String) {
    _snackbarMessage.value = msg
  }

  fun clearToast() {
    _snackbarMessage.value = null
  }
}
