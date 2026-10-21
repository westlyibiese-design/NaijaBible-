package com.westly.naijabible.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Verse text multiplier chosen in Settings. The reading screen (Phase 4) multiplies it on top of the phone's font scale. */
val LocalReadingTextScale = compositionLocalOf { 1f }

private val NaijaShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFF0A6B3A),
    onPrimary = Color(0xFFFFFFFF),
    secondary = LightNaijaColors.gold,
    onSecondary = Color(0xFF141414),
    background = LightNaijaColors.pageBackground,
    onBackground = LightNaijaColors.textPrimary,
    surface = LightNaijaColors.card,
    onSurface = LightNaijaColors.textPrimary,
    onSurfaceVariant = LightNaijaColors.textSecondary,
    outline = LightNaijaColors.divider,
    outlineVariant = LightNaijaColors.divider
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF0FA24D),
    onPrimary = Color(0xFF0F1217),
    secondary = DarkNaijaColors.gold,
    onSecondary = Color(0xFF0F1217),
    background = DarkNaijaColors.pageBackground,
    onBackground = DarkNaijaColors.textPrimary,
    surface = DarkNaijaColors.card,
    onSurface = DarkNaijaColors.textPrimary,
    onSurfaceVariant = DarkNaijaColors.textSecondary,
    outline = DarkNaijaColors.divider,
    outlineVariant = DarkNaijaColors.divider
)

object NaijaTheme {
    val colors: NaijaColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNaijaColors.current
}

@Composable
fun NaijaBibleTheme(
    darkTheme: Boolean,
    readingTextScale: Float,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkNaijaColors else LightNaijaColors
    CompositionLocalProvider(
        LocalNaijaColors provides colors,
        LocalReadingTextScale provides readingTextScale
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkScheme else LightScheme,
            typography = NaijaTypography,
            shapes = NaijaShapes,
            content = content
        )
    }
}
