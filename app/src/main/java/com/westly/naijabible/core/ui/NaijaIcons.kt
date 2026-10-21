package com.westly.naijabible.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
 * Two icons that are not in the small built-in icon set. Drawn on a 24 x 24 grid.
 * Filled = selected / saved, outline = not selected.
 */

private val bookmarkPath: Path by lazy {
    Path().apply {
        moveTo(6.5f, 3f)
        lineTo(17.5f, 3f)
        lineTo(17.5f, 21f)
        lineTo(12f, 17f)
        lineTo(6.5f, 21f)
        close()
    }
}

private fun pagePath(mirror: Boolean): Path {
    fun x(value: Float) = if (mirror) 24f - value else value
    return Path().apply {
        moveTo(x(11.4f), 7.2f)
        cubicTo(x(9.9f), 5.8f, x(7f), 5f, x(3f), 5.5f)
        lineTo(x(3f), 18.5f)
        cubicTo(x(7f), 18f, x(9.9f), 18.7f, x(11.4f), 20.1f)
        close()
    }
}

private val leftPagePath: Path by lazy { pagePath(mirror = false) }
private val rightPagePath: Path by lazy { pagePath(mirror = true) }

@Composable
fun BookmarkIcon(
    filled: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp
) {
    Canvas(modifier.size(size)) {
        scale(this.size.width / 24f, Offset.Zero) {
            if (filled) {
                drawPath(bookmarkPath, tint)
                drawPath(bookmarkPath, tint, style = Stroke(width = 1.5f, join = StrokeJoin.Round))
            } else {
                drawPath(
                    bookmarkPath,
                    tint,
                    style = Stroke(width = 1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}

@Composable
fun OpenBookIcon(
    filled: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp
) {
    Canvas(modifier.size(size)) {
        scale(this.size.width / 24f, Offset.Zero) {
            if (filled) {
                drawPath(leftPagePath, tint)
                drawPath(rightPagePath, tint)
            } else {
                val outline = Stroke(width = 1.7f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                drawPath(leftPagePath, tint, style = outline)
                drawPath(rightPagePath, tint, style = outline)
            }
        }
    }
}
