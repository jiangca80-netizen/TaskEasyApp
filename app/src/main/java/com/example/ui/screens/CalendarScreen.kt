package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.data.model.TaskStatus
import com.example.ui.components.PriorityBadge
import com.example.ui.components.formatDateDisplay
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.InProgressOrange
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TodoBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(
  tasks: List<TaskItem>,
  selectedDate: String,
  currentMonthCal: Calendar,
  onDateSelected: (String) -> Unit,
  onPrevMonth: () -> Unit,
  onNextMonth: () -> Unit,
  onTodayClick: () -> Unit,
  onAddTaskForDate: (String) -> Unit,
  onTaskClick: (TaskItem) -> Unit,
  onTaskMenuClick: (TaskItem) -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current

  val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
  val monthTitle = monthFormat.format(currentMonthCal.time)

  val todaySdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  val todayStr = todaySdf.format(Date())

  // Tasks for selected date
  val tasksForDate = tasks.filter { it.dueDate == selectedDate }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))

      // Top Header: Centered title and Today shortcut
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
      ) {
        Box(modifier = Modifier.align(Alignment.Center)) {
          Text(
            text = "Task Calendar",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            ),
            color = PrimaryBlue
          )
        }

        TextButton(
          onClick = onTodayClick,
          modifier = Modifier
            .align(Alignment.CenterEnd)
            .testTag("calendar_today_button")
        ) {
          Text(
            text = "Today",
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Month Navigation Header: < October 2026 >
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
            shape = RoundedCornerShape(14.dp)
          )
          .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onPrevMonth, modifier = Modifier.testTag("prev_month_button")) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Previous Month",
            tint = if (isDark) TextDarkPrimary else DarkNavy
          )
        }

        Text(
          text = monthTitle,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = if (isDark) TextDarkPrimary else DarkNavy
        )

        IconButton(onClick = onNextMonth, modifier = Modifier.testTag("next_month_button")) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Next Month",
            tint = if (isDark) TextDarkPrimary else DarkNavy
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Weekday Header Row (Sun, Mon, Tue, Wed, Thu, Fri, Sat)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
          Text(
            text = day,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SlateGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(36.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Calendar Days Grid
      CalendarMonthGrid(
        monthCal = currentMonthCal,
        selectedDate = selectedDate,
        todayStr = todayStr,
        tasks = tasks,
        onDateSelected = onDateSelected
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Selected Date Tasks Section Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = formatDateDisplay(selectedDate),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            ),
            color = if (isDark) TextDarkPrimary else DarkNavy
          )
          Text(
            text = "${tasksForDate.size} ${if (tasksForDate.size == 1) "task" else "tasks"} scheduled",
            fontSize = 12.sp,
            color = SlateGray
          )
        }

        Button(
          onClick = { onAddTaskForDate(selectedDate) },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier.testTag("add_task_for_date_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Task", fontSize = 13.sp)
        }
      }
    }

    if (tasksForDate.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.CalendarToday,
              contentDescription = null,
              tint = SlateGray.copy(alpha = 0.5f),
              modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No tasks scheduled for this day",
              fontWeight = FontWeight.Medium,
              color = if (isDark) TextDarkPrimary else DarkNavy
            )
            Text(
              text = "Tap 'Add Task' to plan a task on this date.",
              fontSize = 12.sp,
              color = SlateGray
            )
          }
        }
      }
    } else {
      items(tasksForDate, key = { it.id }) { task ->
        CalendarTaskRow(
          task = task,
          onTaskClick = { onTaskClick(task) },
          onMenuClick = { onTaskMenuClick(task) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
private fun CalendarMonthGrid(
  monthCal: Calendar,
  selectedDate: String,
  todayStr: String,
  tasks: List<TaskItem>,
  onDateSelected: (String) -> Unit
) {
  val isDark = LocalIsDarkTheme.current

  val cal = monthCal.clone() as Calendar
  cal.set(Calendar.DAY_OF_MONTH, 1)
  val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0-based for Sunday
  val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

  val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  val currentYear = cal.get(Calendar.YEAR)
  val currentMonth = cal.get(Calendar.MONTH)

  val totalCells = (firstDayOfWeek + maxDaysInMonth + 6) / 7 * 7

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    for (row in 0 until totalCells / 7) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        for (col in 0 until 7) {
          val cellIndex = row * 7 + col
          val dayOfMonth = cellIndex - firstDayOfWeek + 1

          if (dayOfMonth in 1..maxDaysInMonth) {
            val cellCal = Calendar.getInstance().apply {
              set(currentYear, currentMonth, dayOfMonth)
            }
            val dateStr = sdf.format(cellCal.time)
            val isSelected = dateStr == selectedDate
            val isToday = dateStr == todayStr

            // Check what tasks exist on this date
            val dayTasks = tasks.filter { it.dueDate == dateStr }
            val hasTodo = dayTasks.any { it.status == TaskStatus.TODO }
            val hasInProgress = dayTasks.any { it.status == TaskStatus.IN_PROGRESS }
            val hasCompleted = dayTasks.any { it.status == TaskStatus.COMPLETED }

            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                  when {
                    isSelected -> PrimaryBlue
                    isToday -> if (isDark) Color(0xFF2A364F) else Color(0xFFEDF5FF)
                    else -> Color.Transparent
                  }
                )
                .then(
                  if (isToday && !isSelected) {
                    Modifier.border(1.dp, PrimaryBlue, CircleShape)
                  } else Modifier
                )
                .clickable { onDateSelected(dateStr) }
                .padding(2.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                Text(
                  text = "$dayOfMonth",
                  fontSize = 13.sp,
                  fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                  color = when {
                    isSelected -> Color.White
                    isToday -> PrimaryBlue
                    isDark -> TextDarkPrimary
                    else -> DarkNavy
                  }
                )

                // Colored dots row
                if (hasTodo || hasInProgress || hasCompleted) {
                  Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 1.dp)
                  ) {
                    if (hasTodo) {
                      Box(
                        modifier = Modifier
                          .size(4.dp)
                          .background(if (isSelected) Color.White else TodoBlue, CircleShape)
                      )
                    }
                    if (hasInProgress) {
                      Box(
                        modifier = Modifier
                          .size(4.dp)
                          .background(if (isSelected) Color(0xFFFFD180) else InProgressOrange, CircleShape)
                      )
                    }
                    if (hasCompleted) {
                      Box(
                        modifier = Modifier
                          .size(4.dp)
                          .background(if (isSelected) Color(0xFFA5D6A7) else CompletedGreen, CircleShape)
                      )
                    }
                  }
                } else {
                  Spacer(modifier = Modifier.height(4.dp))
                }
              }
            }
          } else {
            // Empty filler cell
            Spacer(modifier = Modifier.size(42.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun CalendarTaskRow(
  task: TaskItem,
  onTaskClick: () -> Unit,
  onMenuClick: () -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  val statusColor = when (task.status) {
    TaskStatus.TODO -> TodoBlue
    TaskStatus.IN_PROGRESS -> InProgressOrange
    TaskStatus.COMPLETED -> CompletedGreen
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable { onTaskClick() },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) SurfaceDark else SurfaceLight
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Status pill / indicator
      Box(
        modifier = Modifier
          .width(4.dp)
          .height(36.dp)
          .background(statusColor, RoundedCornerShape(2.dp))
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = task.title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = if (isDark) TextDarkPrimary else DarkNavy,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
          )

          PriorityBadge(priority = task.priority)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = task.status.displayName,
            fontSize = 12.sp,
            color = statusColor,
            fontWeight = FontWeight.SemiBold
          )

          Text(
            text = "•",
            fontSize = 12.sp,
            color = SlateGray
          )

          Text(
            text = task.category,
            fontSize = 12.sp,
            color = SlateGray
          )
        }
      }

      IconButton(onClick = onMenuClick, modifier = Modifier.size(32.dp)) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "Options",
          tint = SlateGray,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
