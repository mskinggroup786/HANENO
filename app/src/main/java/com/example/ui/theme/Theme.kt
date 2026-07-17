package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  lightColorScheme(
    primary = CleanBlueAccent,
    secondary = CleanSoftBlue,
    tertiary = CleanRedAccent,
    background = CleanBg,
    surface = CleanCardBg,
    onPrimary = Color.White,
    onSecondary = CleanNavy,
    onTertiary = CleanRedText,
    onBackground = CleanNavy,
    onSurface = CleanNavy,
    surfaceVariant = CleanLightBlue,
    onSurfaceVariant = CleanNavy,
    outline = CleanBorder
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CleanBlueAccent,
    secondary = CleanSoftBlue,
    tertiary = CleanRedAccent,
    background = CleanBg,
    surface = CleanCardBg,
    onPrimary = Color.White,
    onSecondary = CleanNavy,
    onTertiary = CleanRedText,
    onBackground = CleanNavy,
    onSurface = CleanNavy,
    surfaceVariant = CleanLightBlue,
    onSurfaceVariant = CleanNavy,
    outline = CleanBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic colors by default so our custom Clean Minimalism theme is always applied
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
