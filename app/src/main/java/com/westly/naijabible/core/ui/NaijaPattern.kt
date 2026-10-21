package com.westly.naijabible.core.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.ceil

/**
 * The African-pattern tile from the splash design (a 200 x 200 tile of circles, crosses,
 * chevrons and dots). The splash drifts it slowly; the headers draw it still.
 */
private const val TILE = 200f
private const val STROKE_WIDTH = 8f

private val strokePath: Path by lazy {
    Path().apply {
        addOval(Rect(center = Offset(50f, 50f), radius = 26f))
        // X
        moveTo(122f, 22f); lineTo(178f, 78f)
        moveTo(178f, 22f); lineTo(122f, 78f)
        // double chevron
        moveTo(24f, 130f); lineTo(50f, 156f); lineTo(76f, 130f)
        moveTo(24f, 156f); lineTo(50f, 182f); lineTo(76f, 156f)
        // vertical bars
        moveTo(100f, 30f); lineTo(100f, 70f)
        moveTo(100f, 130f); lineTo(100f, 170f)
        // horizontal bars
        moveTo(8f, 100f); lineTo(40f, 100f)
        moveTo(160f, 100f); lineTo(192f, 100f)
        // plus
        moveTo(100f, 88f); lineTo(100f, 112f)
        moveTo(88f, 100f); lineTo(112f, 100f)
    }
}

private val fillPath: Path by lazy {
    Path().apply {
        addOval(Rect(center = Offset(50f, 50f), radius = 8f))
        for (x in listOf(130f, 150f, 170f)) {
            for (y in listOf(130f, 150f, 170f)) {
                addOval(Rect(center = Offset(x, y), radius = 5f))
            }
        }
    }
}

/**
 * Fills the whole canvas with the tile.
 * [originX] and [originY] are the top-left of the first tile in pixels (they may be negative,
 * up to one tile). [unit] is pixels per design unit (use the screen density).
 */
fun DrawScope.drawNaijaPattern(originX: Float, originY: Float, unit: Float, color: Color) {
    val tilePx = TILE * unit
    val cols = ceil((size.width - originX) / tilePx).toInt() + 1
    val rows = ceil((size.height - originY) / tilePx).toInt() + 1
    val stroke = Stroke(width = STROKE_WIDTH, cap = StrokeCap.Round, join = StrokeJoin.Round)
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            withTransform({
                translate(originX + col * tilePx, originY + row * tilePx)
                scale(unit, unit, Offset.Zero)
            }) {
                drawPath(strokePath, color, style = stroke)
                drawPath(fillPath, color)
            }
        }
    }
}
