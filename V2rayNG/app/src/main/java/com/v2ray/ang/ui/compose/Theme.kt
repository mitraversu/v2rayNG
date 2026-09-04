package com.v2ray.ang.ui.compose

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.v2ray.ang.AppConfig
import com.v2ray.ang.handler.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val LightColor = lightColorScheme(
    primary = Color(0xFF111314), // Soft black — minimal, not pure #000
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE8EAED), // Cool gray
    onPrimaryContainer = Color(0xFF111314),
    secondary = Color(0xFF8A4D14), // Muted warm amber — less neon than #f97910
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFEFE0), // Very soft peach
    onSecondaryContainer = Color(0xFF2E1500),
    tertiary = Color(0xFF006B5E), // Muted teal
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF9CF2E1),
    onTertiaryContainer = Color(0xFF00201C),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFCFCFC), // Off-white — softer than pure white
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFCFCFC),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE1E3E5),
    onSurfaceVariant = Color(0xFF444749),
    outline = Color(0xFF777779),
    outlineVariant = Color(0xFFC6C6CD),
    inverseSurface = Color(0xFF2E3133),
    inverseOnSurface = Color(0xFFF0F0F3),
    inversePrimary = Color(0xFFC6C6CD),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFF111314),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF6F7F8), // Subtle depth
    surfaceContainer = Color(0xFFEFF0F1),
    surfaceContainerHigh = Color(0xFFE8EAED),
    surfaceContainerHighest = Color(0xFFE1E3E5),
)

private val DarkColor = darkColorScheme(
    primary = Color(0xFFE1E3E5), // Soft off-white
    onPrimary = Color(0xFF1A1C1E),
    primaryContainer = Color(0xFF3A4443),
    onPrimaryContainer = Color(0xFFD9E3E1),
    secondary = Color(0xFFFFB77A), // Soft peach — muted
    onSecondary = Color(0xFF4A2800),
    secondaryContainer = Color(0xFF6B3B00),
    onSecondaryContainer = Color(0xFFFFDCC1),
    tertiary = Color(0xFF7ED9C8),
    onTertiary = Color(0xFF00382E),
    tertiaryContainer = Color(0xFF005143),
    onTertiaryContainer = Color(0xFF9CF2E1),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0F1113), // True dark — minimal
    onBackground = Color(0xFFE1E3E5),
    surface = Color(0xFF0F1113),
    onSurface = Color(0xFFE1E3E5),
    surfaceVariant = Color(0xFF444749),
    onSurfaceVariant = Color(0xFFC6C6CD),
    outline = Color(0xFF8F9193),
    outlineVariant = Color(0xFF444749),
    inverseSurface = Color(0xFFE1E3E5),
    inverseOnSurface = Color(0xFF1A1C1E),
    inversePrimary = Color(0xFF4F5759),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFE1E3E5),
    surfaceContainerLowest = Color(0xFF0B0E10),
    surfaceContainerLow = Color(0xFF15181A),
    surfaceContainer = Color(0xFF1D2022),
    surfaceContainerHigh = Color(0xFF272A2C),
    surfaceContainerHighest = Color(0xFF323537),
)

// Semantic Colors — minimal, muted
val colorPing = Color(0xFF0F7A5F) // Muted teal-green — calmer than #009966
val colorPingRed = Color(0xFFBA1A1A) // Material error red — less neon than #FF0099
val colorConfigType = Color(0xFF8A4D14) // Muted amber — aligns with new secondary
val colorFabActive = Color(0xFF111314) // Minimal: dark on light, light on dark (resolved in UI)
val colorFabInactiveLight = Color(0xFFE1E3E5) // Soft container
val colorFabInactiveDark = Color(0xFF2E3133) // Soft dark container
val dividerColorLight = Color(0xFFE1E3E5) // Outline variant — 40% opacity feel
val dividerColorDark = Color(0xFF2E3133) // Subtle dark divider

// Toast Colors — minimal, 88% opacity for better legibility
val toastNormalBgLight = Color(0xE01A1C1E) // Soft black
val toastNormalBgDark = Color(0xE02E3133) // Soft charcoal
val toastSuccessBg = Color(0xE00F7A5F) // Muted teal
val toastErrorBg = Color(0xE0BA1A1A) // Muted red
val toastInfoBg = Color(0xE0444749) // Neutral
val toastIconCircleBg = Color(0x1AFFFFFF) // Very subtle
val toastTextColor = Color.White

object ThemeManager {
    private val _themeMode = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
    )
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(
        MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, true)
    )
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    fun setThemeMode(mode: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_UI_MODE_NIGHT, mode)
        _themeMode.value = mode
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        MmkvManager.encodeSettings(AppConfig.PREF_DYNAMIC_COLOR, enabled)
        _dynamicColorEnabled.value = enabled
    }

    fun refresh() {
        _themeMode.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
        _dynamicColorEnabled.value =
            MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, true)
    }
}

@Composable
fun resolveDarkTheme(): Boolean {
    val mode by ThemeManager.themeMode.collectAsState()
    return when (mode) {
        "1" -> false
        "2" -> true
        else -> isSystemInDarkTheme()
    }
}

val LocalDarkTheme = compositionLocalOf { false }

@Composable
fun AppTheme(
    darkTheme: Boolean = resolveDarkTheme(),
    content: @Composable () -> Unit
) {
    val dynamicColor by ThemeManager.dynamicColorEnabled.collectAsState()
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColor
        else -> LightColor
    }
    val snackbarController = rememberAppSnackbarController()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalAppSnackbar provides snackbarController
    ) {
        MaterialTheme(
            colorScheme = colorScheme
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppSnackbarBridge(controller = snackbarController)
                content()
                AppSnackbarHost(hostState = snackbarController.hostState)
            }
        }
    }
}
