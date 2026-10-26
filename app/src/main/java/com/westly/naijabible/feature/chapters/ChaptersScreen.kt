package com.westly.naijabible.feature.chapters

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.westly.naijabible.R
import com.westly.naijabible.core.data.BibleIndex
import com.westly.naijabible.core.data.BookInfo
import com.westly.naijabible.core.data.Testament
import com.westly.naijabible.core.theme.NaijaTheme
import com.westly.naijabible.core.ui.NaijaBackHeader

/**
 * Chapter picker: back header, the book name, "Old Testament · 50 chapters", and a grid of
 * 5 columns with ALL chapters of the book. Chapters without text are greyed and cannot be opened.
 * The chapter opened last in this book ([lastReadChapter]) has the gold fill.
 */
@Composable
fun ChaptersScreen(
    book: BookInfo,
    index: BibleIndex,
    lastReadChapter: Int?,
    onBack: () -> Unit,
    onOpenChapter: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    val gridState = rememberLazyGridState()
    // Width of one chapter button (portrait only): the screen minus the side padding and the 4 gaps, over 5.
    val chipWidth = ((LocalConfiguration.current.screenWidthDp - 40 - 4 * 12) / 5f).dp

    Column(
        modifier
            .fillMaxSize()
            .background(colors.pageBackground)
    ) {
        NaijaBackHeader(onBack = onBack)

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ChaptersTitleBlock(book)
            }
            items((1..book.chapterCount).toList()) { chapter ->
                ChapterChip(
                    chapter = chapter,
                    available = index.isChapterAvailable(book.number, chapter),
                    lastRead = chapter == lastReadChapter,
                    chipWidth = chipWidth,
                    onClick = { onOpenChapter(chapter) }
                )
            }
        }
    }
}

@Composable
private fun ChaptersTitleBlock(book: BookInfo) {
    val colors = NaijaTheme.colors
    val testament = stringResource(
        if (book.testament == Testament.OT) R.string.testament_old else R.string.testament_new
    )
    val chapters = if (book.chapterCount == 1) {
        stringResource(R.string.books_chapters_one)
    } else {
        stringResource(R.string.books_chapters_many, book.chapterCount)
    }
    Column(Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 4.dp)) {
        Text(
            text = book.name,
            color = colors.bookTitle,
            fontSize = 40.sp,
            lineHeight = 46.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.chapters_subtitle, testament, chapters),
            color = colors.textSecondary,
            fontSize = 17.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.chapters_choose),
            color = colors.textSecondary,
            fontSize = 17.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun ChapterChip(
    chapter: Int,
    available: Boolean,
    lastRead: Boolean,
    chipWidth: Dp,
    onClick: () -> Unit
) {
    val colors = NaijaTheme.colors
    val shape = RoundedCornerShape(16.dp)
    // The number follows the app text size, but never grows wider than the button
    // (Psalms have 3-digit chapters, and the phone's font size can be large too).
    val density = LocalDensity.current
    val wanted = with(density) { 22.sp.toDp() }
    val widest = (chipWidth - 8.dp) / (chapter.toString().length * 0.66f)
    val numberSize = with(density) { (if (wanted < widest) wanted else widest).toSp() }
    val description = if (available) {
        stringResource(R.string.cd_chapter, chapter)
    } else {
        stringResource(R.string.cd_chapter_unavailable, chapter)
    }

    val container = when {
        !available -> colors.divider.copy(alpha = 0.55f)
        lastRead -> colors.chapterChipSelected
        else -> colors.chapterChip
    }
    val textColor = when {
        !available -> colors.textHint
        lastRead -> colors.chapterChipSelectedText
        else -> colors.chapterChipText
    }

    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .then(
                if (available && !colors.isDark && !lastRead) {
                    Modifier.shadow(
                        elevation = 2.dp,
                        shape = shape,
                        ambientColor = Color(0x22000000),
                        spotColor = Color(0x22000000)
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(container, shape)
            .then(
                if (available && colors.isDark && !lastRead) {
                    Modifier.border(1.dp, colors.cardBorder, shape)
                } else {
                    Modifier
                }
            )
            .then(
                if (available) {
                    Modifier.clickable(role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                }
            )
            .semantics {
                contentDescription = description
                if (!available) disabled()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = chapter.toString(),
            color = textColor,
            fontSize = numberSize,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.clearAndSetSemantics { }
        )
    }
}
