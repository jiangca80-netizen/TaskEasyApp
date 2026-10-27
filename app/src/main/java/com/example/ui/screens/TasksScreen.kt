package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.data.model.TaskItem
import com.example.data.model.TaskStatus
import com.example.ui.components.TaskCard
import com.example.ui.components.TaskEasyLogo
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.DarkNavyText
import com.example.ui.theme.DarkTabInactive
import com.example.ui.theme.InProgressOrange
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TodoBlue
import com.example.ui.viewmodel.DueFilter
import com.example.ui.viewmodel.SortOption
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TasksScreen(
  tasks: List<TaskItem>,
  selectedTab: TaskStatus,
  searchQuery: String,
  priorityFilter: Priority?,
  categoryFilter: String?,
  dueFilter: DueFilter,
  sortOption: SortOption,
  onTabChange: (TaskStatus) -> Unit,
  onSearchChange: (String) -> Unit,
  onOpenFilterSheet: () -> Unit,
  onClearFilters: () -> Unit,
  onTaskClick: (TaskItem) -> Unit,
  onStartTask: (TaskItem) -> Unit,
  onBackToTodo: (TaskItem) -> Unit,
  onCompleteTask: (TaskItem) -> Unit,
  onReopenTask: (TaskItem) -> Unit,
  onTaskMenuClick: (TaskItem) -> Unit,
  onToggleTimer: ((TaskItem) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current

  val todoCount = tasks.count { it.status == TaskStatus.TODO }
  val inProgressCount = tasks.count { it.status == TaskStatus.IN_PROGRESS }
  val completedCount = tasks.count { it.status == TaskStatus.COMPLETED }

  val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

  val filteredTasks = tasks.filter { task ->
    task.status == selectedTab &&
      (searchQuery.isBlank() ||
        task.title.contains(searchQuery, ignoreCase = true) ||
        task.description.contains(searchQuery, ignoreCase = true)) &&
      (priorityFilter == null || task.priority == priorityFilter) &&
      (categoryFilter == null || task.category.equals(categoryFilter, ignoreCase = true)) &&
      when (dueFilter) {
        DueFilter.ALL -> true
        DueFilter.TODAY -> task.dueDate == todayStr
        DueFilter.OVERDUE -> task.dueDate != null && task.dueDate < todayStr && task.status != TaskStatus.COMPLETED
      }
  }.let { list ->
    when (sortOption) {
      SortOption.NEWEST -> list.sortedByDescending { if (selectedTab == TaskStatus.COMPLETED) (it.completedAt ?: it.updatedAt) else it.createdAt }
      SortOption.OLDEST -> list.sortedBy { it.createdAt }
      SortOption.DUE_DATE -> list.sortedWith(compareBy(nullsLast()) { it.dueDate })
      SortOption.PRIORITY -> list.sortedBy {
        when (it.priority) {
          Priority.HIGH -> 0
          Priority.MEDIUM -> 1
          Priority.LOW -> 2
        }
      }
    }
  }

  val hasActiveFilters = priorityFilter != null || categoryFilter != null || dueFilter != DueFilter.ALL

  Column(
    modifier = modifier.fillMaxSize()
  ) {
    // Top Bar: TaskEasy centered, filter button at right
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp)
        .padding(horizontal = 16.dp)
    ) {
      Box(modifier = Modifier.align(Alignment.Center)) {
        TaskEasyLogo(fontSize = 22.sp)
      }

      IconButton(
        onClick = onOpenFilterSheet,
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .size(36.dp)
          .testTag("open_filter_button")
      ) {
        BadgedBox(
          badge = {
            if (hasActiveFilters) {
              Badge(
                containerColor = InProgressOrange,
                modifier = Modifier.size(8.dp)
              )
            }
          }
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Filters",
            tint = PrimaryBlue,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }

    // Search Bar with explicit text styling for high contrast in dark and light modes
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchChange,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
        .testTag("task_search_field"),
      placeholder = { Text("Search tasks...", color = SlateGray, fontSize = 14.sp) },
      textStyle = TextStyle(
        color = if (isDark) Color.White else DarkNavyText,
        fontSize = 14.sp
      ),
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = SlateGray,
          modifier = Modifier.size(20.dp)
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onSearchChange("") }) {
            Icon(
              imageVector = Icons.Default.Clear,
              contentDescription = "Clear search",
              tint = SlateGray,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(24.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = if (isDark) Color.White else DarkNavyText,
        unfocusedTextColor = if (isDark) Color.White else DarkNavyText,
        focusedContainerColor = if (isDark) SurfaceDark else SurfaceLight,
        unfocusedContainerColor = if (isDark) SurfaceDark else SurfaceLight,
        cursorColor = PrimaryBlue,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = if (isDark) BorderDark else BorderLight
      )
    )

    Spacer(modifier = Modifier.height(10.dp))

    // 3 Pill Tabs: To Do (N), In Progress (N), Completed (N) with distinct number boxes
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      PillTabItem(
        label = "To Do",
        count = todoCount,
        selected = selectedTab == TaskStatus.TODO,
        statusColor = Color(0xFF0072F5),
        onClick = { onTabChange(TaskStatus.TODO) },
        modifier = Modifier.weight(0.85f).testTag("tab_todo")
      )

      PillTabItem(
        label = "In Progress",
        count = inProgressCount,
        selected = selectedTab == TaskStatus.IN_PROGRESS,
        statusColor = Color(0xFFFF9500),
        onClick = { onTabChange(TaskStatus.IN_PROGRESS) },
        modifier = Modifier.weight(1.45f).testTag("tab_in_progress")
      )

      PillTabItem(
        label = "Completed",
        count = completedCount,
        selected = selectedTab == TaskStatus.COMPLETED,
        statusColor = Color(0xFF10B981),
        onClick = { onTabChange(TaskStatus.COMPLETED) },
        modifier = Modifier.weight(1.20f).testTag("tab_completed")
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Active Filters Indicator Row
    if (hasActiveFilters) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val filterDesc = buildString {
          if (priorityFilter != null) append("${priorityFilter.displayName} Priority • ")
          if (categoryFilter != null) append("$categoryFilter • ")
          if (dueFilter != DueFilter.ALL) append("${dueFilter.name} • ")
        }.removeSuffix(" • ")

        Text(
          text = "Filtered by: $filterDesc",
          fontSize = 12.sp,
          color = PrimaryBlue,
          fontWeight = FontWeight.Medium
        )

        TextButton(onClick = onClearFilters) {
          Text("Clear", fontSize = 12.sp, color = InProgressOrange)
        }
      }
    } else {
      Spacer(modifier = Modifier.height(8.dp))
    }

    // Task List
    if (filteredTasks.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 80.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(24.dp)
        ) {
          Text(
            text = when {
              searchQuery.isNotEmpty() -> "No tasks matching \"$searchQuery\""
              hasActiveFilters -> "No tasks match the active filters"
              selectedTab == TaskStatus.TODO -> "No tasks in To Do"
              selectedTab == TaskStatus.IN_PROGRESS -> "No tasks in progress"
              else -> "No completed tasks yet"
            },
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = if (isDark) TextDarkPrimary else DarkNavyText
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = when (selectedTab) {
              TaskStatus.TODO -> "Tap + below to add a new task."
              TaskStatus.IN_PROGRESS -> "Click 'Start Task' on any To Do task to begin."
              TaskStatus.COMPLETED -> "Finish in-progress tasks to see them here."
            },
            fontSize = 13.sp,
            color = SlateGray
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        items(filteredTasks, key = { it.id }) { task ->
          TaskCard(
            task = task,
            onCardClick = { onTaskClick(task) },
            onStartClick = { onStartTask(task) },
            onBackToTodoClick = { onBackToTodo(task) },
            onCompleteClick = { onCompleteTask(task) },
            onReopenClick = { onReopenTask(task) },
            onMenuClick = { onTaskMenuClick(task) },
            onToggleTimerClick = { onToggleTimer?.invoke(task) }
          )
        }

        item {
          Spacer(modifier = Modifier.height(80.dp))
        }
      }
    }
  }
}

@Composable
private fun PillTabItem(
  label: String,
  count: Int,
  selected: Boolean,
  statusColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val tabBg = if (selected) {
    if (isDark) statusColor.copy(alpha = 0.22f) else statusColor.copy(alpha = 0.12f)
  } else {
    if (isDark) DarkTabInactive else Color(0xFFF1F5F9)
  }
  val labelColor = if (selected) {
    if (isDark) Color.White else statusColor
  } else {
    if (isDark) TextDarkPrimary else DarkNavyText
  }
  val borderModifier = if (selected) {
    Modifier.border(BorderStroke(1.5.dp, statusColor), RoundedCornerShape(14.dp))
  } else {
    Modifier.border(
      BorderStroke(1.dp, if (isDark) BorderDark else BorderLight.copy(alpha = 0.7f)),
      RoundedCornerShape(14.dp)
    )
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .then(borderModifier)
      .background(tabBg)
      .clickable { onClick() }
      .padding(vertical = 7.dp, horizontal = 2.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text(
        text = label,
        color = labelColor,
        fontSize = 11.5.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
        maxLines = 1
      )

      Spacer(modifier = Modifier.width(4.dp))

      // Circular number badge matching To Do's circular style with distinct status color
      Box(
        modifier = Modifier
          .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
          .clip(CircleShape)
          .background(statusColor)
          .padding(horizontal = 4.dp, vertical = 1.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "$count",
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = Color.White,
          maxLines = 1
        )
      }
    }
  }
}
