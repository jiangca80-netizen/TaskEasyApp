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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.data.model.TaskItem
import com.example.data.model.TaskStatus
import com.example.ui.components.PriorityBadge
import com.example.ui.components.TaskEasyLogo
import com.example.ui.components.TaskStopwatchBadge
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.DarkNavyText
import com.example.ui.theme.DashBlueBg
import com.example.ui.theme.DashLavenderBg
import com.example.ui.theme.DashLavenderPurple
import com.example.ui.theme.DashMintBg
import com.example.ui.theme.DashOrangeBg
import com.example.ui.theme.InProgressOrange
import com.example.ui.theme.LocalIsDarkTheme
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

import com.example.ui.theme.CompletedCardBgDark
import com.example.ui.theme.CompletedCardBgLight
import com.example.ui.theme.CompletedCardBorderDark
import com.example.ui.theme.CompletedCardBorderLight
import com.example.ui.theme.InProgressCardBgDark
import com.example.ui.theme.InProgressCardBgLight
import com.example.ui.theme.InProgressCardBorderDark
import com.example.ui.theme.InProgressCardBorderLight
import com.example.ui.theme.TodoCardBgDark
import com.example.ui.theme.TodoCardBgLight
import com.example.ui.theme.TodoCardBorderDark
import com.example.ui.theme.TodoCardBorderLight

@Composable
fun DashboardScreen(
  tasks: List<TaskItem>,
  onSeeAllClick: () -> Unit,
  onTaskClick: (TaskItem) -> Unit,
  onTaskMenuClick: (TaskItem) -> Unit,
  onToggleTheme: () -> Unit,
  onTabSelect: (TaskStatus) -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current

  val todoCount = tasks.count { it.status == TaskStatus.TODO }
  val inProgressCount = tasks.count { it.status == TaskStatus.IN_PROGRESS }
  val completedCount = tasks.count { it.status == TaskStatus.COMPLETED }
  val totalTasks = tasks.size
  val completionPercentage = if (totalTasks > 0) ((completedCount.toFloat() / totalTasks.toFloat()) * 100).toInt() else 0

  val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
  val todayTasks = tasks.filter { it.dueDate == todayStr || (it.dueDate != null && it.dueDate < todayStr && it.status != TaskStatus.COMPLETED) }

  val greeting = getGreetingMessage()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))

      // Top Bar: TaskEasy centered, theme toggle at right
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
      ) {
        // Centered TaskEasy Wordmark
        Box(
          modifier = Modifier.align(Alignment.Center)
        ) {
          TaskEasyLogo(fontSize = 22.sp)
        }

        // Right Action: Sun / Theme icon
        IconButton(
          onClick = onToggleTheme,
          modifier = Modifier
            .align(Alignment.CenterEnd)
            .size(38.dp)
            .testTag("dashboard_theme_toggle")
        ) {
          Icon(
            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.WbSunny,
            contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
            tint = if (isDark) Color(0xFFFFD166) else InProgressOrange,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Greeting Header (Screen 2)
      Text(
        text = greeting.first,
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.ExtraBold,
          fontSize = 26.sp,
          letterSpacing = (-0.5).sp
        ),
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = greeting.second,
        fontSize = 14.sp,
        color = SlateGray
      )

      Spacer(modifier = Modifier.height(20.dp))

      // 2 x 2 Summary Cards Grid (with large numbers beside icons & appropriate shades)
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Card 1: To Do
          SummaryCard(
            countText = "$todoCount",
            labelText = "To Do",
            icon = Icons.Default.FormatListBulleted,
            iconBg = Color(0xFF0072F5),
            bgColor = if (isDark) TodoCardBgDark else TodoCardBgLight,
            borderColor = if (isDark) TodoCardBorderDark else TodoCardBorderLight,
            onClick = { onTabSelect(TaskStatus.TODO) },
            modifier = Modifier.weight(1f).testTag("summary_card_todo")
          )

          // Card 2: In Progress
          SummaryCard(
            countText = "$inProgressCount",
            labelText = "In Progress",
            icon = Icons.Default.Schedule,
            iconBg = Color(0xFFFF9500),
            bgColor = if (isDark) InProgressCardBgDark else InProgressCardBgLight,
            borderColor = if (isDark) InProgressCardBorderDark else InProgressCardBorderLight,
            onClick = { onTabSelect(TaskStatus.IN_PROGRESS) },
            modifier = Modifier.weight(1f).testTag("summary_card_in_progress")
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Card 3: Completed
          SummaryCard(
            countText = "$completedCount",
            labelText = "Completed",
            icon = Icons.Default.Check,
            iconBg = Color(0xFF10B981),
            bgColor = if (isDark) CompletedCardBgDark else CompletedCardBgLight,
            borderColor = if (isDark) CompletedCardBorderDark else CompletedCardBorderLight,
            onClick = { onTabSelect(TaskStatus.COMPLETED) },
            modifier = Modifier.weight(1f).testTag("summary_card_completed")
          )

          // Card 4: Completion %
          SummaryCard(
            countText = "$completionPercentage%",
            labelText = "Completion",
            icon = Icons.Default.BarChart,
            iconBg = Color(0xFF8B5CF6),
            bgColor = if (isDark) Color(0xFF221634) else DashLavenderBg,
            borderColor = if (isDark) Color(0xFF452B6A) else Color(0xFFE4D2FD),
            onClick = onSeeAllClick,
            modifier = Modifier.weight(1f).testTag("summary_card_completion")
          )
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // Today's Tasks Section Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Today's Tasks",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          ),
          color = if (isDark) TextDarkPrimary else DarkNavyText
        )

        TextButton(
          onClick = onSeeAllClick,
          modifier = Modifier.testTag("see_all_tasks_button")
        ) {
          Text(
            text = "See all →",
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }

    // List of today's tasks
    if (todayTasks.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "No tasks due today!",
              fontWeight = FontWeight.SemiBold,
              color = if (isDark) TextDarkPrimary else DarkNavyText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Tap + below to create a new task.",
              fontSize = 13.sp,
              color = SlateGray
            )
          }
        }
      }
    } else {
      items(todayTasks, key = { it.id }) { task ->
        TodayTaskRow(
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
private fun SummaryCard(
  countText: String,
  labelText: String,
  icon: ImageVector,
  iconBg: Color,
  bgColor: Color,
  borderColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current

  Card(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .clickable { onClick() },
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = bgColor),
    border = BorderStroke(1.2.dp, borderColor),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.5.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
      // Large number placed right beside the icon
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .background(iconBg, RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }

        Text(
          text = countText,
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Black,
            fontSize = 32.sp,
            letterSpacing = (-1).sp
          ),
          color = if (isDark) Color.White else iconBg
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = labelText,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Bold,
        color = if (isDark) Color(0xFFCBD5E1) else DarkNavyText
      )
    }
  }
}

@Composable
private fun TodayTaskRow(
  task: TaskItem,
  onTaskClick: () -> Unit,
  onMenuClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
  val isDueToday = task.dueDate == todayStr
  val isDueTomorrow = task.dueDate != null && task.dueDate > todayStr

  val (bgColor, borderColor, accentColor) = when (task.status) {
    TaskStatus.TODO -> Triple(
      if (isDark) TodoCardBgDark else TodoCardBgLight,
      if (isDark) TodoCardBorderDark else TodoCardBorderLight,
      Color(0xFF0072F5)
    )
    TaskStatus.IN_PROGRESS -> Triple(
      if (isDark) InProgressCardBgDark else InProgressCardBgLight,
      if (isDark) InProgressCardBorderDark else InProgressCardBorderLight,
      Color(0xFFFF9500)
    )
    TaskStatus.COMPLETED -> Triple(
      if (isDark) CompletedCardBgDark else CompletedCardBgLight,
      if (isDark) CompletedCardBorderDark else CompletedCardBorderLight,
      Color(0xFF10B981)
    )
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 2.dp)
      .clip(RoundedCornerShape(18.dp))
      .clickable { onTaskClick() },
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = bgColor),
    border = BorderStroke(1.2.dp, borderColor),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 13.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .background(accentColor.copy(alpha = 0.15f), CircleShape)
          .border(2.dp, accentColor, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        if (task.status == TaskStatus.COMPLETED) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(14.dp)
          )
        }
      }

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
            color = if (isDark) TextDarkPrimary else DarkNavyText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
          )

          PriorityBadge(priority = task.priority)

          if (task.status == TaskStatus.IN_PROGRESS || task.isTimerRunning) {
            TaskStopwatchBadge(task = task)
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          val dateColor = if (isDueTomorrow) PrimaryBlue else PriorityHigh
          val dateLabel = if (isDueTomorrow) "Due tomorrow" else "Due today"

          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = dateColor,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = dateLabel,
            fontSize = 12.sp,
            color = dateColor,
            fontWeight = FontWeight.Medium
          )
        }
      }

      IconButton(
        onClick = onMenuClick,
        modifier = Modifier.size(30.dp)
      ) {
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

private fun getGreetingMessage(): Pair<String, String> {
  val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
  return when (hour) {
    in 5..11 -> Pair("Good Morning! 👋", "Let's make today productive.")
    in 12..16 -> Pair("Good Afternoon! ☀️", "Let's make today productive.")
    in 17..21 -> Pair("Good Evening! 🌙", "Let's make today productive.")
    else -> Pair("Good Night! ✨", "Let's make today productive.")
  }
}
