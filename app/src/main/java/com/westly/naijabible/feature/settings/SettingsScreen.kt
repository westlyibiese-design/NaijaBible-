package com.westly.naijabible.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.westly.naijabible.core.theme.NaijaTheme
import com.westly.naijabible.core.ui.NaijaMainHeader

/** Settings tab. Phase 2: header only (the settings list arrives in Phase 5). */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val colors = NaijaTheme.colors
    Column(
        modifier
            .fillMaxSize()
            .background(colors.pageBackground)
    ) {
        NaijaMainHeader()
    }
}
