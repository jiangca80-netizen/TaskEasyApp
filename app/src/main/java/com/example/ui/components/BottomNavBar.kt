package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DarkNavBottom
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SurfaceLight
import com.example.ui.viewmodel.AppScreen

@Composable
fun TaskEasyBottomBar(
  currentScreen: AppScreen,
  onNavigate: (AppScreen) -> Unit,
  onAddClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val bgColor = if (isDark) DarkNavBottom else SurfaceLight

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = bgColor
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
      HorizontalDivider(
        color = if (isDark) BorderDark else BorderLight,
        thickness = 1.dp
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(62.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .padding(horizontal = 4.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // 1. Tasks
          BottomNavItem(
            label = "Tasks",
            selected = currentScreen == AppScreen.TASKS,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            onClick = { onNavigate(AppScreen.TASKS) },
            modifier = Modifier.weight(1f).testTag("nav_tasks")
          )

          // 2. Overview (Dashboard)
          BottomNavItem(
            label = "Overview",
            selected = currentScreen == AppScreen.DASHBOARD,
            selectedIcon = Icons.Filled.BarChart,
            unselectedIcon = Icons.Outlined.BarChart,
            onClick = { onNavigate(AppScreen.DASHBOARD) },
            modifier = Modifier.weight(1f).testTag("nav_overview")
          )

          // Space for center Add button
          Spacer(modifier = Modifier.weight(1f))

          // 4. Calendar
          BottomNavItem(
            label = "Calendar",
            selected = currentScreen == AppScreen.CALENDAR,
            selectedIcon = Icons.Filled.CalendarMonth,
            unselectedIcon = Icons.Outlined.CalendarMonth,
            onClick = { onNavigate(AppScreen.CALENDAR) },
            modifier = Modifier.weight(1f).testTag("nav_calendar")
          )

          // 5. Settings
          BottomNavItem(
            label = "Settings",
            selected = currentScreen == AppScreen.SETTINGS,
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            onClick = { onNavigate(AppScreen.SETTINGS) },
            modifier = Modifier.weight(1f).testTag("nav_settings")
          )
        }

        // Prominent circular floating Add (+) button matching reference
        FloatingActionButton(
          onClick = onAddClick,
          modifier = Modifier
            .align(Alignment.Center)
            .offset(y = (-10).dp)
            .size(54.dp)
            .testTag("nav_center_add_button"),
          shape = CircleShape,
          containerColor = PrimaryBlue,
          contentColor = Color.White,
          elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 5.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Task",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun BottomNavItem(
  label: String,
  selected: Boolean,
  selectedIcon: ImageVector,
  unselectedIcon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val iconColor = if (selected) PrimaryBlue else SlateGray
  val textColor = if (selected) PrimaryBlue else SlateGray

  Column(
    modifier = modifier
      .clickable { onClick() }
      .padding(vertical = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = if (selected) selectedIcon else unselectedIcon,
      contentDescription = label,
      tint = iconColor,
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
      color = textColor
    )
  }
}
