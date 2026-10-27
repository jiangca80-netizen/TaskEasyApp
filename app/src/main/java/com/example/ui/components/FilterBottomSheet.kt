package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.viewmodel.DueFilter
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
  selectedPriority: Priority?,
  selectedCategory: String?,
  selectedDue: DueFilter,
  selectedSort: SortOption,
  onPriorityChange: (Priority?) -> Unit,
  onCategoryChange: (String?) -> Unit,
  onDueChange: (DueFilter) -> Unit,
  onSortChange: (SortOption) -> Unit,
  onClearFilters: () -> Unit,
  onDismiss: () -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val categories = listOf("Personal", "Work", "Family", "Shopping", "Others")

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
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Filters & Sorting",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
          ),
          color = if (isDark) TextDarkPrimary else DarkNavy
        )
        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = SlateGray
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 1: Priority
      Text(
        text = "Priority",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = if (isDark) TextDarkPrimary else DarkNavy
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChipItem(
          label = "All",
          selected = selectedPriority == null,
          onClick = { onPriorityChange(null) }
        )
        Priority.entries.forEach { p ->
          FilterChipItem(
            label = p.displayName,
            selected = selectedPriority == p,
            onClick = { onPriorityChange(p) }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 2: Category
      Text(
        text = "Category",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = if (isDark) TextDarkPrimary else DarkNavy
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChipItem(
          label = "All",
          selected = selectedCategory == null,
          onClick = { onCategoryChange(null) }
        )
        categories.forEach { cat ->
          FilterChipItem(
            label = cat,
            selected = selectedCategory == cat,
            onClick = { onCategoryChange(cat) }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 3: Due Date Filter
      Text(
        text = "Due Date",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = if (isDark) TextDarkPrimary else DarkNavy
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChipItem(
          label = "All",
          selected = selectedDue == DueFilter.ALL,
          onClick = { onDueChange(DueFilter.ALL) }
        )
        FilterChipItem(
          label = "Due Today",
          selected = selectedDue == DueFilter.TODAY,
          onClick = { onDueChange(DueFilter.TODAY) }
        )
        FilterChipItem(
          label = "Overdue",
          selected = selectedDue == DueFilter.OVERDUE,
          onClick = { onDueChange(DueFilter.OVERDUE) }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 4: Sort Option
      Text(
        text = "Sort By",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = if (isDark) TextDarkPrimary else DarkNavy
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SortOption.entries.forEach { option ->
          FilterChipItem(
            label = option.displayName,
            selected = selectedSort == option,
            onClick = { onSortChange(option) }
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = {
            onClearFilters()
            onDismiss()
          },
          modifier = Modifier.weight(1f).testTag("clear_filters_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Clear Filters")
        }

        Button(
          onClick = onDismiss,
          modifier = Modifier.weight(1f).testTag("apply_filters_button"),
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Apply")
        }
      }
    }
  }
}

@Composable
private fun FilterChipItem(
  label: String,
  selected: Boolean,
  onClick: () -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  val bg = if (selected) PrimaryBlue else if (isDark) Color(0xFF2A364F) else Color(0xFFF1F5F9)
  val textCol = if (selected) Color.White else if (isDark) TextDarkPrimary else DarkNavy

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .background(bg)
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 7.dp)
  ) {
    Text(
      text = label,
      color = textCol,
      fontSize = 13.sp,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
    )
  }
}
