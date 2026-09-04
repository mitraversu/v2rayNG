package com.v2ray.ang.ui.main

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.R
import com.v2ray.ang.ui.compose.Sparkline
import com.v2ray.ang.ui.compose.colorPing
import com.v2ray.ang.ui.compose.colorPingRed

@Composable
fun TestingHud(
    isTesting: Boolean,
    testTotal: Int,
    testDone: Int,
    statusText: String,
    testingGroupRemarks: String?,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isTesting || testTotal <= 0) return
    val progress = (testDone.toFloat() / testTotal.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val pulse = rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "pulseAlpha"
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(colorPing.copy(alpha = pulse.value))
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (testingGroupRemarks.isNullOrBlank()) "Testing"
                        else "Testing ${testingGroupRemarks}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "$testDone / $testTotal",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(12.dp))
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.action_cancel), style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    }
}

@Composable
fun LatencyFilterChips(
    selected: LatencyFilter,
    counts: Map<LatencyFilter, Int>,
    onSelected: (LatencyFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scroll)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChipItem(LatencyFilter.All, "All", counts[LatencyFilter.All] ?: 0, selected, onSelected)
        ChipItem(LatencyFilter.Good, "Good <100", counts[LatencyFilter.Good] ?: 0, selected, onSelected, dotColor = colorPing)
        ChipItem(LatencyFilter.Okay, "Okay 100-299", counts[LatencyFilter.Okay] ?: 0, selected, onSelected, dotColor = Color(0xFF8A6D00))
        ChipItem(LatencyFilter.Slow, "Slow ≥300", counts[LatencyFilter.Slow] ?: 0, selected, onSelected, dotColor = Color(0xFF7A4A00))
        ChipItem(LatencyFilter.Failed, "Failed", counts[LatencyFilter.Failed] ?: 0, selected, onSelected, dotColor = colorPingRed)
        if ((counts[LatencyFilter.Untested] ?: 0) > 0) {
            ChipItem(LatencyFilter.Untested, "Untested", counts[LatencyFilter.Untested] ?: 0, selected, onSelected, dotColor = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun ChipItem(
    filter: LatencyFilter,
    label: String,
    count: Int,
    selected: LatencyFilter,
    onSelected: (LatencyFilter) -> Unit,
    dotColor: Color? = null
) {
    val isSelected = selected == filter
    FilterChip(
        selected = isSelected,
        onClick = { onSelected(filter) },
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (dotColor != null) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(dotColor))
                    Spacer(Modifier.width(6.dp))
                }
                Text("$label · $count", style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium))
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true, selected = isSelected,
            borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
            selectedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
            borderWidth = 1.dp, selectedBorderWidth = 1.dp
        ),
        shape = RoundedCornerShape(100.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PingHistoryBottomSheet(
    remarks: String,
    history: List<Long>,
    currentDelay: Long,
    typeDescription: String,
    address: String,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(remarks.ifBlank { "Server" }, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(2.dp))
                    Text("$typeDescription • $address", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onDismiss) { Icon(painterResource(R.drawable.ic_arrow_back_24dp), contentDescription = "Close") }
            }
            // Current pill
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            when {
                                currentDelay < 0 -> MaterialTheme.colorScheme.errorContainer
                                currentDelay == 0L -> MaterialTheme.colorScheme.surfaceContainerHighest
                                currentDelay < 150 -> colorPing.copy(alpha = 0.14f)
                                currentDelay < 300 -> Color(0xFFFFE9A8)
                                else -> Color(0xFFFFD1A3)
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        when {
                            currentDelay < 0 -> "Failed"
                            currentDelay == 0L -> "Untested"
                            else -> "${currentDelay}ms"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = when {
                            currentDelay < 0 -> MaterialTheme.colorScheme.onErrorContainer
                            currentDelay == 0L -> MaterialTheme.colorScheme.onSurfaceVariant
                            currentDelay < 300 -> MaterialTheme.colorScheme.onSurface
                            else -> Color(0xFF6B3B00)
                        }
                    )
                }
                Text("${history.size} samples", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                if (history.isNotEmpty()) {
                    OutlinedButton(onClick = onClear, contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp), modifier = Modifier.height(36.dp)) {
                        Text("Clear", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            // Chart
            if (history.size >= 2) {
                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainer, border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.4f)), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Sparkline(values = history, modifier = Modifier.fillMaxWidth().height(80.dp), strokeWidth = 1.8.dp)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            val valid = history.filter { it >= 0 }
                            val min = valid.minOrNull() ?: 0L
                            val max = valid.maxOrNull() ?: 0L
                            val avg = if (valid.isNotEmpty()) valid.average().toInt() else 0
                            val jitter = if (valid.size >= 2) (max - min) else 0L
                            StatCell("Min", if (min == 0L && valid.isEmpty()) "—" else "${min}ms")
                            StatCell("Avg", if (valid.isEmpty()) "—" else "${avg}ms")
                            StatCell("Max", if (max == 0L && valid.isEmpty()) "—" else "${max}ms")
                            StatCell("Jitter", "${jitter}ms")
                        }
                    }
                }
                // timeline dots
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    history.takeLast(12).forEachIndexed { idx, v ->
                        val c = when {
                            v < 0 -> colorPingRed
                            v < 120 -> colorPing
                            v < 300 -> Color(0xFF9C8A00)
                            else -> Color(0xFFB86B00)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.size(10.dp).clip(CircleShape).background(c))
                            Spacer(Modifier.height(4.dp))
                            Text(if (v < 0) "✕" else "${v}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else if (history.size == 1) {
                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainer, border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.4f)), modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.size(14.dp).clip(CircleShape).background(if (history[0] < 0) colorPingRed else colorPing))
                            Spacer(Modifier.height(8.dp))
                            Text("${history[0]}ms single sample", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                Box(Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceContainer).border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.3f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                    Text("No history yet — run a test", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun StatCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestSummaryBottomSheet(
    summary: TestSummary,
    onDismiss: () -> Unit,
    onSort: () -> Unit,
    onRetryFailed: () -> Unit,
    onFilterFailed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Handle + title
            Text("Test finished", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(
                "${summary.success} good • ${summary.failed} failed • ${summary.untested} untested of ${summary.total}",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // Stats cards
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard("Best", if (summary.bestDelay < 0) "—" else "${summary.bestDelay}ms", summary.bestRemarks.ifBlank { "—" }.take(18), colorPing.copy(0.12f), Modifier.weight(1f))
                SummaryCard("Median", if (summary.medianDelay < 0) "—" else "${summary.medianDelay}ms", "middle", Color(0xFFFFF4C2), Modifier.weight(1f))
                SummaryCard("Worst", if (summary.worstDelay < 0) "—" else "${summary.worstDelay}ms", "slowest", Color(0xFFFFE0C2), Modifier.weight(1f))
            }
            // Failure callout if any
            if (summary.failed > 0) {
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_logcat_24dp), contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("${summary.failed} failed — tap Retry to test only those", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.weight(1f))
                    }
                }
            }
            // Actions
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onSort(); onDismiss() }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Text("Sort by speed")
                }
                if (summary.failed > 0) {
                    OutlinedButton(onClick = { onRetryFailed(); onDismiss() }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text("Retry failed")
                    }
                }
            }
            if (summary.failed > 0) {
                OutlinedButton(onClick = { onFilterFailed(); onDismiss() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text("Show failed only")
                }
            }
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Dismiss") }
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, sub: String, bg: Color, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(14.dp), color = bg, modifier = modifier) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
