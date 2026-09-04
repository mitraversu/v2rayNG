package com.v2ray.ang.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.R
import com.v2ray.ang.ui.compose.colorPingRed

private val COMMON_PORTS = listOf("443", "80", "8080", "8443", "2053", "2096", "2083", "2087")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PortFilterDialog(
    mainViewModel: MainViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
    var input by remember { mutableStateOf("") }
    var ports by remember { mutableStateOf(setOf<String>()) }
    var error by remember { mutableStateOf<String?>(null) }

    // Parse input live
    LaunchedEffect(input) {
        val parsed = input.split(",", " ", ";", "\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()
        ports = parsed
        error = parsed.firstOrNull { it.toIntOrNull() == null || it.toInt() !in 1..65535 }?.let {
            "Invalid port: $it"
        }
    }

    val count = remember(ports) {
        if (ports.isEmpty() || error != null) 0 else mainViewModel.countByPort(ports)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        title = {
            Column {
                Text(
                    stringResource(R.string.title_del_by_port),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.port_filter_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.port_filter_label)) },
                    placeholder = { Text(stringResource(R.string.port_filter_hint), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                    supportingText = {
                        when {
                            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
                            ports.isNotEmpty() -> Text(
                                if (count == 0) stringResource(R.string.toast_no_matching_port, ports.joinToString(", "))
                                else stringResource(R.string.title_port_filter_result, count, ports.joinToString(", ")),
                                color = if (count == 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                            else -> Text(stringResource(R.string.port_filter_description), style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    isError = error != null,
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    shape = RoundedCornerShape(14.dp)
                )

                Text(
                    "Quick add:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    COMMON_PORTS.forEach { p ->
                        val selected = p in ports
                        AssistChip(
                            onClick = {
                                val new = if (selected) ports - p else ports + p
                                input = new.joinToString(", ")
                            },
                            label = { Text(p, fontSize = 12.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                                labelColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = null
                        )
                    }
                }

                if (count > 0) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape)
                                .background(colorPingRed)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.confirm_delete_port_filtered, count, ports.joinToString(", ")),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (ports.isNotEmpty() && error == null && count > 0) onConfirm(ports)
                },
                enabled = ports.isNotEmpty() && error == null && count > 0,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    if (count > 0) stringResource(R.string.action_delete) + " ($count)" else stringResource(R.string.action_delete),
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
