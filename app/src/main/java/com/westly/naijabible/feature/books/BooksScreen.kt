package com.westly.naijabible.feature.books

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.westly.naijabible.R
import com.westly.naijabible.core.data.BibleIndex
import com.westly.naijabible.core.data.BookCatalog
import com.westly.naijabible.core.data.BookInfo
import com.westly.naijabible.core.theme.NaijaTheme
import com.westly.naijabible.core.ui.NaijaMainHeader

private const val TAB_OLD = 0
private const val TAB_NEW = 1

/**
 * Books tab: header with the search bar, the Old / New Testament tabs and the list of the 66 books.
 * Books with no text file are greyed and say "Coming soon". A book with text opens its chapter picker.
 */
@Composable
fun BooksScreen(
    index: BibleIndex,
    onOpenSettings: () -> Unit,
    onOpenBook: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors

    // Saved, so the tab and the search text survive switching to another bottom tab and back.
    var query by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableStateOf(TAB_OLD) }

    // Separate list states: each list keeps its own scroll position.
    val oldState = rememberLazyListState()
    val newState = rememberLazyListState()
    val searchState = rememberLazyListState()

    val typed = query.trim()
    val searching = typed.isNotEmpty()
    val results = remember(typed) {
        if (typed.isEmpty()) emptyList<BookInfo>() else BookCatalog.search(typed)
    }

    // A new search starts at the top. The first run (also after coming back) changes nothing.
    var lastTyped by remember { mutableStateOf(typed) }
    LaunchedEffect(typed) {
        if (typed != lastTyped) {
            lastTyped = typed
            searchState.scrollToItem(0)
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(colors.pageBackground)
    ) {
        NaijaMainHeader(
            onSettingsClick = onOpenSettings,
            bottomContent = {
                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    // The header leaves 12 dp on the right and 20 dp on the left; even them out.
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        )

        if (!searching) {
            TestamentTabs(selected = selectedTab, onSelect = { selectedTab = it })
        }

        when {
            searching && results.isEmpty() -> EmptySearchMessage(typed)
            searching -> BookList(results, index, searchState, onOpenBook)
            selectedTab == TAB_OLD -> BookList(BookCatalog.oldTestament, index, oldState, onOpenBook)
            else -> BookList(BookCatalog.newTestament, index, newState, onOpenBook)
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    val focusManager = LocalFocusManager.current
    val clearLabel = stringResource(R.string.cd_clear_search)
    Row(
        modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(colors.searchBar, RoundedCornerShape(16.dp))
            .padding(start = 16.dp, end = if (query.isEmpty()) 16.dp else 6.dp),
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
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                cursorBrush = SolidColor(colors.searchIcon),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.semantics { contentDescription = clearLabel }
            ) {
                ClearIcon(tint = colors.textSecondary)
            }
        }
    }
}

@Composable
private fun TestamentTabs(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        TestamentTab(
            label = stringResource(R.string.books_tab_old),
            selected = selected == TAB_OLD,
            onClick = { onSelect(TAB_OLD) },
            modifier = Modifier.weight(1f)
        )
        TestamentTab(
            label = stringResource(R.string.books_tab_new),
            selected = selected == TAB_NEW,
            onClick = { onSelect(TAB_NEW) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TestamentTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    Column(
        modifier
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(top = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = if (selected) colors.bookTitle else colors.textSecondary,
            fontSize = 17.sp,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold
        )
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier
                .fillMaxWidth(0.72f)
                .height(3.dp)
                .background(
                    color = if (selected) colors.gold else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

@Composable
private fun BookList(
    books: List<BookInfo>,
    index: BibleIndex,
    listState: LazyListState,
    onOpenBook: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(books, key = { it.number }) { book ->
            BookCard(
                book = book,
                available = index.isBookAvailable(book.number),
                onClick = { onOpenBook(book.number) }
            )
        }
    }
}

@Composable
private fun BookCard(
    book: BookInfo,
    available: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    val shape = RoundedCornerShape(16.dp)
    val comingSoonDescription = stringResource(R.string.cd_book_coming_soon, book.name)

    val subtitle = when {
        !available -> stringResource(R.string.books_coming_soon)
        book.chapterCount == 1 -> stringResource(R.string.books_chapters_one)
        else -> stringResource(R.string.books_chapters_many, book.chapterCount)
    }

    Row(
        modifier
            .fillMaxWidth()
            .then(
                if (colors.isDark) {
                    Modifier
                } else {
                    Modifier.shadow(
                        elevation = 3.dp,
                        shape = shape,
                        ambientColor = Color(0x22000000),
                        spotColor = Color(0x22000000)
                    )
                }
            )
            .background(colors.card, shape)
            .then(
                if (colors.isDark) Modifier.border(1.dp, colors.cardBorder, shape) else Modifier
            )
            .then(
                if (available) {
                    Modifier
                        .clip(shape)
                        .clickable(role = Role.Button, onClick = onClick)
                } else {
                    Modifier.clearAndSetSemantics {
                        contentDescription = comingSoonDescription
                        disabled()
                    }
                }
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = book.name,
                color = if (available) colors.bookTitle else colors.textHint,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                color = if (available) colors.textSecondary else colors.textHint,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (available) {
            ChevronRight(tint = colors.gold)
        }
    }
}

@Composable
private fun EmptySearchMessage(typed: String, modifier: Modifier = Modifier) {
    val colors = NaijaTheme.colors
    Box(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = stringResource(R.string.books_search_empty, typed),
            color = colors.textSecondary,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

/** Gold chevron on the right of a book card. Drawn here so no extra icon library is needed. */
@Composable
private fun ChevronRight(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(24.dp)) {
        val w = size.width
        val path = Path().apply {
            moveTo(w * 0.36f, w * 0.18f)
            lineTo(w * 0.68f, w * 0.50f)
            lineTo(w * 0.36f, w * 0.82f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/** Small cross for the "clear search" button. */
@Composable
private fun ClearIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(20.dp)) {
        val w = size.width
        val stroke = 2.dp.toPx()
        drawLine(tint, Offset(w * 0.2f, w * 0.2f), Offset(w * 0.8f, w * 0.8f), stroke, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.8f, w * 0.2f), Offset(w * 0.2f, w * 0.8f), stroke, StrokeCap.Round)
    }
}
