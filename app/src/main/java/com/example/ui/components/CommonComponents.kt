package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.ui.theme.CategoryChipBg
import com.example.ui.theme.CategoryChipDarkBg
import com.example.ui.theme.CategoryChipDarkText
import com.example.ui.theme.CategoryChipText
import com.example.ui.theme.DarkNavyText
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PriorityHighBg
import com.example.ui.theme.PriorityHighText
import com.example.ui.theme.PriorityLowBg
import com.example.ui.theme.PriorityLowText
import com.example.ui.theme.PriorityMediumBg
import com.example.ui.theme.PriorityMediumText
import com.example.ui.theme.TextDarkPrimary

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon

import androidx.compose.foundation.shape.CircleShape

@Composable
fun TaskEasyLogo(
  modifier: Modifier = Modifier,
  fontSize: TextUnit = 22.sp,
  showIcon: Boolean = false,
  showTagline: Boolean = false
) {
  val isDark = LocalIsDarkTheme.current
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (showIcon) {
        Box(
          modifier = Modifier
            .padding(end = 6.dp)
            .size((fontSize.value + 4).dp)
            .background(Color(0xFF0072F5), RoundedCornerShape(7.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size((fontSize.value - 6).dp)
          )
        }
      }
      Text(
        text = "Task",
        color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF071B45),
        fontWeight = FontWeight.Black,
        fontSize = fontSize,
        letterSpacing = (-0.6).sp
      )
      Text(
        text = "Easy",
        color = Color(0xFF0072F5),
        fontWeight = FontWeight.Black,
        fontSize = fontSize,
        letterSpacing = (-0.6).sp
      )
    }

    if (showTagline) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 3.dp)
      ) {
        Text(
          text = "Plan",
          fontSize = (fontSize.value * 0.38f).sp,
          fontWeight = FontWeight.SemiBold,
          color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5A6E8C)
        )
        Box(
          modifier = Modifier
            .padding(horizontal = 6.dp)
            .size(4.dp)
            .background(Color(0xFF0072F5), CircleShape)
        )
        Text(
          text = "Focus",
          fontSize = (fontSize.value * 0.38f).sp,
          fontWeight = FontWeight.SemiBold,
          color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5A6E8C)
        )
        Box(
          modifier = Modifier
            .padding(horizontal = 6.dp)
            .size(4.dp)
            .background(Color(0xFFFF9500), CircleShape)
        )
        Text(
          text = "Complete",
          fontSize = (fontSize.value * 0.38f).sp,
          fontWeight = FontWeight.SemiBold,
          color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5A6E8C)
        )
      }
    }
  }
}

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
  val isDark = LocalIsDarkTheme.current
  val (bgColor, textColor) = when (priority) {
    Priority.HIGH -> Pair(
      PriorityHighBg,
      PriorityHighText
    )
    Priority.MEDIUM -> Pair(
      if (isDark) Color(0xFF382914) else PriorityMediumBg,
      if (isDark) Color(0xFFFBBF24) else PriorityMediumText
    )
    Priority.LOW -> Pair(
      if (isDark) Color(0xFF143324) else PriorityLowBg,
      if (isDark) Color(0xFF34D399) else PriorityLowText
    )
  }

  Box(
    modifier = modifier
      .background(bgColor, RoundedCornerShape(10.dp))
      .padding(horizontal = 9.dp, vertical = 2.5.dp)
  ) {
    Text(
      text = priority.displayName,
      color = textColor,
      fontSize = 11.5.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun CategoryChip(category: String, modifier: Modifier = Modifier) {
  val isDark = LocalIsDarkTheme.current
  Box(
    modifier = modifier
      .background(
        color = if (isDark) CategoryChipDarkBg else CategoryChipBg,
        shape = RoundedCornerShape(8.dp)
      )
      .padding(horizontal = 8.dp, vertical = 2.5.dp)
  ) {
    Text(
      text = category,
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      color = if (isDark) CategoryChipDarkText else CategoryChipText
    )
  }
}
