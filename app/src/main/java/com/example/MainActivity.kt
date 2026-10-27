package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.TaskActionBottomSheet
import com.example.ui.components.TaskEasyBottomBar
import com.example.ui.screens.AddEditTaskScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TaskDetailsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.TaskEasyTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.TaskFlowViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: TaskFlowViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      TaskEasyApp(viewModel = viewModel)
    }
  }
}

@Composable
fun TaskEasyApp(viewModel: TaskFlowViewModel = viewModel()) {
  val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
  val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val priorityFilter by viewModel.priorityFilter.collectAsStateWithLifecycle()
  val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()
  val dueFilter by viewModel.dueFilter.collectAsStateWithLifecycle()
  val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()

  val editingTask by viewModel.editingTask.collectAsStateWithLifecycle()
  val presetDueDate by viewModel.presetDueDate.collectAsStateWithLifecycle()
  val selectedDetailTaskId by viewModel.selectedDetailTaskId.collectAsStateWithLifecycle()
  val activeActionSheetTask by viewModel.activeActionSheetTask.collectAsStateWithLifecycle()
  val taskToDelete by viewModel.taskToDelete.collectAsStateWithLifecycle()
  val showFilterSheet by viewModel.showFilterSheet.collectAsStateWithLifecycle()

  val calendarSelectedDate by viewModel.calendarSelectedDate.collectAsStateWithLifecycle()
  val calendarMonth by viewModel.calendarMonth.collectAsStateWithLifecycle()
  val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(snackbarMessage) {
    if (snackbarMessage != null) {
      snackbarHostState.showSnackbar(snackbarMessage!!)
      viewModel.clearToast()
    }
  }

  TaskEasyTheme(themeMode = themeMode) {
    val isDark = LocalIsDarkTheme.current
    val bgColor = if (isDark) BackgroundDark else BackgroundLight

    // Handle Android system back press
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.SPLASH) {
      viewModel.navigateBack()
    }

    if (currentScreen == AppScreen.SPLASH) {
      SplashScreen(
        onGetStarted = { viewModel.dismissSplash() }
      )
    } else {
      val isSubScreen = currentScreen == AppScreen.ADD_EDIT_TASK || currentScreen == AppScreen.TASK_DETAILS

      Scaffold(
        modifier = Modifier
          .fillMaxSize()
          .background(bgColor),
        containerColor = bgColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
          if (!isSubScreen) {
            TaskEasyBottomBar(
              currentScreen = currentScreen,
              onNavigate = { screen -> viewModel.navigateTo(screen) },
              onAddClick = { viewModel.openAddTask() }
            )
          }
        }
      ) { innerPadding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
          AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ScreenTransition"
          ) { screen ->
            when (screen) {
              AppScreen.SPLASH -> {
                SplashScreen(onGetStarted = { viewModel.dismissSplash() })
              }

              AppScreen.DASHBOARD -> {
                DashboardScreen(
                  tasks = tasks,
                  onSeeAllClick = {
                    viewModel.navigateTo(AppScreen.TASKS)
                  },
                  onTaskClick = { task -> viewModel.openTaskDetails(task) },
                  onTaskMenuClick = { task -> viewModel.openActionSheet(task) },
                  onToggleTheme = { viewModel.toggleTheme() },
                  onTabSelect = { status ->
                    viewModel.selectTab(status)
                    viewModel.navigateTo(AppScreen.TASKS)
                  }
                )
              }

              AppScreen.TASKS -> {
                TasksScreen(
                  tasks = tasks,
                  selectedTab = selectedTab,
                  searchQuery = searchQuery,
                  priorityFilter = priorityFilter,
                  categoryFilter = categoryFilter,
                  dueFilter = dueFilter,
                  sortOption = sortOption,
                  onTabChange = { status -> viewModel.selectTab(status) },
                  onSearchChange = { query -> viewModel.setSearchQuery(query) },
                  onOpenFilterSheet = { viewModel.openFilterSheet() },
                  onClearFilters = { viewModel.clearFilters() },
                  onTaskClick = { task -> viewModel.openTaskDetails(task) },
                  onStartTask = { task -> viewModel.startTask(task) },
                  onBackToTodo = { task -> viewModel.moveBackToTodo(task) },
                  onCompleteTask = { task -> viewModel.completeTask(task) },
                  onReopenTask = { task -> viewModel.reopenTask(task) },
                  onTaskMenuClick = { task -> viewModel.openActionSheet(task) },
                  onToggleTimer = { task -> viewModel.toggleTaskTimer(task) }
                )
              }

              AppScreen.CALENDAR -> {
                CalendarScreen(
                  tasks = tasks,
                  selectedDate = calendarSelectedDate,
                  currentMonthCal = calendarMonth,
                  onDateSelected = { dateStr -> viewModel.selectCalendarDate(dateStr) },
                  onPrevMonth = { viewModel.prevCalendarMonth() },
                  onNextMonth = { viewModel.nextCalendarMonth() },
                  onTodayClick = { viewModel.resetCalendarToToday() },
                  onAddTaskForDate = { dateStr -> viewModel.openAddTask(dateStr) },
                  onTaskClick = { task -> viewModel.openTaskDetails(task) },
                  onTaskMenuClick = { task -> viewModel.openActionSheet(task) }
                )
              }

              AppScreen.SETTINGS -> {
                SettingsScreen(
                  themeMode = themeMode,
                  onThemeChange = { mode -> viewModel.setThemeMode(mode) },
                  onExportData = { viewModel.exportDataJson() },
                  onImportData = { json, onSuccess, onError ->
                    viewModel.importDataJson(json, onSuccess, onError)
                  },
                  onClearAllData = { viewModel.clearAllData() },
                  onBack = { viewModel.navigateBack() }
                )
              }

              AppScreen.ADD_EDIT_TASK -> {
                AddEditTaskScreen(
                  taskToEdit = editingTask,
                  presetDueDate = presetDueDate,
                  onSave = { title, desc, priority, category, dueDate, subtasks ->
                    viewModel.saveTask(title, desc, priority, category, dueDate, subtasks)
                  },
                  onCancel = { viewModel.navigateBack() }
                )
              }

              AppScreen.TASK_DETAILS -> {
                val detailTask = tasks.find { it.id == selectedDetailTaskId }
                TaskDetailsScreen(
                  task = detailTask,
                  onBack = { viewModel.navigateBack() },
                  onEdit = { task -> viewModel.openEditTask(task) },
                  onDelete = { task -> viewModel.confirmDeleteTask(task) },
                  onMenuClick = { task -> viewModel.openActionSheet(task) },
                  onStartTask = { task -> viewModel.startTask(task) },
                  onCompleteTask = { task -> viewModel.completeTask(task) },
                  onReopenTask = { task -> viewModel.reopenTask(task) },
                  onToggleSubtask = { task, subId -> viewModel.toggleSubtask(task, subId) },
                  onAddSubtask = { task, text -> viewModel.addSubtaskToActiveTask(task, text) },
                  onToggleTimer = { task -> viewModel.toggleTaskTimer(task) }
                )
              }
            }
          }
        }
      }

      // --- Modals & Sheets ---

      // Task Action Bottom Sheet
      if (activeActionSheetTask != null) {
        val sheetTask = tasks.find { it.id == activeActionSheetTask!!.id } ?: activeActionSheetTask!!
        TaskActionBottomSheet(
          task = sheetTask,
          onDismiss = { viewModel.closeActionSheet() },
          onStartTask = { viewModel.startTask(sheetTask) },
          onMoveToTodo = { viewModel.moveBackToTodo(sheetTask) },
          onMarkCompleted = { viewModel.completeTask(sheetTask) },
          onReopenTask = { viewModel.reopenTask(sheetTask) },
          onEditTask = { viewModel.openEditTask(sheetTask) },
          onDuplicateTask = { viewModel.duplicateTask(sheetTask) },
          onDeleteTask = { viewModel.confirmDeleteTask(sheetTask) }
        )
      }

      // Filter Bottom Sheet
      if (showFilterSheet) {
        FilterBottomSheet(
          selectedPriority = priorityFilter,
          selectedCategory = categoryFilter,
          selectedDue = dueFilter,
          selectedSort = sortOption,
          onPriorityChange = { p -> viewModel.setPriorityFilter(p) },
          onCategoryChange = { c -> viewModel.setCategoryFilter(c) },
          onDueChange = { d -> viewModel.setDueFilter(d) },
          onSortChange = { s -> viewModel.setSortOption(s) },
          onClearFilters = { viewModel.clearFilters() },
          onDismiss = { viewModel.closeFilterSheet() }
        )
      }

      // Delete Task Confirmation Dialog
      if (taskToDelete != null) {
        AlertDialog(
          onDismissRequest = { viewModel.dismissDeleteDialog() },
          title = { Text("Delete Task?", fontWeight = FontWeight.Bold, color = PriorityHigh) },
          text = {
            Text(
              "Are you sure you want to permanently delete \"${taskToDelete!!.title}\"?",
              fontSize = 14.sp
            )
          },
          confirmButton = {
            Button(
              onClick = { viewModel.deleteTask(taskToDelete!!) },
              colors = ButtonDefaults.buttonColors(containerColor = PriorityHigh)
            ) {
              Text("Delete")
            }
          },
          dismissButton = {
            TextButton(onClick = { viewModel.dismissDeleteDialog() }) {
              Text("Cancel")
            }
          }
        )
      }
    }
  }
}
