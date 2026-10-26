package com.westly.naijabible.feature.bookmarks

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.westly.naijabible.R
import com.westly.naijabible.core.data.BibleRepository
import com.westly.naijabible.core.data.BookCatalog
import com.westly.naijabible.core.prefs.BookmarkKey
import com.westly.naijabible.core.theme.NaijaTheme
import com.westly.naijabible.core.ui.BookmarkIcon
import com.westly.naijabible.core.ui.NaijaMainHeader
import kotlinx.coroutines.launch

/**
 * Bookmarks tab: one card per saved verse, in Bible order (book, chapter, verse).
 * The verse text is read from the Bible text files; if it cannot be read, only the reference shows.
 * [onToggleBookmark] removes a bookmark that exists and puts back one that does not (used for Undo).
 */
@Composable
fun BookmarksScreen(
    bookmarks: Set<BookmarkKey>,
    repository: BibleRepository,
    onOpenBookmark: (BookmarkKey) -> Unit,
    onToggleBookmark: (BookmarkKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val removedMessage = stringResource(R.string.bookmark_removed)
    val undoLabel = stringResource(R.string.bookmark_undo)

    // Bible order. A saved verse of a book that is not in the catalog cannot be shown, so it is left out.
    val sorted = remember(bookmarks) {
        bookmarks
            .filter { BookCatalog.byNumber(it.bookNumber) != null }
            .sortedWith(compareBy({ it.bookNumber }, { it.chapter }, { it.verse }))
    }

    // Verse texts, loaded one by one on a background thread (the repository does that and keeps a cache).
    var texts by remember { mutableStateOf<Map<BookmarkKey, String>>(emptyMap()) }
    LaunchedEffect(sorted) {
        for (key in sorted) {
            if (texts.containsKey(key)) continue
            val line = repository.loadChapter(key.bookNumber, key.chapter)
                ?.verses
                ?.firstOrNull { it.number == key.verse }
                ?.text
            if (line != null) {
                texts = texts + (key to line)
            }
        }
    }

    fun removeWithUndo(key: BookmarkKey) {
        onToggleBookmark(key)
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = removedMessage,
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                onToggleBookmark(key)
            }
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .background(colors.pageBackground)
    ) {
        Column(Modifier.fillMaxSize()) {
            NaijaMainHeader()
            Text(
                text = stringResource(R.string.bookmarks_title),
                color = colors.bookTitle,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp)
            )

            if (sorted.isEmpty()) {
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
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = sorted,
                        key = { "${it.bookNumber}:${it.chapter}:${it.verse}" }
                    ) { key ->
                        val bookName = BookCatalog.byNumber(key.bookNumber)?.name.orEmpty()
                        val reference = "$bookName ${key.chapter}:${key.verse}"
                        BookmarkCard(
                            reference = reference,
                            verseText = texts[key],
                            onOpen = { onOpenBookmark(key) },
                            onRemove = { removeWithUndo(key) }
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = colors.snackbar,
                contentColor = colors.snackbarText,
                actionColor = colors.snackbarAction
            )
        }
    }
}

@Composable
private fun BookmarkCard(
    reference: String,
    verseText: String?,
    onOpen: () -> Unit,
    onRemove: () -> Unit
) {
    val colors = NaijaTheme.colors
    val shape = RoundedCornerShape(22.dp)
    val removeLabel = stringResource(R.string.cd_remove_bookmark, reference)

    Row(
        Modifier
            .fillMaxWidth()
            .then(
                if (!colors.isDark) {
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
            .background(colors.card, shape)
            .then(
                if (colors.isDark) Modifier.border(1.dp, colors.cardBorder, shape) else Modifier
            )
            .clickable(role = Role.Button, onClick = onOpen)
            .padding(start = 20.dp, top = 14.dp, bottom = 14.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = reference,
                color = colors.bookTitle,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (verseText != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = verseText,
                    color = colors.textPrimary,
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        IconButton(
            onClick = onRemove,
            modifier = Modifier.semantics { contentDescription = removeLabel }
        ) {
            BookmarkIcon(filled = true, tint = colors.bookmarkGold, size = 28.dp)
        }
    }
}
