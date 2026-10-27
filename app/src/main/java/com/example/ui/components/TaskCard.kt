package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.data.model.TaskStatus
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.CompletedGreenBorder
import com.example.ui.theme.CompletedGreenLightBg
import com.example.ui.theme.DarkNavyText
import com.example.ui.theme.InProgressOrange
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueBorder
import com.example.ui.theme.PrimaryBlueLightBg
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextDarkPrimary
import java.text.SimpleDateFormat
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
fun TaskCard(
  task: TaskItem,
  onCardClick: () -> Unit,
  onStartClick: () -> Unit,
  onBackToTodoClick: () -> Unit,
  onCompleteClick: () -> Unit,
  onReopenClick: () -> Unit,
  onMenuClick: () -> Unit,
  onToggleTimerClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val (cardBg, borderColor) = when (task.status) {
    TaskStatus.TODO -> Pair(
      if (isDark) TodoCardBgDark else TodoCardBgLight,
      if (isDark) TodoCardBorderDark else TodoCardBorderLight
    )
    TaskStatus.IN_PROGRESS -> Pair(
      if (isDark) InProgressCardBgDark else InProgressCardBgLight,
      if (isDark) InProgressCardBorderDark else InProgressCardBorderLight
    )
    TaskStatus.COMPLETED -> Pair(
      if (isDark) CompletedCardBgDark else CompletedCardBgLight,
      if (isDark) CompletedCardBorderDark else CompletedCardBorderLight
    )
  }

  val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
  val isDueToday = task.dueDate == todayStr
  val isOverdue = task.dueDate != null && task.dueDate < todayStr && task.status != TaskStatus.COMPLETED

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 7.dp)
      .clip(RoundedCornerShape(20.dp))
      .clickable { onCardClick() }
      .testTag("task_card_${task.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    border = BorderStroke(1.2.dp, borderColor),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      if (task.status == TaskStatus.COMPLETED) {
        // --- Completed Card Layout (Screen 5) ---
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Top
        ) {
          // Circular green checkmark
          Box(
            modifier = Modifier
              .size(26.dp)
              .background(CompletedGreen, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Completed",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                ),
                color = if (isDark) TextDarkPrimary else DarkNavyText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
              )

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                if (task.elapsedSeconds > 0) {
                  CompletedTimeSpentBadge(seconds = task.elapsedSeconds)
                }

                IconButton(
                  onClick = onMenuClick,
                  modifier = Modifier
                    .size(24.dp)
                    .testTag("task_menu_${task.id}")
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

            Spacer(modifier = Modifier.height(4.dp))

            // Completed timestamp
            val compDateStr = if (task.completedAt != null) {
              val compSdf = SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault())
              compSdf.format(Date(task.completedAt))
            } else "14 Oct 2026, 10:30 AM"

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = SlateGray,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Completed $compDateStr",
                fontSize = 12.sp,
                color = SlateGray
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              CategoryChip(category = task.category)

              // Reopen Button
              OutlinedButton(
                onClick = onReopenClick,
                modifier = Modifier
                  .height(34.dp)
                  .testTag("reopen_task_button"),
                shape = RoundedCornerShape(17.dp),
                border = BorderStroke(1.dp, CompletedGreenBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                  containerColor = if (isDark) Color(0xFF142920) else CompletedGreenLightBg,
                  contentColor = CompletedGreen
                )
              ) {
                Text(
                  text = "Reopen ⟲",
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      } else {
        // --- To Do (Screen 3) & In Progress (Screen 4) Layout ---
        // Top Title & Priority Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = task.title,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.5.sp
              ),
              color = if (isDark) TextDarkPrimary else DarkNavyText,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f, fill = false)
            )

            Spacer(modifier = Modifier.width(8.dp))
            PriorityBadge(priority = task.priority)
          }

          // Top Right Corner: Stopwatch Timer Badge & Options Menu
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            if (task.status == TaskStatus.IN_PROGRESS || task.isTimerRunning || task.elapsedSeconds > 0) {
              TaskStopwatchBadge(
                task = task,
                onToggleTimer = onToggleTimerClick
              )
            }

            IconButton(
              onClick = onMenuClick,
              modifier = Modifier
                .size(24.dp)
                .testTag("task_menu_${task.id}")
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

        Spacer(modifier = Modifier.height(8.dp))

        // Due Date & Category Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          if (task.dueDate != null) {
            val dateText = when {
              isDueToday -> "Due today"
              isOverdue -> "Due today"
              else -> "Due ${formatDateDisplay(task.dueDate)}"
            }
            val dateColor = when {
              isDueToday || isOverdue -> PriorityHigh
              else -> SlateGray
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = dateColor,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = dateText,
                fontSize = 12.sp,
                color = dateColor,
                fontWeight = if (isDueToday || isOverdue) FontWeight.SemiBold else FontWeight.Normal
              )
            }
          }

          CategoryChip(category = task.category)
        }

        // Description Preview
        if (task.description.isNotBlank()) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = task.description,
            fontSize = 13.sp,
            color = if (isDark) Color(0xFF94A3B8) else SlateGray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Buttons
        if (task.status == TaskStatus.TODO) {
          // Full-width royal blue button: Start Task →
          Button(
            onClick = onStartClick,
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("start_task_button"),
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = PrimaryBlue,
              contentColor = Color.White
            )
          ) {
            Text(
              text = "Start Task →",
              fontWeight = FontWeight.Bold,
              fontSize = 14.5.sp
            )
          }
        } else if (task.status == TaskStatus.IN_PROGRESS) {
          // Dual Buttons: ← Back to To Do & Complete ✓
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onBackToTodoClick,
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("back_to_todo_button"),
              shape = RoundedCornerShape(22.dp),
              border = BorderStroke(1.dp, if (isDark) BorderDark else PrimaryBlueBorder),
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isDark) Color(0xFF16233B) else PrimaryBlueLightBg,
                contentColor = PrimaryBlue
              )
            ) {
              Text(
                text = "← Back to To Do",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }

            Button(
              onClick = onCompleteClick,
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("complete_task_button"),
              shape = RoundedCornerShape(22.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = InProgressOrange,
                contentColor = Color.White
              )
            ) {
              Text(
                text = "Complete ✓",
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
              )
            }
          }
        }
      }
    }
  }
}

fun formatDateDisplay(dateStr: String): String {
  return try {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    val date = parser.parse(dateStr)
    if (date != null) formatter.format(date) else dateStr
  } catch (e: Exception) {
    dateStr
  }
}

@Composable
fun TaskStopwatchBadge(
  task: TaskItem,
  onToggleTimer: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  var currentTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }

  LaunchedEffect(task.isTimerRunning) {
    if (task.isTimerRunning) {
      while (true) {
        delay(1000L)
        currentTimestamp = System.currentTimeMillis()
      }
    }
  }

  val totalSeconds = task.currentElapsedSeconds(currentTimestamp)
  val formattedTime = formatStopwatch(totalSeconds)
  val isRunning = task.isTimerRunning

  val badgeBg = if (isRunning) {
    if (isDark) Color(0xFF2C1E0A) else Color(0xFFFFF4E6)
  } else {
    if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
  }
  val badgeBorder = if (isRunning) InProgressOrange else SlateGray.copy(alpha = 0.4f)
  val contentColor = if (isRunning) InProgressOrange else SlateGray

  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(enabled = onToggleTimer != null) { onToggleTimer?.invoke() }
      .testTag("stopwatch_badge_${task.id}"),
    shape = RoundedCornerShape(12.dp),
    color = badgeBg,
    border = BorderStroke(1.2.dp, badgeBorder)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.5.dp)
    ) {
      if (isRunning) {
        Box(
          modifier = Modifier
            .size(6.5.dp)
            .background(InProgressOrange, CircleShape)
        )
      } else {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Resume timer",
          tint = contentColor,
          modifier = Modifier.size(11.dp)
        )
      }

      Icon(
        imageVector = Icons.Default.Schedule,
        contentDescription = "Stopwatch",
        tint = contentColor,
        modifier = Modifier.size(12.5.dp)
      )

      Text(
        text = formattedTime,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = contentColor,
        letterSpacing = 0.4.sp
      )
    }
  }
}

@Composable
fun CompletedTimeSpentBadge(
  seconds: Long,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val formatted = formatElapsedSpent(seconds)

  Surface(
    modifier = modifier.clip(RoundedCornerShape(12.dp)),
    shape = RoundedCornerShape(12.dp),
    color = if (isDark) Color(0xFF12281D) else CompletedGreenLightBg,
    border = BorderStroke(1.dp, CompletedGreenBorder)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Schedule,
        contentDescription = "Time spent",
        tint = CompletedGreen,
        modifier = Modifier.size(12.dp)
      )
      Text(
        text = formatted,
        fontWeight = FontWeight.Bold,
        fontSize = 11.5.sp,
        color = CompletedGreen
      )
    }
  }
}

fun formatElapsedSpent(seconds: Long): String {
  val h = seconds / 3600
  val m = (seconds % 3600) / 60
  val s = seconds % 60
  return when {
    h > 0 -> "${h}h ${m}m"
    m > 0 -> "${m}m ${s}s"
    else -> "${s}s"
  }
}

fun formatStopwatch(seconds: Long): String {
  val h = seconds / 3600
  val m = (seconds % 3600) / 60
  val s = seconds % 60
  return if (h > 0) {
    String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
  } else {
    String.format(Locale.getDefault(), "%02d:%02d", m, s)
  }
}
