package com.v2ray.ang.ui.server

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.v2ray.ang.R
import com.v2ray.ang.enums.EConfigType

class ServerWireguardActivity : BaseServerActivity() {

    override val serverConfigType: EConfigType = EConfigType.WIREGUARD

    @Composable
    override fun ScreenContent() {
        val scope = rememberCoroutineScope()
        val uiState = rememberSaveable(saver = ServerUiState.Saver) {
            ServerUiState.from(
                initialConfig = initialConfig
            )
        }.apply {
            configType = EConfigType.WIREGUARD
        }

        ServerEditorScaffold(
            title = serverConfigType.toString(),
            onSaveClick = { saveServer(uiState) }
        ) {
            CommonBasicFields(uiState)
            WireguardProtocolFields(uiState)

        }
    }

    @Composable
    private fun WireguardProtocolFields(state: ServerUiState) {
        EditorSection(title = stringResource(R.string.server_section_credentials)) {
            EditorTextField(
                label = stringResource(R.string.server_lab_secret_key),
                value = state.secretKey,
                onValueChange = { state.secretKey = it }
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_public_key),
                value = state.publicKey,
                onValueChange = { state.publicKey = it }
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_preshared_key),
                value = state.preSharedKey,
                onValueChange = { state.preSharedKey = it }
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_reserved),
                value = state.reserved,
                onValueChange = { state.reserved = it }
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_local_address),
                value = state.localAddress,
                onValueChange = { state.localAddress = it }
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_local_mtu),
                value = state.mtu,
                onValueChange = { state.mtu = it },
                keyboardType = KeyboardType.Number
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_final_mask),
                value = state.finalMask,
                onValueChange = { state.finalMask = it },
                placeholder = "{\"outbound\":\"...\"}",
                maxLines = 3
            )
        }
    }
}
