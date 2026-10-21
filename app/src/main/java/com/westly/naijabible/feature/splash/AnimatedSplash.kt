package com.westly.naijabible.feature.splash

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.westly.naijabible.R
import com.westly.naijabible.core.theme.NaijaTheme
import com.westly.naijabible.core.ui.LOGO_VIEWBOX_HEIGHT
import com.westly.naijabible.core.ui.LOGO_VIEWBOX_WIDTH
import com.westly.naijabible.core.ui.drawLogoBook
import com.westly.naijabible.core.ui.drawLogoBubble
import com.westly.naijabible.core.ui.drawLogoCross
import com.westly.naijabible.core.ui.drawLogoRibbon
import com.westly.naijabible.core.ui.drawNaijaPattern
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/*
 * Compose version of Splash_Light_Green.html / Splash_Dark.html.
 * Same timings, easings and colours; the numbers below are copied from those files.
 */

@Immutable
private class SplashPalette(
    val bgCenter: Color,
    val bgMid: Color,
    val bgEdge: Color,
    val patternAlpha: Float,
    val vignette: Color,
    val bubble: Color,
    val title: Color,
    val tagline: Color,
    val dot: Color,
    val footer: Color,
    val ring: Color,
    val gold: Color,
    val goldDark: Color,
    val page: Color
)

private val LightSplash = SplashPalette(
    bgCenter = Color(0xFF0A6B3A),
    bgMid = Color(0xFF07603A),
    bgEdge = Color(0xFF04502D),
    patternAlpha = 0.07f,
    vignette = Color(4, 80, 45),
    bubble = Color(0xFFFFFFFF),
    title = Color(0xFFFFFFFF),
    tagline = Color(0xFFF6E7B0),
    dot = Color(0xFFFFFFFF),
    footer = Color(0xFFFFFFFF).copy(alpha = 0.55f),
    ring = Color(0xFFFFFFFF).copy(alpha = 0.35f),
    gold = Color(0xFFD9A01A),
    goldDark = Color(0xFFB9820B),
    page = Color(0xFFF5EFE0)
)

private val DarkSplash = SplashPalette(
    bgCenter = Color(0xFF11161C),
    bgMid = Color(0xFF0E1217),
    bgEdge = Color(0xFF0A0D11),
    patternAlpha = 0.045f,
    vignette = Color(10, 13, 17),
    bubble = Color(0xFF262C36),
    title = Color(0xFF0FA24D),
    tagline = Color(0xFF8B929C),
    dot = Color(0xFFE3B232),
    footer = Color(0xFFA0A7B1),
    ring = Color(0xFFFFFFFF).copy(alpha = 0.18f),
    gold = Color(0xFFD9A01A),
    goldDark = Color(0xFFB9820B),
    page = Color(0xFFE9E1CC)
)

// CSS easings used in the splash file
private val EaseOut = CubicBezierEasing(0f, 0f, 0.58f, 1f)
private val EaseInOut = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
private val PopEase = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
private val CrossEase = CubicBezierEasing(0.34f, 1.8f, 0.64f, 1f)
private val LetterEase = CubicBezierEasing(0.34f, 1.4f, 0.64f, 1f)
private val LeaveEase = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

private const val HOLD_SECONDS = 4.8f
private const val LEAVE_MILLIS = 650
private const val PRELOAD_SECONDS = 4.3f

private fun anim(t: Float, delay: Float, duration: Float, easing: Easing): Float {
    val raw = ((t - delay) / duration).coerceIn(0f, 1f)
    return easing.transform(raw)
}

/** Slow up-and-down float of the logo: 0 to -7 and back, 3.6 s, starting at 2.2 s. */
private fun logoFloat(t: Float): Float {
    val local = t - 2.2f
    if (local <= 0f) return 0f
    val phase = (local % 3.6f) / 3.6f
    val half = if (phase < 0.5f) EaseInOut.transform(phase * 2f) else EaseInOut.transform(2f - phase * 2f)
    return -7f * half
}

@Composable
private fun rememberSplashClock(running: Boolean): State<Float> {
    val time = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(running) {
        if (running) {
            val start = withFrameNanos { it }
            while (true) {
                withFrameNanos { now ->
                    time.floatValue = (now - start) / 1_000_000_000f
                }
            }
        }
    }
    return time
}

@Composable
private fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

/**
 * The animated start screen.
 * It stays at least 4.8 s, and until [dataReady] is true, then fades out over 0.65 s.
 * [onPreloadApp] is called shortly before the fade so the app is ready underneath.
 */
@Composable
fun AnimatedSplash(
    dataReady: Boolean,
    onPreloadApp: () -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = if (NaijaTheme.colors.isDark) DarkSplash else LightSplash
    val reduceMotion = rememberReduceMotion()
    val clock = rememberSplashClock(running = !reduceMotion)
    val leave = remember { Animatable(0f) }
    var minTimeElapsed by remember { mutableStateOf(false) }
    val preload by rememberUpdatedState(onPreloadApp)
    val finished by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        delay((PRELOAD_SECONDS * 1000).toLong())
        preload()
        delay(((HOLD_SECONDS - PRELOAD_SECONDS) * 1000).toLong())
        minTimeElapsed = true
    }
    LaunchedEffect(minTimeElapsed, dataReady) {
        if (minTimeElapsed && dataReady) {
            leave.animateTo(1f, tween(durationMillis = LEAVE_MILLIS, easing = LeaveEase))
            finished()
        }
    }

    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val leaveState: State<Float> = leave.asState()

    Box(
        modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 1f - leaveState.value }
            .pointerInput(Unit) {
                // Swallow touches so nothing underneath reacts while the splash is showing.
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
            }
    ) {
        SplashBackground(palette, clock, reduceMotion)

        SplashContent(
            palette = palette,
            clock = clock,
            leave = leaveState,
            reduceMotion = reduceMotion,
            modifier = Modifier.align(Alignment.Center)
        )

        // Loader dots (hidden when the phone has animations turned off)
        if (!reduceMotion) {
            Canvas(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = navBottom + 96.dp)
                    .size(36.dp)
                    .leaveEffect(leaveState)
                    .graphicsLayer { alpha = anim(clock.value, 2.5f, 0.6f, EaseOut) }
            ) {
                val t = clock.value
                for (k in 0 until 8) {
                    val phase = (((t - 0.12f * k) / 0.96f) % 1f + 1f) % 1f
                    val dotAlpha = 1f - 0.8f * phase
                    val angle = k * 45f * (PI.toFloat() / 180f)
                    val radius = 14f * density
                    val centre = Offset(
                        x = size.width / 2f + radius * sin(angle),
                        y = size.height / 2f - radius * cos(angle)
                    )
                    drawCircle(palette.dot.copy(alpha = dotAlpha), radius = 3f * density, center = centre)
                }
            }
        }

        // Footer
        Text(
            text = stringResource(R.string.copyright),
            style = TextStyle(
                color = palette.footer,
                fontSize = with(LocalDensity.current) { 13.6f.dp.toSp() },
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = navBottom + 36.dp)
                .leaveEffect(leaveState)
                .graphicsLayer {
                    alpha = if (reduceMotion) 1f else anim(clock.value, 2.7f, 0.8f, EaseOut)
                }
        )
    }
}

/** The "out" animation of the splash: grows 6 percent and lifts 8 dp while fading. */
private fun Modifier.leaveEffect(leave: State<Float>): Modifier = graphicsLayer {
    val e = leave.value
    scaleX = 1f + 0.06f * e
    scaleY = 1f + 0.06f * e
    translationY = -8f * density * e
}

@Composable
private fun SplashBackground(
    palette: SplashPalette,
    clock: State<Float>,
    reduceMotion: Boolean
) {
    // Radial gradient, like the CSS "radial-gradient(120% 90% at 50% 45%)"
    Canvas(Modifier.fillMaxSize()) {
        drawRect(palette.bgEdge)
        drawEllipticalGradient(
            cx = size.width * 0.5f,
            cy = size.height * 0.45f,
            rx = size.width * 1.2f,
            ry = size.height * 0.9f,
            stops = arrayOf(0f to palette.bgCenter, 0.4f to palette.bgMid, 1f to palette.bgEdge)
        )
    }
    // Drifting pattern: fades in over 1.6 s, drifts 200 units diagonally every 45 s
    Canvas(
        Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = if (reduceMotion) 1f else anim(clock.value, 0f, 1.6f, EaseOut) }
    ) {
        val drift = if (reduceMotion) 0f else ((clock.value / 45f) % 1f) * 200f
        drawNaijaPattern(
            originX = (-200f + drift) * density,
            originY = (-200f + drift) * density,
            unit = density,
            color = Color.White.copy(alpha = palette.patternAlpha)
        )
    }
    // Soft vignette behind the logo
    Canvas(Modifier.fillMaxSize()) {
        drawEllipticalGradient(
            cx = size.width * 0.5f,
            cy = size.height * 0.46f,
            rx = size.width * 0.6f,
            ry = size.height * 0.4f,
            stops = arrayOf(
                0f to palette.vignette.copy(alpha = 0.6f),
                1f to palette.vignette.copy(alpha = 0f)
            )
        )
    }
}

private fun DrawScope.drawEllipticalGradient(
    cx: Float,
    cy: Float,
    rx: Float,
    ry: Float,
    stops: Array<Pair<Float, Color>>
) {
    val squash = ry / rx
    withTransform({
        translate(cx, cy)
        scale(1f, squash, Offset.Zero)
    }) {
        drawRect(
            brush = Brush.radialGradient(*stops, center = Offset.Zero, radius = rx),
            topLeft = Offset(-cx, -cy / squash),
            size = Size(size.width, size.height / squash)
        )
    }
}

@Composable
private fun SplashContent(
    palette: SplashPalette,
    clock: State<Float>,
    leave: State<Float>,
    reduceMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val config = LocalConfiguration.current
    val screenW = config.screenWidthDp.toFloat()
    val screenH = config.screenHeightDp.toFloat()
    val logoWidth = (screenW * 0.42f).coerceIn(150f, 230f)
    val logoGap = (screenH * 0.04f).coerceIn(18f, 34f)
    val titleSize = (screenW * 0.17f).coerceIn(51.2f, 89.6f)
    val taglineSize = (screenW * 0.039f).coerceIn(14.4f, 17.6f)
    val taglineGap = (screenH * 0.024f).coerceIn(14f, 22f)
    val titleSp = with(LocalDensity.current) { titleSize.dp.toSp() }
    val taglineSp = with(LocalDensity.current) { taglineSize.dp.toSp() }

    Column(
        modifier
            .systemBarsPadding()
            .padding(horizontal = 24.dp)
            .leaveEffect(leave),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SplashLogo(palette, clock, reduceMotion, logoWidth)
        Spacer(Modifier.height(logoGap.dp))

        // Title: each letter drops in with a small turn and a bounce
        val words = listOf("Naija", "Bible")
        var letterIndex = 0
        words.forEach { word ->
            Row {
                word.forEach { letter ->
                    val index = letterIndex++
                    Text(
                        text = letter.toString(),
                        style = TextStyle(
                            color = palette.title,
                            fontSize = titleSp,
                            lineHeight = titleSp * 0.98f,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.02f).em
                        ),
                        modifier = Modifier.graphicsLayer {
                            val e = if (reduceMotion) 1f else anim(clock.value, 1.35f + index * 0.055f, 0.7f, LetterEase)
                            alpha = e.coerceIn(0f, 1f)
                            translationY = size.height * 0.7f * (1f - e)
                            rotationZ = 8f * (1f - e)
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(taglineGap.dp))
        Text(
            text = stringResource(R.string.app_tagline),
            style = TextStyle(
                color = palette.tagline,
                fontSize = taglineSp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.01f.em,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.graphicsLayer {
                val e = if (reduceMotion) 1f else anim(clock.value, 2.2f, 0.8f, EaseOut)
                alpha = e
                translationY = 10f * density * (1f - e)
            }
        )
    }
}

@Composable
private fun SplashLogo(
    palette: SplashPalette,
    clock: State<Float>,
    reduceMotion: Boolean,
    widthDp: Float
) {
    Canvas(Modifier.size(width = widthDp.dp, height = (widthDp * LOGO_VIEWBOX_HEIGHT / LOGO_VIEWBOX_WIDTH).dp)) {
        val t = if (reduceMotion) 100f else clock.value
        val unit = size.width / LOGO_VIEWBOX_WIDTH

        // Two ripple rings behind the bubble (they do not float)
        if (!reduceMotion) {
            drawRipple(t, 0.45f, palette.ring)
            drawRipple(t, 0.70f, palette.ring)
        }

        val floatY = if (reduceMotion) 0f else logoFloat(t) * density

        // Bubble pops in from its tail corner (design units from here on)
        val pop = anim(t, 0.3f, 0.9f, PopEase)
        val popOpacity = anim(t, 0.3f, 0.54f, PopEase).coerceIn(0f, 1f)
        if (pop <= 0.001f || popOpacity <= 0f) return@Canvas
        val tailCorner = Offset(40f, 176.4f)

        withTransform({
            translate(0f, floatY)
            scale(unit, unit, Offset.Zero)
            scale(pop, pop, tailCorner)
            rotate(-12f * (1f - pop), tailCorner)
        }) {
            withGroupAlpha(popOpacity) {
                drawBubbleShadow(unit)
                drawLogoBubble(palette.bubble)

                // Bible rises in
                val book = anim(t, 1.0f, 0.85f, PopEase)
                withGroupAlpha(book.coerceIn(0f, 1f)) {
                    withTransform({
                        translate(0f, 18f * (1f - book))
                        scale(0.7f + 0.3f * book, 0.7f + 0.3f * book, Offset(102f, 140f))
                    }) {
                        drawLogoBook(palette.page, palette.gold)
                    }
                }

                // Cross pops in with a small turn
                val cross = anim(t, 1.55f, 0.6f, CrossEase)
                withGroupAlpha(cross.coerceIn(0f, 1f)) {
                    withTransform({
                        scale(cross, cross, Offset(102f, 68f))
                        rotate(-20f * (1f - cross), Offset(102f, 68f))
                    }) {
                        drawLogoCross(Color.White)
                    }
                }

                // One-time glow ring around the cross
                val glow = anim(t, 1.6f, 1.1f, EaseOut)
                if (glow > 0f && glow < 1f) {
                    val glowScale = 0.3f + 1.6f * glow
                    withGroupAlpha(0.9f * (1f - glow)) {
                        withTransform({ scale(glowScale, glowScale, Offset(102f, 68f)) }) {
                            drawCircle(
                                color = palette.gold,
                                radius = 30f,
                                center = Offset(102f, 68f),
                                style = Stroke(width = 3f)
                            )
                        }
                    }
                }

                // Ribbon slides down
                val ribbon = anim(t, 1.9f, 0.5f, EaseOut)
                withGroupAlpha(ribbon) {
                    withTransform({ translate(0f, -14f * (1f - ribbon)) }) {
                        drawLogoRibbon(palette.goldDark)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawRipple(t: Float, delay: Float, ring: Color) {
    val e = anim(t, delay, 1.6f, EaseOut)
    if (e <= 0f || e >= 1f) return
    val ringScale = 0.5f + 1.4f * e
    val width = size.width
    val centre = Offset(width / 2f, size.height * 0.45f)
    val radius = (width / 2f - density) * ringScale
    drawCircle(
        color = ring.copy(alpha = ring.alpha * 0.8f * (1f - e)),
        radius = radius,
        center = centre,
        style = Stroke(width = 2f * density * ringScale)
    )
}

/** Soft shadow under the bubble (about 0 10px 18px at 28 percent black in the design file). */
private fun DrawScope.drawBubbleShadow(unit: Float) {
    val offsetY = 10f * density / unit
    val blur = 18f * density / unit
    val layers = 6
    for (i in 0 until layers) {
        val grow = blur * 0.5f * (i / (layers - 1f))
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.055f),
            topLeft = Offset(6f - grow, 6f - grow + offsetY),
            size = Size(188f + 2f * grow, 160f + 2f * grow),
            cornerRadius = CornerRadius(58f + grow, 58f + grow)
        )
    }
}

/** Draws [block] as one piece that fades together (so overlapping parts do not show through). */
private inline fun DrawScope.withGroupAlpha(groupAlpha: Float, block: DrawScope.() -> Unit) {
    if (groupAlpha <= 0f) return
    if (groupAlpha >= 1f) {
        block()
        return
    }
    val canvas = drawContext.canvas
    val layerPaint = Paint()
    layerPaint.alpha = groupAlpha
    canvas.saveLayer(Rect(-4000f, -4000f, 4000f, 4000f), layerPaint)
    block()
    canvas.restore()
}
