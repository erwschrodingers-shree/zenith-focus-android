package com.zenith.focus.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Color Palette
val OffWhite = Color(0xFFF4F4F6)
val DarkSlate = Color(0xFF0F172A)
val SaffronOrange = Color(0xFFFF5500)
val SubtleGray = Color(0xFFE2E8F0)

val PureDark = Color(0xFF0A0A0C)
val SlateContainer = Color(0xFF16161E)
val GlowingOrange = Color(0xFFFF6B35)

// Light Theme
private val LightColorScheme = lightColorScheme(
    primary = SaffronOrange,
    onPrimary = Color.White,
    secondary = DarkSlate,
    onSecondary = OffWhite,
    tertiary = SubtleGray,
    background = OffWhite,
    onBackground = DarkSlate,
    surface = Color.White,
    onSurface = DarkSlate,
    surfaceVariant = SubtleGray,
    error = Color(0xFFE84C3D)
)

// Dark Theme
private val DarkColorScheme = darkColorScheme(
    primary = GlowingOrange,
    onPrimary = PureDark,
    secondary = Color.White,
    onSecondary = PureDark,
    tertiary = SlateContainer,
    background = PureDark,
    onBackground = Color.White,
    surface = SlateContainer,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2A36),
    error = Color(0xFFFF6B6B)
)

@Composable
fun ZenithTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZenithTypography,
        shapes = ZenithShapes,
        content = content
    )
}
