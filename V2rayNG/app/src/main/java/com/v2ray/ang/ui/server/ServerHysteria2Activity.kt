package com.v2ray.ang.ui.server

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.dto.entities.ProfileItem
import com.v2ray.ang.enums.EConfigType
import com.v2ray.ang.extension.toast
import com.v2ray.ang.ui.compose.SettingsSwitchItem

class ServerHysteria2Activity : BaseServerActivity() {

    override val serverConfigType: EConfigType = EConfigType.HYSTERIA2

    @Composable
    override fun ScreenContent() {
        val scope = rememberCoroutineScope()
        val uiState = rememberSaveable(saver = ServerUiState.Saver) {
            ServerUiState.from(
                initialConfig = initialConfig
            )
        }.apply {
            configType = EConfigType.HYSTERIA2
        }

        ServerEditorScaffold(
            title = serverConfigType.toString(),
            onSaveClick = { saveServer(uiState) }
        ) {
            CommonBasicFields(uiState)
            Hysteria2ProtocolFields(uiState)

        }
    }

    override fun validateProtocolConfig(config: ProfileItem): Boolean {
        if (config.password.isNullOrBlank()) {
            toast(R.string.server_lab_id3)
            return false
        }
        if (config.security.isNullOrBlank()) {
            config.security = AppConfig.TLS
        }
        return true
    }

    @Composable
    private fun Hysteria2ProtocolFields(state: ServerUiState) {
        EditorSection(title = stringResource(R.string.server_section_credentials)) {
            EditorTextField(
                label = stringResource(R.string.server_lab_id3),
                value = state.password,
                onValueChange = { state.password = it }
            )
            EditorTextField(
                label = stringResource(R.string.server_obfs_password),
                value = state.obfsPassword,
                onValueChange = { state.obfsPassword = it }
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_port_hop),
                value = state.portHopping,
                onValueChange = { state.portHopping = it },
                placeholder = "e.g. 20000-30000"
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_port_hop_interval),
                value = state.portHoppingInterval,
                onValueChange = { state.portHoppingInterval = it },
                placeholder = "30s"
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_bandwidth_down),
                value = state.bandwidthDown,
                onValueChange = { state.bandwidthDown = it },
                placeholder = "0"
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_bandwidth_up),
                value = state.bandwidthUp,
                onValueChange = { state.bandwidthUp = it },
                placeholder = "0"
            )
        }
        EditorSection(title = stringResource(R.string.server_section_security)) {
            SettingsSwitchItem(
                title = stringResource(R.string.server_lab_allow_insecure),
                checked = state.allowInsecure,
                onCheckedChange = { state.allowInsecure = it }
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_sni),
                value = state.sni,
                onValueChange = { state.sni = it }
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_ech_config_list),
                value = state.echConfigList,
                onValueChange = { state.echConfigList = it }
            )
            EditorTextField(
                label = stringResource(R.string.server_lab_pinned_ca256),
                value = state.pinnedCA256,
                onValueChange = { state.pinnedCA256 = it }
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
