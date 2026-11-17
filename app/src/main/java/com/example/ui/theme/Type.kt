package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val NotoSansKRFontFamily = FontFamily(
  Font(R.font.noto_sans_kr, FontWeight.Normal),
  Font(R.font.noto_sans_kr, FontWeight.Medium),
  Font(R.font.noto_sans_kr, FontWeight.SemiBold),
  Font(R.font.noto_sans_kr, FontWeight.Bold),
)

// All typography styles are >= 22sp for effortless readability by seniors (60+)
val SeniorTypography =
  Typography(
    displayLarge =
      TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.5).sp,
      ),
    headlineLarge =
      TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
      ),
    headlineMedium =
      TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 30.sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
      ),
    labelMedium =
      TextStyle(
        fontFamily = NotoSansKRFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
      ),
  )
