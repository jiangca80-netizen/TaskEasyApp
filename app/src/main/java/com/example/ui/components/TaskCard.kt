package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
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
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CompletedCardBgDark
import com.example.ui.theme.CompletedCardBgLight
import com.example.ui.theme.CompletedCardBorderDark
import com.example.ui.theme.CompletedCardBorderLight
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.CompletedGreenBorder
import com.example.ui.theme.DarkNavyText
import com.example.ui.theme.InProgressCardBgDark
import com.example.ui.theme.InProgressCardBgLight
import com.example.ui.theme.InProgressCardBorderDark
import com.example.ui.theme.InProgressCardBorderLight
import com.example.ui.theme.InProgressOrange
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueBorder
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TodoCardBgDark
import com.example.ui.theme.TodoCardBgLight
import com.example.ui.theme.TodoCardBorderDark
import com.example.ui.theme.TodoCardBorderLight
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
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

  val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
  val isDueToday = task.dueDate == todayStr
  val isOverdue = task.dueDate != null && task.dueDate < todayStr && task.status != TaskStatus.COMPLETED

  Box(
    modifier = modifier.fillMaxWidth(),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.86f)
        .widthIn(max = 460.dp)
        .padding(vertical = 5.dp)
        .clip(RoundedCornerShape(16.dp))
        .clickable { onCardClick() }
        .testTag("task_card_${task.id}"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = cardBg),
      border = BorderStroke(1.2.dp, borderColor),
      elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 13.dp, vertical = 10.dp)
      ) {
        if (task.status == TaskStatus.COMPLETED) {
          // --- Completed Card Layout ---
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
          ) {
            Box(
              modifier = Modifier
                .padding(top = 2.dp)
                .size(22.dp)
                .background(CompletedGreen, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
              ) {
                Text(
                  text = task.title,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                  ),
                  color = if (isDark) TextDarkPrimary else DarkNavyText,
                  textAlign = TextAlign.Justify,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                )

                IconButton(
                  onClick = onMenuClick,
                  modifier = Modifier
                    .size(24.dp)
                    .testTag("task_menu_${task.id}")
                ) {
                  Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = if (isDark) Color(0xFFA7F3D0) else SlateGray,
                    modifier = Modifier.size(17.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(3.dp))

              val compLabel = if (task.completedAt != null) {
                val compSdf = SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault())
                "Completed ${compSdf.format(Date(task.completedAt))}"
              } else {
                "Completed"
              }

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.CalendarToday,
                  contentDescription = null,
                  tint = if (isDark) Color(0xFF94A3B8) else SlateGray,
                  modifier = Modifier.size(12.dp)
                )
                Text(
                  text = compLabel,
                  fontSize = 11.5.sp,
                  color = if (isDark) Color(0xFFCBD5E1) else SlateGray
                )
              }

              if (task.elapsedSeconds > 0) {
                Spacer(modifier = Modifier.height(5.dp))
                CompletedTimeSpentBadge(seconds = task.elapsedSeconds)
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                CategoryChip(category = task.category)

                OutlinedButton(
                  onClick = onReopenClick,
                  modifier = Modifier
                    .height(32.dp)
                    .testTag("reopen_task_button"),
                  shape = RoundedCornerShape(16.dp),
                  border = BorderStroke(1.2.dp, CompletedGreenBorder),
                  colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isDark) Color(0xFF143D2B) else Color.White.copy(alpha = 0.85f),
                    contentColor = if (isDark) Color(0xFF6EE7B7) else Color(0xFF0F8A56)
                  ),
                  contentPadding = PaddingValues(horizontal = 11.dp, vertical = 0.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Reopen",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    softWrap = false
                  )
                }
              }
            }
          }
        } else {
          // --- To Do & In Progress Title Row (Justified, maximum width) ---
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Text(
              text = task.title,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.5.sp,
                lineHeight = 20.sp
              ),
              color = if (isDark) TextDarkPrimary else DarkNavyText,
              textAlign = TextAlign.Justify,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier
                .weight(1f)
                .padding(end = 6.dp)
            )

            IconButton(
              onClick = onMenuClick,
              modifier = Modifier
                .size(24.dp)
                .testTag("task_menu_${task.id}")
            ) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options",
                tint = if (isDark) Color(0xFFCBD5E1) else SlateGray,
                modifier = Modifier.size(17.dp)
              )
            }
          }

          if (task.status == TaskStatus.IN_PROGRESS) {
            Spacer(modifier = Modifier.height(5.dp))
            FlowRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              TaskStopwatchBadge(
                task = task,
                onToggleTimer = onToggleTimerClick
              )
              PriorityBadge(priority = task.priority)
              if (task.subtasks.isNotEmpty()) {
                SubtaskCountChip(task = task, isDark = isDark)
              }
            }
          }

          // Due Date & Category Row
          if (task.dueDate != null || task.category.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              if (task.dueDate != null) {
                val dateText = when {
                  isDueToday -> "Due today"
                  isOverdue -> "Overdue (${formatDateDisplay(task.dueDate)})"
                  else -> "Due ${formatDateDisplay(task.dueDate)}"
                }
                val dateColor = when {
                  isDueToday || isOverdue -> PriorityHigh
                  else -> if (isDark) Color(0xFFCBD5E1) else SlateGray
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = dateColor,
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = dateText,
                    fontSize = 11.5.sp,
                    color = dateColor,
                    fontWeight = if (isDueToday || isOverdue) FontWeight.SemiBold else FontWeight.Medium
                  )
                }
              }

              CategoryChip(category = task.category)
            }
          }

          // Description Preview
          if (task.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
              text = task.description,
              fontSize = 12.5.sp,
              lineHeight = 17.sp,
              color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }

          if (task.status == TaskStatus.TODO) {
            Spacer(modifier = Modifier.height(8.dp))
            // Bottom Row: Priority Badge + Subtask state on the left, Start Task Button on the right
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              FlowRow(
                modifier = Modifier
                  .weight(1f)
                  .padding(end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                PriorityBadge(priority = task.priority)
                if (task.subtasks.isNotEmpty()) {
                  SubtaskCountChip(task = task, isDark = isDark)
                }
              }

              Button(
                onClick = onStartClick,
                modifier = Modifier
                  .height(32.dp)
                  .testTag("start_task_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = PrimaryBlue,
                  contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp, pressedElevation = 0.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                  )
                  Text(
                    text = "Start Task",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    softWrap = false
                  )
                }
              }
            }
          } else if (task.status == TaskStatus.IN_PROGRESS) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = onBackToTodoClick,
                modifier = Modifier
                  .weight(1f)
                  .height(33.dp)
                  .testTag("back_to_todo_button"),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isDark) BorderDark else PrimaryBlueBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                  containerColor = if (isDark) Color(0xFF16233B) else Color.White.copy(alpha = 0.85f),
                  contentColor = PrimaryBlue
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
              ) {
                Text(
                  text = "← Back to To Do",
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 11.5.sp,
                  maxLines = 1,
                  softWrap = false
                )
              }

              Button(
                onClick = onCompleteClick,
                modifier = Modifier
                  .weight(1f)
                  .height(33.dp)
                  .testTag("complete_task_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = InProgressOrange,
                  contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp, pressedElevation = 0.dp)
              ) {
                Text(
                  text = "Complete ✓",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.5.sp,
                  maxLines = 1,
                  softWrap = false
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SubtaskCountChip(task: TaskItem, isDark: Boolean) {
  val subtasksDone = task.subtasks.count { it.isCompleted }
  val allDone = subtasksDone == task.subtasks.size
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(
        if (allDone) {
          if (isDark) Color(0xFF132A1C) else Color(0xFFDCFCE7)
        } else {
          if (isDark) Color(0xFF1E2D47) else Color.White.copy(alpha = 0.78f)
        }
      )
      .padding(horizontal = 7.dp, vertical = 2.5.dp)
  ) {
    Text(
      text = "✓ $subtasksDone/${task.subtasks.size}",
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      color = if (allDone) CompletedGreen else if (isDark) Color(0xFFCBD5E1) else SlateGray
    )
  }
}

fun formatDateDisplay(dateStr: String): String {
  return try {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
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
    if (isDark) Color(0xFF2C1E0A) else Color(0xFFFFF8EE)
  } else {
    if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
  }
  val badgeBorder = if (isRunning) InProgressOrange else SlateGray.copy(alpha = 0.4f)
  val contentColor = if (isRunning) InProgressOrange else SlateGray

  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .clickable(enabled = onToggleTimer != null) { onToggleTimer?.invoke() }
      .testTag("stopwatch_badge_${task.id}"),
    shape = RoundedCornerShape(10.dp),
    color = badgeBg,
    border = BorderStroke(1.1.dp, badgeBorder)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      if (isRunning) {
        Box(
          modifier = Modifier
            .size(6.dp)
            .background(InProgressOrange, CircleShape)
        )
      } else {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Resume timer",
          tint = contentColor,
          modifier = Modifier.size(10.dp)
        )
      }

      Icon(
        imageVector = Icons.Default.Schedule,
        contentDescription = "Stopwatch",
        tint = contentColor,
        modifier = Modifier.size(11.5.dp)
      )

      Text(
        text = formattedTime,
        fontWeight = FontWeight.Bold,
        fontSize = 11.5.sp,
        color = contentColor,
        letterSpacing = 0.3.sp
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
    modifier = modifier.clip(RoundedCornerShape(10.dp)),
    shape = RoundedCornerShape(10.dp),
    color = if (isDark) Color(0xFF12281D) else Color.White.copy(alpha = 0.78f),
    border = BorderStroke(1.dp, CompletedGreenBorder)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Schedule,
        contentDescription = "Time spent",
        tint = CompletedGreen,
        modifier = Modifier.size(11.5.dp)
      )
      Text(
        text = formatted,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
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
