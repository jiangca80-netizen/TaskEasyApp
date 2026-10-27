package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.TaskEasyLogo
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.DarkNavyText
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TextDarkPrimary

@Composable
fun SplashScreen(
  onGetStarted: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val bgBrush = if (isDark) {
    Brush.verticalGradient(listOf(BackgroundDark, Color(0xFF131D33)))
  } else {
    Brush.verticalGradient(listOf(Color(0xFFF0F6FF), BackgroundLight))
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(bgBrush)
      .padding(horizontal = 24.dp, vertical = 20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Top & Center Branding: Using the uploaded TaskEasy 3D Calendar Logo
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
      ) {
        Box(
          modifier = Modifier
            .size(240.dp)
            .shadow(16.dp, RoundedCornerShape(32.dp))
            .clip(RoundedCornerShape(32.dp))
            .background(if (isDark) Color(0xFF131D33) else Color.White)
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_taskeasy_branding),
            contentDescription = "TaskEasy Branding Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "Personal Task Manager",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
          ),
          color = if (isDark) Color(0xFFF8FAFC) else DarkNavyText
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = "Plan",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5A6E8C)
          )
          Box(
            modifier = Modifier
              .padding(horizontal = 6.dp)
              .size(5.dp)
              .background(Color(0xFF0072F5), CircleShape)
          )
          Text(
            text = "Focus",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5A6E8C)
          )
          Box(
            modifier = Modifier
              .padding(horizontal = 6.dp)
              .size(5.dp)
              .background(Color(0xFFFF9500), CircleShape)
          )
          Text(
            text = "Complete",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5A6E8C)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "A simpler, friendlier way\nto plan, start, and complete your tasks",
          fontSize = 14.sp,
          color = SlateGray,
          textAlign = TextAlign.Center,
          lineHeight = 20.sp
        )
      }

      // Bottom Section: Dots and Get Started Button
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .width(20.dp)
              .height(6.dp)
              .background(PrimaryBlue, RoundedCornerShape(3.dp))
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .size(6.dp)
              .background(if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1), CircleShape)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .size(6.dp)
              .background(if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1), CircleShape)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onGetStarted,
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("get_started_button"),
          shape = RoundedCornerShape(27.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            contentColor = Color.White
          ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
          Text(
            text = "Get Started →",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(12.dp))
      }
    }
  }
}
