package com.rupiksha.distributer.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = BrandPrimary,
        secondary = BrandPrimaryDark,
        background = BackgroundDark,
        surface = SurfaceDark,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = TextPrimaryDark,
        onSurface = TextPrimaryDark,
        outline = BorderDark,
        onSurfaceVariant = TextSecondaryDark
    )

private val LightColorScheme =
    lightColorScheme(
        primary = BrandPrimary,
        secondary = BrandPrimaryDark,
        background = BackgroundLight,
        surface = SurfaceLight,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = TextPrimaryLight,
        onSurface = TextPrimaryLight,
        outline = BorderLight,
        onSurfaceVariant = TextSecondaryLight
    )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Default to bright theme
  // Disable dynamic color to maintain brand consistency
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
