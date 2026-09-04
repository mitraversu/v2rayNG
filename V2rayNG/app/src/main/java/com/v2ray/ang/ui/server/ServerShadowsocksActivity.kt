package com.v2ray.ang.ui.server

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.v2ray.ang.R
import com.v2ray.ang.enums.EConfigType

class ServerShadowsocksActivity : BaseServerActivity() {

    override val serverConfigType: EConfigType = EConfigType.SHADOWSOCKS

    @Composable
    override fun ScreenContent() {
        val options = rememberFieldOptions()
        val scope = rememberCoroutineScope()
        val uiState = rememberSaveable(saver = ServerUiState.Saver) {
            ServerUiState.from(
                initialConfig = initialConfig
            )
        }.apply {
            configType = EConfigType.SHADOWSOCKS
        }
        val securityOptions = stringArrayResource(R.array.ss_securitys).toList()

        ServerEditorScaffold(
            title = serverConfigType.toString(),
            onSaveClick = { saveServer(uiState) }
        ) {
            CommonBasicFields(uiState)
            ShadowsocksProtocolFields(uiState, securityOptions)
            CommonNetworkFields(uiState, options)
            CommonStreamSecurityFields(
                state = uiState,
                options = options,
                scope = scope,
                buildProfileItem = { uiState.toProfileItem(initialConfig) }
            )
        }
    }

    override fun validateProtocolConfig(config: com.v2ray.ang.dto.entities.ProfileItem): Boolean = true

    @Composable
    private fun ShadowsocksProtocolFields(
        state: ServerUiState,
        methodOptions: List<String>
    ) {
        EditorSection(title = stringResource(R.string.server_section_credentials)) {
            EditorTextField(
                label = stringResource(R.string.server_lab_id3),
                value = state.password,
                onValueChange = { state.password = it }
            )
            EditorDropdownField(
                label = stringResource(R.string.server_lab_security),
                value = state.method,
                options = methodOptions,
                onValueChange = { state.method = it }
            )
        }
    }
}
