package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
  SYSTEM,
  LIGHT,
  DARK
}

val LocalIsDarkTheme = compositionLocalOf { false }

private val TaskEasyDarkColorScheme =
  darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = SurfaceDarkVariant,
    onPrimaryContainer = Color(0xFF90CAF9),
    secondary = InProgressOrange,
    onSecondary = Color.White,
    secondaryContainer = InProgressCardDark,
    onSecondaryContainer = Color(0xFFFFCC80),
    tertiary = CompletedGreen,
    onTertiary = Color.White,
    tertiaryContainer = CompletedCardDark,
    onTertiaryContainer = Color(0xFFA5D6A7),
    background = BackgroundDark,
    onBackground = TextDarkPrimary,
    surface = SurfaceDark,
    onSurface = TextDarkPrimary,
    surfaceVariant = SurfaceDarkVariant,
    onSurfaceVariant = TextDarkSecondary,
    outline = BorderDark
  )

private val TaskEasyLightColorScheme =
  lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlueLightBg,
    onPrimaryContainer = PrimaryBlueDark,
    secondary = InProgressOrange,
    onSecondary = Color.White,
    secondaryContainer = InProgressOrangeLightBg,
    onSecondaryContainer = Color(0xFFC77700),
    tertiary = CompletedGreen,
    onTertiary = Color.White,
    tertiaryContainer = CompletedGreenLightBg,
    onTertiaryContainer = Color(0xFF0F8F5C),
    background = BackgroundLight,
    onBackground = DarkNavyText,
    surface = SurfaceLight,
    onSurface = DarkNavyText,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = SlateGray,
    outline = BorderLight
  )

@Composable
fun TaskEasyTheme(
  themeMode: AppThemeMode = AppThemeMode.SYSTEM,
  content: @Composable () -> Unit
) {
  val isDark = when (themeMode) {
    AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    AppThemeMode.LIGHT -> false
    AppThemeMode.DARK -> true
  }

  val colorScheme = if (isDark) TaskEasyDarkColorScheme else TaskEasyLightColorScheme

  CompositionLocalProvider(LocalIsDarkTheme provides isDark) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
