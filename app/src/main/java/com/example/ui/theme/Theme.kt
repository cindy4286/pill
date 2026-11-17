package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

private val SeniorIvoryColorScheme =
  lightColorScheme(
    primary = MedDoneGreen,
    onPrimary = Color.White,
    primaryContainer = MedDoneBadgeBg,
    onPrimaryContainer = MedDoneGreenDark,
    secondary = WaterPrimaryBlue,
    onSecondary = Color.White,
    secondaryContainer = WaterCardSurface,
    onSecondaryContainer = InkPrimary,
    background = IvoryBackground,
    onBackground = InkPrimary,
    surface = IvoryCardSurface,
    onSurface = InkPrimary,
    surfaceVariant = IvoryHeaderTint,
    onSurfaceVariant = InkSecondary,
    outline = IvoryCardBorder,
  )

private val SeniorShapes =
  Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
  )

@Composable
fun SeniorCareTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = SeniorIvoryColorScheme,
    typography = SeniorTypography,
    shapes = SeniorShapes,
    content = content,
  )
}
