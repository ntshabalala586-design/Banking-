package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = ProfessionalPrimary,
    onPrimary = Color.White,
    primaryContainer = ProfessionalPrimaryContainer,
    onPrimaryContainer = ProfessionalOnPrimaryContainer,
    secondary = Color(0xFF004A77),
    onSecondary = Color.White,
    tertiary = Color(0xFF535F70),
    background = ProfessionalBackground,
    onBackground = TextPrimaryDark,
    surface = ProfessionalSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = ProfessionalSurfaceVariant,
    onSurfaceVariant = TextSecondaryMuted,
    outline = ProfessionalBorder,
    error = PolishExpenseRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9ECEFF),
    onPrimary = Color(0xFF003355),
    primaryContainer = Color(0xFF004A77),
    onPrimaryContainer = Color(0xFFD3E4FF),
    secondary = Color(0xFF9ECEFF),
    onSecondary = Color(0xFF003355),
    tertiary = Color(0xFFBCC7DB),
    background = Color(0xFF101318),
    onBackground = Color(0xFFE1E2E8),
    surface = Color(0xFF191C22),
    onSurface = Color(0xFFE1E2E8),
    surfaceVariant = Color(0xFF2C3038),
    onSurfaceVariant = Color(0xFFC1C7CE),
    outline = Color(0xFF41484D),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun FinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    FinanceTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
