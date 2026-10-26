package com.westly.naijabible.feature.reading

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.westly.naijabible.R
import com.westly.naijabible.core.data.BibleIndex
import com.westly.naijabible.core.data.BibleRepository
import com.westly.naijabible.core.data.BookCatalog
import com.westly.naijabible.core.data.ChapterText
import com.westly.naijabible.core.data.Verse
import com.westly.naijabible.core.data.nextChapter
import com.westly.naijabible.core.data.previousChapter
import com.westly.naijabible.core.prefs.BookmarkKey
import com.westly.naijabible.core.theme.LocalReadingTextScale
import com.westly.naijabible.core.theme.NaijaTheme
import com.westly.naijabible.core.ui.BookmarkIcon
import com.westly.naijabible.core.ui.NaijaReadingHeader
import com.westly.naijabible.core.ui.TextSizeControl

/**
 * Reading screen: slim header, the chapter in one flowing column, Previous / Next at the bottom.
 * The chapter on screen is kept here (and saved), so Next and Previous change it without
 * opening a new screen. [onBack] is told which book is on screen now.
 */
@Composable
fun ReadingScreen(
    initialBook: Int,
    initialChapter: Int,
    index: BibleIndex,
    repository: BibleRepository,
    bookmarks: Set<BookmarkKey>,
    textSizeStep: Int,
    onTextSizeStepChange: (Int) -> Unit,
    onToggleBookmark: (BookmarkKey) -> Unit,
    onChapterShown: (bookNumber: Int, chapter: Int) -> Unit,
    onBack: (currentBook: Int) -> Unit,
    onOpenBookmarks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    val haptic = LocalHapticFeedback.current

    var book by rememberSaveable { mutableStateOf(initialBook) }
    var chapter by rememberSaveable { mutableStateOf(initialChapter) }
    var showSizeSheet by rememberSaveable { mutableStateOf(false) }

    var loaded by remember { mutableStateOf<ChapterText?>(null) }
    var failed by remember { mutableStateOf(false) }

    // Loads the chapter on a background thread (the repository does that). A new chapter cancels the old load.
    LaunchedEffect(book, chapter) {
        loaded = null
        failed = false
        val result = repository.loadChapter(book, chapter)
        if (result == null) {
            failed = true
        } else {
            loaded = result
            onChapterShown(book, chapter)
        }
    }

    val next = remember(index, book, chapter) { index.nextChapter(book, chapter) }
    val previous = remember(index, book, chapter) { index.previousChapter(book, chapter) }

    // One scroll state per chapter: a new chapter starts at the top, a rotation keeps the position.
    val listState = rememberSaveable(book, chapter, saver = LazyListState.Saver) { LazyListState() }

    val bookmarkedVerses = remember(bookmarks, book, chapter) {
        bookmarks.filter { it.bookNumber == book && it.chapter == chapter }.map { it.verse }.toSet()
    }

    val bookName = BookCatalog.byNumber(book)?.name.orEmpty()

    BackHandler { onBack(book) }

    Column(
        modifier
            .fillMaxSize()
            .background(colors.pageBackground)
    ) {
        NaijaReadingHeader(
            title = stringResource(R.string.reading_title, bookName, chapter),
            onBack = { onBack(book) },
            onTextSizeClick = { showSizeSheet = true },
            onBookmarksClick = onOpenBookmarks
        )

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val text = loaded
            when {
                text != null && text.bookNumber == book && text.chapter == chapter -> {
                    ChapterContent(
                        text = text,
                        listState = listState,
                        bookmarkedVerses = bookmarkedVerses,
                        onVerseClick = { verse ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onToggleBookmark(BookmarkKey(book, chapter, verse))
                        }
                    )
                }
                failed -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.reading_unavailable),
                            color = colors.textSecondary,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        ReadingBottomBar(
            hasPrevious = previous != null,
            hasNext = next != null,
            onPrevious = {
                previous?.let {
                    book = it.bookNumber
                    chapter = it.chapter
                }
            },
            onNext = {
                next?.let {
                    book = it.bookNumber
                    chapter = it.chapter
                }
            }
        )
    }

    if (showSizeSheet) {
        TextSizeSheet(
            step = textSizeStep,
            onStepChange = onTextSizeStepChange,
            onDismiss = { showSizeSheet = false }
        )
    }
}

@Composable
private fun ChapterContent(
    text: ChapterText,
    listState: LazyListState,
    bookmarkedVerses: Set<Int>,
    onVerseClick: (Int) -> Unit
) {
    val colors = NaijaTheme.colors
    val scale = LocalReadingTextScale.current

    // A lazy list, so even Psalm 119 (176 verses) scrolls smoothly.
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item {
            Column(Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text(
                    text = stringResource(R.string.reading_chapter_title, text.chapter),
                    color = colors.bookTitle,
                    fontSize = (36f * scale).sp,
                    lineHeight = (44f * scale).sp,
                    fontWeight = FontWeight.ExtraBold
                )
                if (text.title != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = text.title,
                        color = colors.textSecondary,
                        fontSize = (16f * scale).sp,
                        lineHeight = (24f * scale).sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        items(text.verses.size) { position ->
            val verse = text.verses[position]
            VerseRow(
                verse = verse,
                bookmarked = verse.number in bookmarkedVerses,
                scale = scale,
                onClick = { onVerseClick(verse.number) }
            )
        }
    }
}

@Composable
private fun VerseRow(
    verse: Verse,
    bookmarked: Boolean,
    scale: Float,
    onClick: () -> Unit
) {
    val colors = NaijaTheme.colors
    val shape = RoundedCornerShape(16.dp)
    val density = LocalDensity.current
    // The number column grows with the text size, so the hanging indent stays tidy at every size.
    val numberWidth = with(density) { (36f * scale).sp.toDp() }
    val toggleLabel = stringResource(R.string.cd_toggle_bookmark)
    val stateText = stringResource(
        if (bookmarked) R.string.reading_bookmarked else R.string.reading_not_bookmarked
    )

    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (bookmarked) colors.verseHighlight else Color.Transparent, shape)
            .then(
                if (bookmarked && colors.isDark) {
                    Modifier.border(1.dp, colors.verseHighlightBorder, shape)
                } else {
                    Modifier
                }
            )
            .clickable(onClickLabel = toggleLabel, onClick = onClick)
            .semantics(mergeDescendants = true) { stateDescription = stateText }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier.weight(1f),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = verse.number.toString(),
                color = colors.verseNumber,
                fontSize = (19f * scale).sp,
                lineHeight = (34f * scale).sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(numberWidth)
            )
            Text(
                text = verse.text,
                color = colors.textPrimary,
                fontSize = (20f * scale).sp,
                lineHeight = (34f * scale).sp,
                modifier = Modifier.weight(1f)
            )
        }
        // The space for the icon is always kept, so the text does not jump when a verse is bookmarked.
        Box(Modifier.width(34.dp), contentAlignment = Alignment.CenterEnd) {
            if (bookmarked) {
                BookmarkIcon(filled = true, tint = colors.bookmarkGold, size = 26.dp)
            }
        }
    }
}

@Composable
private fun ReadingBottomBar(
    hasPrevious: Boolean,
    hasNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val colors = NaijaTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .background(colors.pageBackground)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NavButton(
            label = stringResource(R.string.reading_previous),
            forward = false,
            filled = false,
            enabled = hasPrevious,
            onClick = onPrevious,
            modifier = Modifier.weight(1f)
        )
        NavButton(
            label = stringResource(R.string.reading_next),
            forward = true,
            filled = true,
            enabled = hasNext,
            onClick = onNext,
            modifier = Modifier.weight(1f)
        )
    }
}

/** Previous (outlined) and Next (filled green). A disabled button is outlined and grey. */
@Composable
private fun NavButton(
    label: String,
    forward: Boolean,
    filled: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    val shape = RoundedCornerShape(28.dp)
    val solid = filled && enabled
    val contentColor = when {
        !enabled -> colors.textHint
        filled -> colors.buttonNextText
        else -> colors.buttonPreviousText
    }
    val arrowColor = when {
        !enabled -> colors.textHint
        colors.isDark -> colors.gold
        else -> contentColor
    }

    Row(
        modifier
            .height(56.dp)
            .clip(shape)
            .background(if (solid) colors.buttonNext else Color.Transparent, shape)
            .then(
                if (solid) Modifier else Modifier.border(1.dp, colors.buttonPreviousBorder, shape)
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!forward) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = arrowColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = label,
            color = contentColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        if (forward) {
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = arrowColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/** Compact bottom sheet "Text size" with the slider. No sample verse. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextSizeSheet(
    step: Int,
    onStepChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = NaijaTheme.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.card,
        contentColor = colors.textPrimary
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, bottom = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.text_size_title),
                color = colors.textPrimary,
                fontSize = 20.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(12.dp))
            TextSizeControl(step = step, onStepChange = onStepChange)
        }
    }
}
