package com.example.movie_app.presentation.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Dark color scheme for the app.
 */
private val DarkColorScheme = darkColorScheme(
    primary = MovieAppColors.Primary,
    secondary = MovieAppColors.Secondary,
    background = MovieAppColors.Background,
    surface = MovieAppColors.Surface,
    error = MovieAppColors.Error,
    onPrimary = MovieAppColors.OnPrimary,
    onSecondary = MovieAppColors.OnSecondary,
    onBackground = MovieAppColors.OnBackground,
    onSurface = MovieAppColors.OnSurface,
    onError = MovieAppColors.OnError
)

/**
 * Light color scheme for the app.
 */
private val LightColorScheme = lightColorScheme(
    primary = MovieAppColors.Primary,
    secondary = MovieAppColors.Secondary,
    background = Color.White,
    surface = Color.White,
    error = MovieAppColors.Error,
    onPrimary = MovieAppColors.OnPrimary,
    onSecondary = MovieAppColors.OnSecondary,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = MovieAppColors.OnError
)

/**
 * Movie App theme with Material Design 3 support.
 * Supports dynamic colors on Android 12+ for white label customization.
 */
@Composable
fun MovieAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

