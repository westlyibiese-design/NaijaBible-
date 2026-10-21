package com.westly.naijabible.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.westly.naijabible.R
import com.westly.naijabible.core.theme.NaijaTheme

/** Green header surface with the still pattern. It reaches up under the status bar. */
@Composable
private fun HeaderSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = NaijaTheme.colors
    Box(
        modifier
            .fillMaxWidth()
            .background(colors.headerGreen)
            .clipToBounds()
            .drawBehind {
                drawNaijaPattern(
                    originX = -70f * density,
                    originY = -40f * density,
                    unit = density,
                    color = Color.White.copy(alpha = colors.headerPatternAlpha)
                )
            }
    ) {
        content()
    }
}

/**
 * Main header: logo, "Naija Bible", the tagline and an optional settings gear.
 * [bottomContent] sits under the logo row (the Books screen puts the search bar there).
 */
@Composable
fun NaijaMainHeader(
    modifier: Modifier = Modifier,
    onSettingsClick: (() -> Unit)? = null,
    bottomContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    val colors = NaijaTheme.colors
    HeaderSurface(modifier) {
        Column(
            Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, end = 12.dp, top = 12.dp, bottom = 18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NaijaLogo(width = 52.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.app_title),
                        color = colors.onHeader,
                        fontSize = 28.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = stringResource(R.string.app_tagline),
                        color = colors.headerTagline,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (onSettingsClick != null) {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.cd_settings),
                            tint = colors.onHeader
                        )
                    }
                }
            }
            if (bottomContent != null) {
                Spacer(Modifier.height(16.dp))
                bottomContent()
            }
        }
    }
}

/** Header with a back arrow, a smaller logo and the app name (used on the chapters screen, Phase 3). */
@Composable
fun NaijaBackHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    HeaderSurface(modifier) {
        Row(
            Modifier
                .statusBarsPadding()
                .padding(start = 4.dp, end = 20.dp, top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = colors.onHeader
                )
            }
            NaijaLogo(width = 46.dp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(R.string.app_title),
                    color = colors.onHeader,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = stringResource(R.string.app_tagline),
                    color = colors.headerTagline,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/** Slim header for reading: back arrow, centred title (for example "Genesis 1"), "Aa" and a bookmark (Phase 4). */
@Composable
fun NaijaReadingHeader(
    title: String,
    onBack: () -> Unit,
    onTextSizeClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    HeaderSurface(modifier) {
        Box(
            Modifier
                .statusBarsPadding()
                .height(56.dp)
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = colors.onHeader
                )
            }
            Text(
                text = title,
                color = colors.onHeader,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 112.dp)
            )
            Row(
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onTextSizeClick) {
                    Text(
                        text = "Aa",
                        color = colors.onHeader,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onBookmarksClick) {
                    BookmarkIcon(filled = false, tint = colors.onHeader, size = 26.dp)
                }
            }
        }
    }
}
