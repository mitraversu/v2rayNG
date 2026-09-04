package com.v2ray.ang.ui.compose

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/**
 * Mitra minimal sparkline — 1px thin line, no clutter.
 * Shows last 12 ping samples. Fails (-1) are drawn as bottom dips.
 * Calm, barely there — the perfect touch.
 */
@Composable
fun Sparkline(
    values: List<Long>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 1.2.dp,
    color: Color = colorPing,
    errorColor: Color = colorPingRed
) {
    if (values.size < 2) return

    // Filter and clamp: keep -1 as failure marker, others 20..800ms
    val clamped = remember(values) {
        values.map { v ->
            when {
                v < 0 -> -1L
                v < 15 -> 15L
                v > 900 -> 900L
                else -> v
            }
        }
    }

    val hasError = clamped.any { it < 0 }
    val lineColor = if (hasError) errorColor.copy(alpha = 0.85f) else color.copy(alpha = 0.75f)

    // Minimal: no dots, no fill, just the line
    val density = LocalDensity.current
    val strokePx = with(density) { strokeWidth.toPx() }

    Canvas(modifier = modifier) {
        if (clamped.size < 2) return@Canvas
        val w = size.width
        val h = size.height
        val n = clamped.size
        val stepX = w / (n - 1).coerceAtLeast(1).toFloat()

        // Normalize to height: lower ping = higher on canvas (better)
        val validValues = clamped.filter { it >= 0 }
        val minV = if (validValues.isEmpty()) 0L else validValues.minOrNull() ?: 0L
        val maxV = if (validValues.isEmpty()) 300L else validValues.maxOrNull() ?: 300L
        val range = max(1L, maxV - minV).toFloat()
        val pad = 2.dp.toPx()

        fun yFor(v: Long): Float {
            if (v < 0) return h - pad // failure dips to bottom
            // invert: small ping -> top
            val norm = (v - minV).toFloat() / range // 0..1
            return pad + norm * (h - pad * 2)
        }

        val points = clamped.mapIndexed { i, v ->
            Offset(x = i * stepX, y = yFor(v))
        }

        // Smooth cubic path — minimal tension
        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val cur = points[i]
                val midX = (prev.x + cur.x) / 2
                cubicTo(
                    x1 = midX, y1 = prev.y,
                    x2 = midX, y2 = cur.y,
                    x3 = cur.x, y3 = cur.y
                )
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = strokePx,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

/**
 * Even more minimal: single dot indicator when only 1 sample.
 */
@Composable
fun SparklineOrDot(
    values: List<Long>,
    modifier: Modifier = Modifier
) {
    when {
        values.size >= 2 -> Sparkline(values = values, modifier = modifier)
        values.size == 1 -> {
            val v = values[0]
            val c = when {
                v < 0 -> colorPingRed.copy(alpha = 0.9f)
                v < 250 -> colorPing.copy(alpha = 0.8f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
            }
            Canvas(modifier = modifier) {
                val r = min(size.width, size.height) / 3.2f
                drawCircle(color = c, radius = r, center = Offset(size.width / 2, size.height / 2))
            }
        }
        else -> Unit
    }
}
