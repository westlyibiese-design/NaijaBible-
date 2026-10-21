package com.westly.naijabible.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import com.westly.naijabible.core.theme.NaijaTheme

/*
 * The logo (white speech bubble holding a gold Bible with a white cross), drawn in the same
 * 200 x 196 design space as the splash file. The splash animates the pieces one by one;
 * the headers draw them all at once.
 */
const val LOGO_VIEWBOX_WIDTH = 200f
const val LOGO_VIEWBOX_HEIGHT = 196f

private val tailPath: Path by lazy {
    Path().apply {
        moveTo(44f, 150f)
        lineTo(22f, 190f)
        lineTo(96f, 164f)
        close()
    }
}

private val ribbonPath: Path by lazy {
    Path().apply {
        moveTo(78f, 132f)
        lineTo(90f, 132f)
        lineTo(90f, 152f)
        lineTo(84f, 146f)
        lineTo(78f, 152f)
        close()
    }
}

fun DrawScope.drawLogoBubble(color: Color) {
    drawRoundRect(
        color = color,
        topLeft = Offset(6f, 6f),
        size = Size(188f, 160f),
        cornerRadius = CornerRadius(58f, 58f)
    )
    drawPath(tailPath, color)
}

fun DrawScope.drawLogoBook(page: Color, gold: Color) {
    drawRoundRect(page, Offset(64f, 112f), Size(76f, 30f), CornerRadius(7f, 7f))
    drawRoundRect(gold, Offset(64f, 34f), Size(76f, 98f), CornerRadius(8f, 8f))
    drawRoundRect(Color.Black.copy(alpha = 0.14f), Offset(64f, 34f), Size(9f, 98f), CornerRadius(5f, 5f))
}

fun DrawScope.drawLogoCross(color: Color) {
    drawRoundRect(color, Offset(98f, 48f), Size(8f, 44f), CornerRadius(1.5f, 1.5f))
    drawRoundRect(color, Offset(86f, 60f), Size(32f, 8f), CornerRadius(1.5f, 1.5f))
}

fun DrawScope.drawLogoRibbon(color: Color) {
    drawPath(ribbonPath, color)
}

/** The finished logo. [width] is the width; the height follows the 200:196 shape. */
@Composable
fun NaijaLogo(width: Dp, modifier: Modifier = Modifier) {
    val colors = NaijaTheme.colors
    Canvas(modifier.size(width = width, height = width * (LOGO_VIEWBOX_HEIGHT / LOGO_VIEWBOX_WIDTH))) {
        val unit = size.width / LOGO_VIEWBOX_WIDTH
        withTransform({ scale(unit, unit, Offset.Zero) }) {
            drawLogoBubble(colors.logoBubble)
            drawLogoBook(colors.logoBookPage, colors.logoBookGold)
            drawLogoCross(colors.logoBubble)
            drawLogoRibbon(colors.logoRibbon)
        }
    }
}
