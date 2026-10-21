package com.westly.naijabible.feature.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.westly.naijabible.R
import com.westly.naijabible.core.theme.NaijaTheme
import com.westly.naijabible.core.ui.NaijaMainHeader

/** Bookmarks tab. Phase 2: header, title and the empty message (saved verses arrive in Phase 5). */
@Composable
fun BookmarksScreen(modifier: Modifier = Modifier) {
    val colors = NaijaTheme.colors
    Column(
        modifier
            .fillMaxSize()
            .background(colors.pageBackground)
    ) {
        NaijaMainHeader()
        Text(
            text = stringResource(R.string.bookmarks_title),
            color = colors.bookTitle,
            fontSize = 34.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)
        )
        Box(
            Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.bookmarks_empty),
                color = colors.textSecondary,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
