package app.raihanlazuardi.whatif.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.raihanlazuardi.whatif.R

/**
 * Universal Variable Font Family: Google Sans Flex
 * Supports full continuous variable weight axis (wght 100 - 1000).
 * Explicit FontVariation.Settings ensure each weight is dynamically interpolated by the text engine.
 */
@OptIn(ExperimentalTextApi::class)
val GoogleSansFlexFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(FontVariation.weight(300))
    ),
    Font(
        resId = R.font.google_sans_flex,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        resId = R.font.google_sans_flex,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        resId = R.font.google_sans_flex,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        resId = R.font.google_sans_flex,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(750))
    ),
    Font(
        resId = R.font.google_sans_flex,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(850))
    ),
    Font(
        resId = R.font.google_sans_flex,
        weight = FontWeight.Black,
        variationSettings = FontVariation.Settings(FontVariation.weight(950))
    )
)

val GoogleSans = GoogleSansFlexFontFamily

val Typography = Typography(
    // Display: Google Sans Flex Variable (Bold / Expressive)
    displayLarge = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 54.sp,
        lineHeight = 62.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 42.sp
    ),

    // Headlines: Google Sans Flex Variable
    headlineLarge = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp
    ),

    // Titles: Google Sans Flex Variable
    titleLarge = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body: Google Sans Flex Variable
    bodyLarge = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Labels: Google Sans Flex Variable
    labelLarge = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
