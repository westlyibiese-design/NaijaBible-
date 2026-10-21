@file:OptIn(ExperimentalTextApi::class)

package com.westly.naijabible.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.westly.naijabible.R

/**
 * Nunito is one variable font file (res/font/nunito.ttf).
 * Each weight below is the same file with a different weight setting.
 */
private fun nunito(weight: FontWeight): Font = Font(
    resId = R.font.nunito,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

val NunitoFamily: FontFamily = FontFamily(
    nunito(FontWeight.Normal),
    nunito(FontWeight.SemiBold),
    nunito(FontWeight.Bold),
    nunito(FontWeight.ExtraBold)
)

private val base = Typography()

val NaijaTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = NunitoFamily),
    displayMedium = base.displayMedium.copy(fontFamily = NunitoFamily),
    displaySmall = base.displaySmall.copy(fontFamily = NunitoFamily),
    headlineLarge = base.headlineLarge.copy(fontFamily = NunitoFamily),
    headlineMedium = base.headlineMedium.copy(fontFamily = NunitoFamily),
    headlineSmall = base.headlineSmall.copy(fontFamily = NunitoFamily),
    titleLarge = base.titleLarge.copy(fontFamily = NunitoFamily),
    titleMedium = base.titleMedium.copy(fontFamily = NunitoFamily),
    titleSmall = base.titleSmall.copy(fontFamily = NunitoFamily),
    bodyLarge = base.bodyLarge.copy(fontFamily = NunitoFamily),
    bodyMedium = base.bodyMedium.copy(fontFamily = NunitoFamily),
    bodySmall = base.bodySmall.copy(fontFamily = NunitoFamily),
    labelLarge = base.labelLarge.copy(fontFamily = NunitoFamily),
    labelMedium = base.labelMedium.copy(fontFamily = NunitoFamily),
    labelSmall = base.labelSmall.copy(fontFamily = NunitoFamily)
)
