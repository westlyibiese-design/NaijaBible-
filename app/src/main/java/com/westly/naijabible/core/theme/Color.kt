package com.westly.naijabible.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Every colour of the app lives in this one file.
 * Values were sampled from the design screenshots (Light Theme / Dark Theme folders).
 */
@Immutable
class NaijaColors(
    val isDark: Boolean,
    // Page and cards
    val pageBackground: Color,
    val card: Color,
    val cardBorder: Color,
    // Green header
    val headerGreen: Color,
    val headerPatternAlpha: Float,
    val onHeader: Color,
    val headerTagline: Color,
    // Logo (the bubble stays white in both themes)
    val logoBubble: Color,
    val logoBookGold: Color,
    val logoBookPage: Color,
    val logoRibbon: Color,
    // Accents
    val gold: Color,
    val goldText: Color,
    val cream: Color,
    // Text
    val bookTitle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textHint: Color,
    val divider: Color,
    // Search bar
    val searchBar: Color,
    val searchIcon: Color,
    // Reading (used from Phase 4)
    val verseHighlight: Color,
    val verseHighlightBorder: Color,
    val verseNumber: Color,
    val bookmarkGold: Color,
    // Chapter buttons (used from Phase 3)
    val chapterChip: Color,
    val chapterChipText: Color,
    val chapterChipSelected: Color,
    val chapterChipSelectedText: Color,
    // Previous / Next buttons (used from Phase 4)
    val buttonNext: Color,
    val buttonNextText: Color,
    val buttonPreviousBorder: Color,
    val buttonPreviousText: Color,
    // Bottom bar
    val navBar: Color,
    val navBarBorder: Color,
    val navInactive: Color
)

val LightNaijaColors = NaijaColors(
    isDark = false,
    pageBackground = Color(0xFFFCF9F3),
    card = Color(0xFFFFFFFF),
    cardBorder = Color.Transparent,
    headerGreen = Color(0xFF036530),
    headerPatternAlpha = 0.07f,
    onHeader = Color(0xFFFFFFFF),
    headerTagline = Color(0xFFF6E7B0),
    logoBubble = Color(0xFFFFFFFF),
    logoBookGold = Color(0xFFD9A01A),
    logoBookPage = Color(0xFFF5EFE0),
    logoRibbon = Color(0xFFB9820B),
    gold = Color(0xFFD59B26),
    goldText = Color(0xFFC98F0F),
    cream = Color(0xFFF6E7B0),
    bookTitle = Color(0xFF00401A),
    textPrimary = Color(0xFF141414),
    textSecondary = Color(0xFF6F6F70),
    textHint = Color(0xFF757575),
    divider = Color(0xFFE9E7E1),
    searchBar = Color(0xFFFFFFFF),
    searchIcon = Color(0xFF0A6B3A),
    verseHighlight = Color(0xFFFCF0DA),
    verseHighlightBorder = Color.Transparent,
    verseNumber = Color(0xFFC98F0F),
    bookmarkGold = Color(0xFFD59B26),
    chapterChip = Color(0xFFFFFFFF),
    chapterChipText = Color(0xFF00401A),
    chapterChipSelected = Color(0xFFD59B26),
    chapterChipSelectedText = Color(0xFFFFFFFF),
    buttonNext = Color(0xFF045D2E),
    buttonNextText = Color(0xFFFFFFFF),
    buttonPreviousBorder = Color(0xFFD8D4CA),
    buttonPreviousText = Color(0xFF444340),
    navBar = Color(0xFFFFFFFF),
    navBarBorder = Color(0xFFEEEBE4),
    navInactive = Color(0xFF6E6E6F)
)

val DarkNaijaColors = NaijaColors(
    isDark = true,
    pageBackground = Color(0xFF0F1217),
    card = Color(0xFF171B21),
    cardBorder = Color(0xFF252B33),
    headerGreen = Color(0xFF054A25),
    headerPatternAlpha = 0.07f,
    onHeader = Color(0xFFFFFFFF),
    headerTagline = Color(0xFFF6E7B0),
    logoBubble = Color(0xFFFFFFFF),
    logoBookGold = Color(0xFFD9A01A),
    logoBookPage = Color(0xFFF5EFE0),
    logoRibbon = Color(0xFFB9820B),
    gold = Color(0xFFD89A1F),
    goldText = Color(0xFFF0B323),
    cream = Color(0xFFF6E7B0),
    // Sampled dark greens (#027F37 to #159152) are below 4.5:1 on the dark cards,
    // so the brighter green from the dark splash is used for all green text.
    bookTitle = Color(0xFF0FA24D),
    textPrimary = Color(0xFFEAECEF),
    textSecondary = Color(0xFFB1B3B8),
    textHint = Color(0xFFACACAF),
    divider = Color(0xFF262B33),
    searchBar = Color(0xFF191C23),
    searchIcon = Color(0xFF0FA24D),
    verseHighlight = Color(0xFF171A1F),
    verseHighlightBorder = Color(0xFF262B33),
    verseNumber = Color(0xFFF0B323),
    bookmarkGold = Color(0xFFD89A1F),
    chapterChip = Color(0xFF191E24),
    chapterChipText = Color(0xFF0FA24D),
    chapterChipSelected = Color(0xFFC98B0A),
    chapterChipSelectedText = Color(0xFF0F1217),
    buttonNext = Color(0xFF0A4926),
    buttonNextText = Color(0xFFFFFFFF),
    buttonPreviousBorder = Color(0xFF2A3038),
    buttonPreviousText = Color(0xFFC5C8D1),
    navBar = Color(0xFF161B21),
    navBarBorder = Color(0xFF1F252C),
    navInactive = Color(0xFFABAAAD)
)

val LocalNaijaColors = staticCompositionLocalOf { LightNaijaColors }
