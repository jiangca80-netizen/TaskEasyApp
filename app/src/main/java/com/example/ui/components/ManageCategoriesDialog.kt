package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.CategoryStore
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DarkNavyText
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TextDarkPrimary

fun categoryIconFor(category: String): ImageVector {
  return when (category.trim().lowercase()) {
    "personal" -> Icons.Default.LocalOffer
    "work" -> Icons.Default.Work
    "family" -> Icons.Default.Home
    "shopping" -> Icons.Default.ShoppingCart
    "others" -> Icons.Default.GridView
    else -> Icons.AutoMirrored.Filled.Label
  }
}

@Composable
fun AddOrEditCategoryDialog(
  initialName: String?,
  onConfirm: (String) -> Result<String>,
  onDismiss: () -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  val isEdit = initialName != null
  var name by remember(initialName) { mutableStateOf(initialName ?: "") }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isEdit) "Edit Category" else "New Category",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = {
            if (it.length <= CategoryStore.MAX_CATEGORY_LENGTH) {
              name = it
              errorText = null
            }
          },
          placeholder = { Text("e.g. Health, Study, Finance", color = SlateGray, fontSize = 14.sp) },
          textStyle = TextStyle(
            color = if (isDark) Color.White else DarkNavyText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
          ),
          isError = errorText != null,
          supportingText = {
            Text(
              text = errorText ?: "${name.trim().length}/${CategoryStore.MAX_CATEGORY_LENGTH} characters",
              color = if (errorText != null) PriorityHigh else SlateGray,
              fontSize = 11.5.sp
            )
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("category_name_input"),
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
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val result = onConfirm(name)
          result.onSuccess {
            onDismiss()
          }.onFailure { err ->
            errorText = err.localizedMessage ?: "Invalid category name"
          }
        },
        modifier = Modifier.testTag("category_dialog_save_button"),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
      ) {
        Text(if (isEdit) "Save" else "Add")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun ManageCategoriesDialog(
  categories: List<String>,
  categoryTaskCounts: Map<String, Int>,
  onAddCategory: (String) -> Result<String>,
  onRenameCategory: (oldName: String, newName: String) -> Result<String>,
  onDeleteCategory: (String) -> Result<String>,
  onDismiss: () -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  var showAddDialog by remember { mutableStateOf(false) }
  var categoryToEdit by remember { mutableStateOf<String?>(null) }
  var categoryToDelete by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Manage Categories",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
        TextButton(
          onClick = { showAddDialog = true },
          modifier = Modifier.testTag("manage_categories_add_button")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add", color = PrimaryBlue, fontWeight = FontWeight.Bold)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 360.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        categories.forEach { cat ->
          val count = categoryTaskCounts.entries
            .firstOrNull { it.key.equals(cat, ignoreCase = true) }
            ?.value ?: 0
          val canDelete = categories.size > 1

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(if (isDark) Color(0xFF1E2D47) else Color(0xFFF8FAFC))
              .border(
                BorderStroke(1.dp, if (isDark) BorderDark else BorderLight),
                RoundedCornerShape(12.dp)
              )
              .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = categoryIconFor(cat),
              contentDescription = null,
              tint = PrimaryBlue,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = cat,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (isDark) TextDarkPrimary else DarkNavyText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "$count ${if (count == 1) "task" else "tasks"}",
                fontSize = 11.5.sp,
                color = SlateGray
              )
            }

            IconButton(
              onClick = { categoryToEdit = cat },
              modifier = Modifier
                .size(40.dp)
                .testTag("edit_category_$cat")
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit $cat",
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
              )
            }

            IconButton(
              onClick = {
                if (count > 0) {
                  categoryToDelete = cat
                } else {
                  onDeleteCategory(cat)
                }
              },
              enabled = canDelete,
              modifier = Modifier
                .size(40.dp)
                .testTag("delete_category_$cat")
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete $cat",
                tint = if (canDelete) PriorityHigh else SlateGray.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Done", fontWeight = FontWeight.Bold)
      }
    }
  )

  if (showAddDialog) {
    AddOrEditCategoryDialog(
      initialName = null,
      onConfirm = { newName -> onAddCategory(newName) },
      onDismiss = { showAddDialog = false }
    )
  }

  if (categoryToEdit != null) {
    val target = categoryToEdit!!
    AddOrEditCategoryDialog(
      initialName = target,
      onConfirm = { newName -> onRenameCategory(target, newName) },
      onDismiss = { categoryToEdit = null }
    )
  }

  if (categoryToDelete != null) {
    val target = categoryToDelete!!
    val remaining = categories.filterNot { it.equals(target, ignoreCase = true) }
    val fallback = remaining.find { it.equals("Others", ignoreCase = true) } ?: remaining.firstOrNull() ?: "Others"
    val affectedCount = categoryTaskCounts.entries
      .firstOrNull { it.key.equals(target, ignoreCase = true) }
      ?.value ?: 0

    AlertDialog(
      onDismissRequest = { categoryToDelete = null },
      title = { Text("Delete Category?", fontWeight = FontWeight.Bold, color = PriorityHigh) },
      text = {
        Text(
          "\"$target\" is currently used by $affectedCount ${if (affectedCount == 1) "task" else "tasks"}. Deleting it will move those tasks to \"$fallback\"."
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onDeleteCategory(target)
            categoryToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = PriorityHigh)
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        TextButton(onClick = { categoryToDelete = null }) {
          Text("Cancel")
        }
      }
    )
  }
}
