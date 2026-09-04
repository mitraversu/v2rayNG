package com.v2ray.ang.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v2ray.ang.R

@Composable
fun MainBottomBar(
    displayText: String,
    isRunning: Boolean,
    isDarkTheme: Boolean,
    isTesting: Boolean = false,
    onAction: (MainAction) -> Unit
) {
    // Mitra: when testing, FAB becomes Cancel (outline), dot pulses
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(72.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isTesting -> Color(0xFF0F7A5F).copy(alpha = 0.9f)
                        isRunning -> Color(0xFF0F7A5F)
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }
                )
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = !isTesting, onClick = { onAction(MainAction.TestCurrentServer) })
                .semantics { contentDescription = displayText },
            maxLines = 1
        )
        Spacer(Modifier.width(16.dp))
        if (isTesting) {
            FloatingActionButton(
                onClick = { onAction(MainAction.CancelTesting) },
                modifier = Modifier.size(52.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_stop_24dp),
                    contentDescription = stringResource(R.string.action_cancel),
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            FloatingActionButton(
                onClick = { onAction(MainAction.ToggleService) },
                modifier = Modifier.size(52.dp),
                containerColor = if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                contentColor = if (isRunning) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Icon(
                    painter = if (isRunning) painterResource(R.drawable.ic_stop_24dp)
                    else painterResource(R.drawable.ic_play_24dp),
                    contentDescription = stringResource(
                        if (isRunning) R.string.acc_stop else R.string.acc_start
                    ),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
