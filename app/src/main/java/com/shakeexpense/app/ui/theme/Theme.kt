package com.shakeexpense.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    background = Background,
    surface = Surface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Error
)

@Composable
fun ShakeExpenseTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}
