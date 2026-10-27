package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.data.model.TaskStatus
import com.example.ui.components.CategoryChip
import com.example.ui.components.PriorityBadge
import com.example.ui.components.TaskStopwatchBadge
import com.example.ui.components.formatDateDisplay
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
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

@Composable
fun TaskDetailsScreen(
  task: TaskItem?,
  onBack: () -> Unit,
  onEdit: (TaskItem) -> Unit,
  onDelete: (TaskItem) -> Unit,
  onMenuClick: (TaskItem) -> Unit,
  onStartTask: (TaskItem) -> Unit,
  onCompleteTask: (TaskItem) -> Unit,
  onReopenTask: (TaskItem) -> Unit,
  onToggleSubtask: (TaskItem, String) -> Unit,
  onAddSubtask: (TaskItem, String) -> Unit,
  onToggleTimer: ((TaskItem) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val isDark = isSystemInDarkTheme()
  var newSubtaskText by remember { mutableStateOf("") }
  var showAddSubtaskField by remember { mutableStateOf(false) }

  if (task == null) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(if (isDark) SurfaceDark else SurfaceLight),
      contentAlignment = Alignment.Center
    ) {
      Text("Task not found", color = SlateGray)
    }
    return
  }

  val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
  val isDueToday = task.dueDate == todayStr
  val isOverdue = task.dueDate != null && task.dueDate < todayStr && task.status != TaskStatus.COMPLETED

  val createdSdf = SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault())
  val createdStr = createdSdf.format(Date(task.createdAt))

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(if (isDark) SurfaceDark else SurfaceLight)
  ) {
    // Top Bar (Screen 7)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = if (isDark) TextDarkPrimary else DarkNavyText
        )
      }

      Text(
        text = "Task Details",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 19.sp
        ),
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )

      IconButton(onClick = { onMenuClick(task) }, modifier = Modifier.testTag("detail_overflow_button")) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "Options",
          tint = if (isDark) TextDarkPrimary else DarkNavyText
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      // Header item matching Screen 7:
      // Big blue calendar icon on left, Title, Priority and Category
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .background(if (isDark) Color(0xFF1E2D47) else PrimaryBlueLightBg, RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = task.title,
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            ),
            color = if (isDark) TextDarkPrimary else DarkNavyText
          )

          Spacer(modifier = Modifier.height(4.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            PriorityBadge(priority = task.priority)
            CategoryChip(category = task.category)
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Metadata card: Due Date & Created (Screen 7)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isDark) Color(0xFF1E2D47) else Color(0xFFF8FAFC)
        ),
        border = BorderStroke(1.dp, if (isDark) BorderDark else BorderLight)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Due Date Row
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = "Due Date",
                fontSize = 11.5.sp,
                color = SlateGray
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = if (task.dueDate != null) formatDateDisplay(task.dueDate) else "Not set",
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isDark) TextDarkPrimary else DarkNavyText
                )
                if (task.dueDate != null) {
                  val diffLabel = when {
                    task.dueDate == todayStr -> "(Due today)"
                    task.dueDate < todayStr && task.status != TaskStatus.COMPLETED -> "(Overdue)"
                    else -> {
                      try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val d = sdf.parse(task.dueDate)
                        val t = sdf.parse(todayStr)
                        if (d != null && t != null) {
                          val days = ((d.time - t.time) / (1000 * 60 * 60 * 24)).toInt()
                          if (days == 1) "(Due tomorrow)" else if (days > 1) "($days days left)" else ""
                        } else ""
                      } catch (e: Exception) {
                        ""
                      }
                    }
                  }
                  if (diffLabel.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = diffLabel,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isOverdue) PriorityHigh else PrimaryBlue
                    )
                  }
                }
              }
            }
          }

          // Created Row
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = "Created",
                fontSize = 11.5.sp,
                color = SlateGray
              )
              Text(
                text = createdStr,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) TextDarkPrimary else DarkNavyText
              )
            }
          }

          // Time Spent (Stopwatch) Row
          if (task.status == TaskStatus.IN_PROGRESS || task.isTimerRunning || task.elapsedSeconds > 0) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .background(InProgressOrange.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Schedule,
                  contentDescription = null,
                  tint = InProgressOrange,
                  modifier = Modifier.size(18.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Text(
                  text = if (task.status == TaskStatus.COMPLETED) "Time Spent" else "Time Spent (Stopwatch)",
                  fontSize = 11.5.sp,
                  color = SlateGray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  TaskStopwatchBadge(
                    task = task,
                    onToggleTimer = { onToggleTimer?.invoke(task) }
                  )
                  if (task.status == TaskStatus.IN_PROGRESS) {
                    Text(
                      text = if (task.isTimerRunning) "Ticking live..." else "Paused",
                      fontSize = 12.sp,
                      color = if (task.isTimerRunning) InProgressOrange else SlateGray,
                      fontWeight = FontWeight.Medium
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Description Section (Screen 7)
      Text(
        text = "Description",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = task.description.ifBlank { "No description provided." },
        fontSize = 14.sp,
        lineHeight = 22.sp,
        color = if (task.description.isBlank()) SlateGray else if (isDark) Color(0xFF94A3B8) else SlateGray
      )

      Spacer(modifier = Modifier.height(22.dp))

      // Subtasks Section (Screen 7)
      val completedCount = task.subtasks.count { it.isCompleted }
      val totalCount = task.subtasks.size

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (totalCount > 0) "Subtasks ($completedCount/$totalCount)" else "Subtasks",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = if (isDark) TextDarkPrimary else DarkNavyText
        )

        // Blue '+' button in outlined box
        Box(
          modifier = Modifier
            .size(28.dp)
            .border(1.dp, PrimaryBlue, RoundedCornerShape(6.dp))
            .clickable { showAddSubtaskField = !showAddSubtaskField },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add subtask",
            tint = PrimaryBlue,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (showAddSubtaskField) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = newSubtaskText,
            onValueChange = { newSubtaskText = it },
            placeholder = { Text("Enter subtask...", fontSize = 13.sp) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
          Button(
            onClick = {
              if (newSubtaskText.isNotBlank()) {
                onAddSubtask(task, newSubtaskText)
                newSubtaskText = ""
                showAddSubtaskField = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Add")
          }
        }
      }

      // Checklist items
      if (task.subtasks.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          Text(
            text = "No subtasks yet. Tap '+' above to add one.",
            fontSize = 13.5.sp,
            color = SlateGray
          )
        }
      } else {
        task.subtasks.forEach { subtask ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .clickable { onToggleSubtask(task, subtask.id) }
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Checkbox(
              checked = subtask.isCompleted,
              onCheckedChange = { onToggleSubtask(task, subtask.id) },
              colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = subtask.text,
              fontSize = 14.sp,
              textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
              color = if (subtask.isCompleted) SlateGray else if (isDark) TextDarkPrimary else DarkNavyText
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Bottom Dual Buttons: Edit (left) and Delete (right) matching Screen 7
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = { onEdit(task) },
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("detail_edit_button"),
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.dp, PrimaryBlue),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isDark) Color(0xFF1E2D47) else PrimaryBlueLightBg,
            contentColor = PrimaryBlue
          )
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Edit", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Button(
          onClick = { onDelete(task) },
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("detail_delete_button"),
          colors = ButtonDefaults.buttonColors(containerColor = PriorityHigh),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Delete", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
