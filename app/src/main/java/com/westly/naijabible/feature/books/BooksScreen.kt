package com.westly.naijabible.feature.books

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.westly.naijabible.R
import com.westly.naijabible.core.theme.NaijaTheme
import com.westly.naijabible.core.ui.NaijaMainHeader

/** Books tab. Phase 2: header and search bar only (the book list arrives in Phase 3). */
@Composable
fun BooksScreen(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    Column(
        modifier
            .fillMaxSize()
            .background(colors.pageBackground)
    ) {
        NaijaMainHeader(
            onSettingsClick = onOpenSettings,
            bottomContent = { SearchBar() }
        )
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier) {
    val colors = NaijaTheme.colors
    var query by rememberSaveable { mutableStateOf("") }
    Row(
        modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(colors.searchBar, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(R.string.cd_search),
            tint = colors.searchIcon
        )
        Spacer(Modifier.width(12.dp))
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (query.isEmpty()) {
                Text(
                    text = stringResource(R.string.books_search_hint),
                    color = colors.textHint,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                textStyle = TextStyle(
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                cursorBrush = SolidColor(colors.searchIcon),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
