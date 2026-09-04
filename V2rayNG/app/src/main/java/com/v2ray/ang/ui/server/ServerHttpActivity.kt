package com.v2ray.ang.ui.server

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.v2ray.ang.R
import com.v2ray.ang.enums.EConfigType

class ServerHttpActivity : BaseServerActivity() {

    override val serverConfigType: EConfigType = EConfigType.HTTP

    @Composable
    override fun ScreenContent() {
        val scope = rememberCoroutineScope()
        val uiState = rememberSaveable(saver = ServerUiState.Saver) {
            ServerUiState.from(
                initialConfig = initialConfig
            )
        }.apply {
            configType = serverConfigType
        }

        ServerEditorScaffold(
            title = serverConfigType.toString(),
            onSaveClick = { saveServer(uiState) }
        ) {
            CommonBasicFields(uiState)
            HttpProtocolFields(uiState)

        }
    }

    @Composable
    private fun HttpProtocolFields(state: ServerUiState) {
        EditorSection(title = stringResource(R.string.server_section_credentials)) {
            EditorTextField(
                label = stringResource(R.string.server_lab_security4),
                value = state.username,
                onValueChange = { state.username = it }
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_id4),
                value = state.password,
                onValueChange = { state.password = it }
            )
        }
    }
}
