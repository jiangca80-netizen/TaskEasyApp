package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.data.model.TaskStatus
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.DarkNavyText
import com.example.ui.theme.InProgressOrange
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextDarkPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskActionBottomSheet(
  task: TaskItem,
  onDismiss: () -> Unit,
  onStartTask: () -> Unit,
  onMoveToTodo: () -> Unit,
  onMarkCompleted: () -> Unit,
  onReopenTask: () -> Unit,
  onEditTask: () -> Unit,
  onDuplicateTask: () -> Unit,
  onDeleteTask: () -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = if (isDark) SurfaceDark else SurfaceLight,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
    ) {
      // Header: Move Task and Close X (Screen 8)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Move Task",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
          ),
          color = if (isDark) TextDarkPrimary else DarkNavyText
        )
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_actions_sheet")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = SlateGray
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Mini Preview Card (Screen 8)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            color = if (isDark) Color(0xFF1E2D47) else Color(0xFFF1F5F9),
            shape = RoundedCornerShape(14.dp)
          )
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Blue task icon container
          Box(
            modifier = Modifier
              .size(40.dp)
              .background(PrimaryBlue.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CalendarToday,
              contentDescription = null,
              tint = PrimaryBlue,
              modifier = Modifier.size(20.dp)
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
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isDark) TextDarkPrimary else DarkNavyText
              )
              PriorityBadge(priority = task.priority)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              if (task.dueDate != null) {
                Text(
                  text = "Due ${formatDateDisplay(task.dueDate)}",
                  fontSize = 12.sp,
                  color = SlateGray
                )
              }
              CategoryChip(category = task.category)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Actions list matching Screen 8:
      // 1. Move to To Do / Start Task
      if (task.status == TaskStatus.IN_PROGRESS) {
        ActionSquareRowItem(
          icon = Icons.Default.ArrowBack,
          iconBg = PrimaryBlue,
          title = "Move to To Do",
          subtitle = "Send this task back to To Do list",
          onClick = {
            onDismiss()
            onMoveToTodo()
          },
          testTag = "sheet_action_move_todo"
        )
      } else if (task.status == TaskStatus.TODO) {
        ActionSquareRowItem(
          icon = Icons.Default.ArrowForward,
          iconBg = PrimaryBlue,
          title = "Start Task",
          subtitle = "Move to In Progress list and start work",
          onClick = {
            onDismiss()
            onStartTask()
          },
          testTag = "sheet_action_start"
        )
      }

      // 2. Mark as Completed / Reopen
      if (task.status == TaskStatus.IN_PROGRESS) {
        ActionSquareRowItem(
          icon = Icons.Default.Check,
          iconBg = CompletedGreen,
          title = "Mark as Completed",
          subtitle = "Move to Completed and record time",
          onClick = {
            onDismiss()
            onMarkCompleted()
          },
          testTag = "sheet_action_complete"
        )
      } else if (task.status == TaskStatus.COMPLETED) {
        ActionSquareRowItem(
          icon = Icons.Default.Refresh,
          iconBg = CompletedGreen,
          title = "Reopen Task",
          subtitle = "Move task back to In Progress",
          onClick = {
            onDismiss()
            onReopenTask()
          },
          testTag = "sheet_action_reopen"
        )
      }

      // 3. Edit Task
      ActionSquareRowItem(
        icon = Icons.Default.Edit,
        iconBg = InProgressOrange,
        title = "Edit Task",
        subtitle = "Change title, description, etc.",
        onClick = {
          onDismiss()
          onEditTask()
        },
        testTag = "sheet_action_edit"
      )

      // 4. Duplicate Task
      ActionSquareRowItem(
        icon = Icons.Default.ContentCopy,
        iconBg = Color(0xFF6366F1),
        title = "Duplicate Task",
        subtitle = "Create a copy of this task",
        onClick = {
          onDismiss()
          onDuplicateTask()
        },
        testTag = "sheet_action_duplicate"
      )

      // 5. Delete Task
      ActionSquareRowItem(
        icon = Icons.Default.Delete,
        iconBg = PriorityHigh,
        title = "Delete Task",
        subtitle = "Remove this task permanently",
        onClick = {
          onDismiss()
          onDeleteTask()
        },
        testTag = "sheet_action_delete"
      )
    }
  }
}

@Composable
private fun ActionSquareRowItem(
  icon: ImageVector,
  iconBg: Color,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  testTag: String
) {
  val isDark = LocalIsDarkTheme.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(vertical = 10.dp, horizontal = 4.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Solid rounded square with white icon (Screen 8)
    Box(
      modifier = Modifier
        .size(40.dp)
        .background(iconBg, RoundedCornerShape(10.dp)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )
      Text(
        text = subtitle,
        fontSize = 12.sp,
        color = SlateGray
      )
    }
  }
}
