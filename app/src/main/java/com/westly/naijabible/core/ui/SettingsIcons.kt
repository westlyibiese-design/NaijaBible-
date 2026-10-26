package com.westly.naijabible.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/*
 * Icons of the Settings screen (Phase 5). They are not in the small built-in icon set,
 * so they are drawn here on a 24 x 24 grid, like the icons in NaijaIcons.kt.
 */

// Crescent moon (the "dark mode" shape of the Material icon set, written with absolute points).
private val moonPath: Path by lazy {
    Path().apply {
        moveTo(12f, 3f)
        cubicTo(7.03f, 3f, 3f, 7.03f, 3f, 12f)
        cubicTo(3f, 16.97f, 7.03f, 21f, 12f, 21f)
        cubicTo(16.97f, 21f, 21f, 16.97f, 21f, 12f)
        cubicTo(21f, 11.54f, 20.96f, 11.08f, 20.9f, 10.64f)
        cubicTo(19.92f, 12.01f, 18.32f, 12.9f, 16.5f, 12.9f)
        cubicTo(13.52f, 12.9f, 11.1f, 10.48f, 11.1f, 7.5f)
        cubicTo(11.1f, 5.69f, 11.99f, 4.08f, 13.36f, 3.1f)
        cubicTo(12.92f, 3.04f, 12.46f, 3f, 12f, 3f)
        close()
    }
}

private val starPath: Path by lazy {
    Path().apply {
        val centerX = 12f
        val centerY = 12.4f
        val outer = 10f
        val inner = 4.3f
        for (i in 0 until 10) {
            val radius = if (i % 2 == 0) outer else inner
            val angle = Math.toRadians((-90 + i * 36).toDouble())
            val x = centerX + (radius * cos(angle)).toFloat()
            val y = centerY + (radius * sin(angle)).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
}

private val flagPath: Path by lazy {
    Path().apply {
        moveTo(5.5f, 4f)
        cubicTo(8.5f, 2.2f, 10.5f, 5.8f, 13.5f, 4.2f)
        cubicTo(15.5f, 3.2f, 17.5f, 3.2f, 19.5f, 4.2f)
        lineTo(19.5f, 13.2f)
        cubicTo(17.5f, 12.2f, 15.5f, 12.2f, 13.5f, 13.2f)
        cubicTo(10.5f, 14.8f, 8.5f, 11.2f, 5.5f, 13f)
        close()
    }
}

private val chevronPath: Path by lazy {
    Path().apply {
        moveTo(9f, 5f)
        lineTo(16f, 12f)
        lineTo(9f, 19f)
    }
}

@Composable
fun MoonIcon(tint: Color, modifier: Modifier = Modifier, size: Dp = 30.dp) {
    Canvas(modifier.size(size)) {
        scale(this.size.width / 24f, Offset.Zero) {
            drawPath(moonPath, tint)
        }
    }
}

/** The "Tt" mark of the Text size row: a large and a small serif T. */
@Composable
fun TextSizeIcon(tint: Color, modifier: Modifier = Modifier) {
    Row(
        modifier.clearAndSetSemantics { },
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "T",
            color = tint,
            fontSize = 28.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Serif,
            maxLines = 1,
            softWrap = false
        )
        Text(
            text = "T",
            color = tint,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Serif,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun InfoIcon(tint: Color, modifier: Modifier = Modifier, size: Dp = 30.dp) {
    Canvas(modifier.size(size)) {
        scale(this.size.width / 24f, Offset.Zero) {
            drawCircle(tint, radius = 9.2f, center = Offset(12f, 12f), style = Stroke(width = 2f))
            drawCircle(tint, radius = 1.35f, center = Offset(12f, 7.7f))
            drawLine(tint, Offset(12f, 11f), Offset(12f, 16.8f), strokeWidth = 2.4f, cap = StrokeCap.Round)
        }
    }
}

@Composable
fun StarIcon(tint: Color, modifier: Modifier = Modifier, size: Dp = 30.dp) {
    Canvas(modifier.size(size)) {
        scale(this.size.width / 24f, Offset.Zero) {
            drawPath(starPath, tint)
            drawPath(starPath, tint, style = Stroke(width = 1.6f, join = StrokeJoin.Round))
        }
    }
}

@Composable
fun ShareIcon(tint: Color, modifier: Modifier = Modifier, size: Dp = 30.dp) {
    Canvas(modifier.size(size)) {
        scale(this.size.width / 24f, Offset.Zero) {
            drawLine(tint, Offset(8.4f, 10.7f), Offset(15.6f, 6.6f), strokeWidth = 2f, cap = StrokeCap.Round)
            drawLine(tint, Offset(8.4f, 13.3f), Offset(15.6f, 17.4f), strokeWidth = 2f, cap = StrokeCap.Round)
            drawCircle(tint, radius = 3.2f, center = Offset(18f, 5.3f))
            drawCircle(tint, radius = 3.2f, center = Offset(6f, 12f))
            drawCircle(tint, radius = 3.2f, center = Offset(18f, 18.7f))
        }
    }
}

@Composable
fun FlagIcon(tint: Color, modifier: Modifier = Modifier, size: Dp = 30.dp) {
    Canvas(modifier.size(size)) {
        scale(this.size.width / 24f, Offset.Zero) {
            drawLine(tint, Offset(5.5f, 3.2f), Offset(5.5f, 21.2f), strokeWidth = 2.4f, cap = StrokeCap.Round)
            drawPath(flagPath, tint)
        }
    }
}

/** The gold arrow at the end of a Settings row. */
@Composable
fun ChevronIcon(tint: Color, modifier: Modifier = Modifier, size: Dp = 26.dp) {
    Canvas(modifier.size(size)) {
        scale(this.size.width / 24f, Offset.Zero) {
            drawPath(
                chevronPath,
                tint,
                style = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}
