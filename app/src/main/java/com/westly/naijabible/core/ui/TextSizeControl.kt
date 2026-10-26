package com.westly.naijabible.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.westly.naijabible.R
import com.westly.naijabible.core.prefs.ReadingTextScale
import com.westly.naijabible.core.theme.NaijaTheme
import kotlin.math.roundToInt

/**
 * The text size slider: a small "A", a gold track with a gold knob and 5 steps, a large "A".
 * [step] is 0 (smallest) to 4 (largest). [onStepChange] is called only when the step really changes.
 * Used twice, each with its own saved value: the reading screen (Aa sheet, reading text size)
 * and Settings (app text size). [label] is what a screen reader says for this slider.
 * The two "A" letters have a fixed size, so the slider does not move under the finger while
 * the whole app changes size.
 */
@Composable
fun TextSizeControl(
    step: Int,
    onStepChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    val density = LocalDensity.current
    val lastIndex = ReadingTextScale.steps.lastIndex
    val currentOnStepChange by rememberUpdatedState(onStepChange)

    // The knob follows the finger at once; the saved value catches up a moment later.
    var shown by remember { mutableStateOf(step.coerceIn(0, lastIndex)) }
    LaunchedEffect(step) { shown = step.coerceIn(0, lastIndex) }

    fun select(target: Int) {
        val clamped = target.coerceIn(0, lastIndex)
        if (clamped != shown) {
            shown = clamped
            currentOnStepChange(clamped)
        }
    }

    fun stepAt(x: Float, width: Float, padding: Float): Int {
        val usable = width - 2f * padding
        if (usable <= 0f) return shown
        val fraction = ((x - padding) / usable).coerceIn(0f, 1f)
        return (fraction * lastIndex).roundToInt()
    }

    val stateText = stringResource(R.string.text_size_state, shown + 1, lastIndex + 1)

    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "A",
            color = colors.textSecondary,
            fontSize = with(density) { 18.dp.toSp() },
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.width(10.dp))
        Box(
            Modifier
                .weight(1f)
                .height(48.dp)
                .semantics {
                    contentDescription = label
                    stateDescription = stateText
                    progressBarRangeInfo = ProgressBarRangeInfo(
                        shown.toFloat(),
                        0f..lastIndex.toFloat(),
                        lastIndex - 1
                    )
                    setProgress { target ->
                        select(target.roundToInt())
                        true
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        select(stepAt(offset.x, size.width.toFloat(), 12.dp.toPx()))
                    }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { start ->
                            select(stepAt(start.x, size.width.toFloat(), 12.dp.toPx()))
                        },
                        onHorizontalDrag = { change, _ ->
                            change.consume()
                            select(stepAt(change.position.x, size.width.toFloat(), 12.dp.toPx()))
                        }
                    )
                }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val padding = 12.dp.toPx()
                val trackHeight = 4.dp.toPx()
                val centerY = size.height / 2f
                val left = padding
                val right = size.width - padding
                val knobX = left + (right - left) * shown / lastIndex.toFloat()
                drawLine(
                    color = colors.divider,
                    start = Offset(left, centerY),
                    end = Offset(right, centerY),
                    strokeWidth = trackHeight,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = colors.gold,
                    start = Offset(left, centerY),
                    end = Offset(knobX, centerY),
                    strokeWidth = trackHeight,
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = colors.gold,
                    radius = 10.dp.toPx(),
                    center = Offset(knobX, centerY)
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = "A",
            color = colors.textPrimary,
            fontSize = with(density) { 30.dp.toSp() },
            fontWeight = FontWeight.Bold
        )
    }
}
