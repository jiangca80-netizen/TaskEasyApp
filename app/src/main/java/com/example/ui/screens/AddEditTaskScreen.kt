package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.data.model.Subtask
import com.example.data.model.TaskItem
import com.example.ui.components.formatDateDisplay
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DarkNavyText
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextDarkPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditTaskScreen(
  taskToEdit: TaskItem?,
  presetDueDate: String?,
  onSave: (title: String, desc: String, priority: Priority, category: String, dueDate: String?, subtasks: List<Subtask>) -> Unit,
  onCancel: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onCancel() }

  val context = LocalContext.current
  val isDark = LocalIsDarkTheme.current

  var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
  var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
  var priority by remember { mutableStateOf(taskToEdit?.priority ?: Priority.MEDIUM) }
  var category by remember { mutableStateOf(taskToEdit?.category ?: "Personal") }
  var dueDate by remember { mutableStateOf(taskToEdit?.dueDate ?: presetDueDate) }

  val subtasks = remember {
    mutableStateListOf<Subtask>().apply {
      if (taskToEdit != null) {
        addAll(taskToEdit.subtasks)
      }
    }
  }

  var newSubtaskText by remember { mutableStateOf("") }
  var titleError by remember { mutableStateOf(false) }

  val categories = listOf(
    Pair("Personal", Icons.Default.LocalOffer),
    Pair("Work", Icons.Default.Work),
    Pair("Family", Icons.Default.Home),
    Pair("Shopping", Icons.Default.ShoppingCart),
    Pair("Others", Icons.Default.GridView)
  )

  val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

  fun showDatePicker() {
    val cal = Calendar.getInstance()
    if (dueDate != null) {
      try {
        val parsed = sdf.parse(dueDate!!)
        if (parsed != null) cal.time = parsed
      } catch (_: Exception) {}
    }
    DatePickerDialog(
      context,
      { _, year, month, dayOfMonth ->
        val selectedCal = Calendar.getInstance()
        selectedCal.set(year, month, dayOfMonth)
        dueDate = sdf.format(selectedCal.time)
      },
      cal.get(Calendar.YEAR),
      cal.get(Calendar.MONTH),
      cal.get(Calendar.DAY_OF_MONTH)
    ).show()
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(if (isDark) SurfaceDark else SurfaceLight)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onCancel,
        modifier = Modifier.testTag("back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = if (isDark) TextDarkPrimary else DarkNavyText
        )
      }

      Text(
        text = if (taskToEdit != null) "Edit Task" else "Add New Task",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 19.sp
        ),
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      // Task Title *
      Text(
        text = "Task Title *",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = title,
        onValueChange = {
          title = it
          if (it.isNotBlank()) titleError = false
        },
        placeholder = { Text("e.g. Prepare monthly report", color = SlateGray, fontSize = 14.sp) },
        textStyle = TextStyle(
          color = if (isDark) Color.White else DarkNavyText,
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium
        ),
        isError = titleError,
        supportingText = if (titleError) {
          { Text("Title cannot be blank", color = PriorityHigh) }
        } else null,
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_title_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = if (isDark) Color.White else DarkNavyText,
          unfocusedTextColor = if (isDark) Color.White else DarkNavyText,
          focusedContainerColor = if (isDark) Color(0xFF1E2D47) else Color(0xFFFBFDFF),
          unfocusedContainerColor = if (isDark) Color(0xFF1E2D47) else Color(0xFFFBFDFF),
          cursorColor = PrimaryBlue,
          focusedBorderColor = PrimaryBlue,
          unfocusedBorderColor = if (isDark) BorderDark else BorderLight
        )
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Description (optional)
      Text(
        text = "Description (optional)",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        placeholder = { Text("Add more details...", color = SlateGray, fontSize = 14.sp) },
        textStyle = TextStyle(
          color = if (isDark) Color.White else DarkNavyText,
          fontSize = 14.sp
        ),
        minLines = 3,
        maxLines = 5,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_description_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = if (isDark) Color.White else DarkNavyText,
          unfocusedTextColor = if (isDark) Color.White else DarkNavyText,
          focusedContainerColor = if (isDark) Color(0xFF1E2D47) else Color(0xFFFBFDFF),
          unfocusedContainerColor = if (isDark) Color(0xFF1E2D47) else Color(0xFFFBFDFF),
          cursorColor = PrimaryBlue,
          focusedBorderColor = PrimaryBlue,
          unfocusedBorderColor = if (isDark) BorderDark else BorderLight
        )
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Priority
      Text(
        text = "Priority",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Priority.entries.forEach { p ->
          val selected = priority == p
          val (chipBg, dotColor, textColor) = when (p) {
            Priority.HIGH -> Triple(if (selected) Color(0xFFFFEBEB) else if (isDark) Color(0xFF261919) else Color(0xFFFAFAFA), PriorityHigh, PriorityHigh)
            Priority.MEDIUM -> Triple(if (selected) Color(0xFFFFF4E0) else if (isDark) Color(0xFF2E2214) else Color(0xFFFAFAFA), PriorityMedium, PriorityMedium)
            Priority.LOW -> Triple(if (selected) Color(0xFFEAFBF3) else if (isDark) Color(0xFF142920) else Color(0xFFFAFAFA), PriorityLow, PriorityLow)
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(chipBg)
              .then(
                if (selected) {
                  Modifier.border(1.5.dp, dotColor, RoundedCornerShape(12.dp))
                } else {
                  Modifier.border(1.dp, if (isDark) BorderDark else BorderLight, RoundedCornerShape(12.dp))
                }
              )
              .clickable { priority = p }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(dotColor, CircleShape)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = p.displayName,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.5.sp,
                color = if (selected) textColor else SlateGray
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Due Date (optional)
      Text(
        text = "Due Date (optional)",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(if (isDark) Color(0xFF1E2D47) else Color(0xFFFBFDFF))
          .border(1.dp, if (isDark) BorderDark else BorderLight, RoundedCornerShape(12.dp))
          .clickable { showDatePicker() }
          .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.CalendarToday,
          contentDescription = null,
          tint = SlateGray,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = if (dueDate != null) formatDateDisplay(dueDate!!) else "Select date",
          fontSize = 14.sp,
          color = if (dueDate != null) (if (isDark) TextDarkPrimary else DarkNavyText) else SlateGray,
          modifier = Modifier.weight(1f)
        )
        if (dueDate != null) {
          IconButton(
            onClick = { dueDate = null },
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Clear,
              contentDescription = "Clear date",
              tint = SlateGray,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Category
      Text(
        text = "Category",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        categories.forEach { (cat, icon) ->
          val selected = category.equals(cat, ignoreCase = true)
          val bg = if (selected) Color(0xFFEDF5FF) else if (isDark) Color(0xFF1E2D47) else Color(0xFFF8FAFC)
          val textColor = if (selected) PrimaryBlue else SlateGray
          val borderColor = if (selected) PrimaryBlue else if (isDark) BorderDark else BorderLight

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(bg)
              .border(1.dp, borderColor, RoundedCornerShape(12.dp))
              .clickable { category = cat }
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = cat,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Subtasks
      Text(
        text = "Subtasks (optional)",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )
      Spacer(modifier = Modifier.height(8.dp))

      subtasks.forEachIndexed { index, subtask ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "• ${subtask.text}",
            fontSize = 14.sp,
            color = if (isDark) TextDarkPrimary else DarkNavyText,
            modifier = Modifier.weight(1f)
          )
          IconButton(
            onClick = { subtasks.removeAt(index) },
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Remove subtask",
              tint = SlateGray,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = newSubtaskText,
          onValueChange = { newSubtaskText = it },
          placeholder = { Text("Add subtask item...", fontSize = 13.sp, color = SlateGray) },
          textStyle = TextStyle(
            color = if (isDark) Color.White else DarkNavyText,
            fontSize = 13.5.sp
          ),
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = if (isDark) Color.White else DarkNavyText,
            unfocusedTextColor = if (isDark) Color.White else DarkNavyText,
            focusedContainerColor = if (isDark) Color(0xFF1E2D47) else Color(0xFFFBFDFF),
            unfocusedContainerColor = if (isDark) Color(0xFF1E2D47) else Color(0xFFFBFDFF),
            cursorColor = PrimaryBlue,
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = if (isDark) BorderDark else BorderLight
          )
        )
        Button(
          onClick = {
            if (newSubtaskText.isNotBlank()) {
              subtasks.add(Subtask(UUID.randomUUID().toString(), newSubtaskText.trim(), false))
              newSubtaskText = ""
            }
          },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(18.dp))
        }
      }

      Spacer(modifier = Modifier.height(30.dp))

      // Save Task Button
      Button(
        onClick = {
          if (title.isBlank()) {
            titleError = true
          } else {
            onSave(title, description, priority, category, dueDate, subtasks.toList())
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("save_task_button"),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = PrimaryBlue,
          contentColor = Color.White
        )
      ) {
        Text(
          text = "Save Task",
          fontWeight = FontWeight.Bold,
          fontSize = 15.5.sp
        )
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
