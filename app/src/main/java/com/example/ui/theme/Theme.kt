package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AgriForestGreen,
    onPrimary = Color.White,
    primaryContainer = AgriLightMint,
    onPrimaryContainer = AgriDeepGreen,
    secondary = AgriFreshGreen,
    onSecondary = Color.White,
    secondaryContainer = AgriContainerMint,
    onSecondaryContainer = AgriDarkText,
    tertiary = AgriWaterTeal,
    onTertiary = Color.White,
    background = AgriMintWhite,
    onBackground = AgriDarkText,
    surface = AgriCardBackground,
    onSurface = AgriDarkText,
    surfaceVariant = AgriLightMint,
    onSurfaceVariant = AgriMutedText,
    outline = AgriBorder,
    outlineVariant = AgriCardBorder,
    error = AgriDangerRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = AgriPrimaryDark,
    onPrimary = Color(0xFF032616),
    primaryContainer = AgriDeepGreen,
    onPrimaryContainer = Color(0xFFB8F5D6),
    secondary = AgriFreshGreen,
    onSecondary = Color.White,
    background = AgriBackgroundDark,
    onBackground = AgriTextDark,
    surface = AgriSurfaceDark,
    onSurface = AgriTextDark,
    surfaceVariant = AgriSurfaceVariantDark,
    onSurfaceVariant = AgriMutedDark,
    outline = Color(0xFF2E4E3C),
    error = AgriDangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep AgriScan distinctive branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
