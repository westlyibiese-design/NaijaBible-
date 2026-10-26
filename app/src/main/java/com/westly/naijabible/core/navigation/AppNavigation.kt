package com.westly.naijabible.core.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.westly.naijabible.R
import com.westly.naijabible.core.data.BibleIndex
import com.westly.naijabible.core.data.BibleRepository
import com.westly.naijabible.core.data.BookCatalog
import com.westly.naijabible.core.prefs.AppPreferences
import com.westly.naijabible.core.prefs.AppSettings
import com.westly.naijabible.core.prefs.BookmarkKey
import com.westly.naijabible.core.prefs.ReaderStore
import com.westly.naijabible.core.theme.NaijaTheme
import com.westly.naijabible.core.ui.BookmarkIcon
import com.westly.naijabible.core.ui.OpenBookIcon
import com.westly.naijabible.feature.bookmarks.BookmarksScreen
import com.westly.naijabible.feature.books.BooksScreen
import com.westly.naijabible.feature.chapters.ChaptersScreen
import com.westly.naijabible.feature.reading.ReadingScreen
import com.westly.naijabible.feature.settings.AboutScreen
import com.westly.naijabible.feature.settings.SettingsScreen
import kotlinx.coroutines.launch

/** The three tabs of the bottom bar. */
enum class TopDestination(val route: String, @StringRes val labelRes: Int) {
    Books("books", R.string.nav_books),
    Bookmarks("bookmarks", R.string.nav_bookmarks),
    Settings("settings", R.string.nav_settings)
}

private const val ARG_BOOK = "book"
private const val ARG_CHAPTER = "chapter"
private const val ARG_VERSE = "verse"
private const val ROUTE_CHAPTERS = "chapters/{$ARG_BOOK}"
private const val ROUTE_READING = "reading/{$ARG_BOOK}/{$ARG_CHAPTER}?$ARG_VERSE={$ARG_VERSE}"
private const val ROUTE_ABOUT = "about"

private fun chaptersRoute(book: Int) = "chapters/$book"
private fun readingRoute(book: Int, chapter: Int, verse: Int = 0) =
    if (verse > 0) "reading/$book/$chapter?$ARG_VERSE=$verse" else "reading/$book/$chapter"

@Composable
fun NaijaBibleApp(bibleIndex: BibleIndex) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val colors = NaijaTheme.colors

    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val repository = remember(bibleIndex) { BibleRepository(context.assets, bibleIndex) }
    val readerStore = remember { ReaderStore(context) }
    val preferences = remember { AppPreferences(context) }

    // Observed by the whole app: saved bookmarks, the chapter opened last in each book, the text size.
    val bookmarks by readerStore.bookmarks.collectAsState(initial = emptySet())
    val lastRead by readerStore.lastRead.collectAsState(initial = emptyMap())
    val settings by preferences.settings.collectAsState(initial = AppSettings())

    val isReading = currentRoute == ROUTE_READING
    // The chapter picker belongs to the Books tab, and the About page to the Settings tab.
    val selectedRoute = when (currentRoute) {
        ROUTE_CHAPTERS -> TopDestination.Books.route
        ROUTE_ABOUT -> TopDestination.Settings.route
        else -> currentRoute
    }

    // Back from the reading screen always goes to the chapter picker of the book on screen now.
    fun leaveReading(currentBook: Int) {
        val previous = navController.previousBackStackEntry
        // Opened from the Bookmarks screen: Back returns there.
        if (previous?.destination?.route == TopDestination.Bookmarks.route) {
            navController.popBackStack()
            return
        }
        val pickerIsBelow = previous != null &&
            previous.destination.route == ROUTE_CHAPTERS &&
            previous.arguments?.getInt(ARG_BOOK) == currentBook
        if (pickerIsBelow) {
            navController.popBackStack()
        } else {
            navController.navigate(chaptersRoute(currentBook)) {
                popUpTo(TopDestination.Books.route)
            }
        }
    }

    fun goTo(destination: TopDestination) {
        if (destination == TopDestination.Books && currentRoute == ROUTE_CHAPTERS) {
            navController.popBackStack(TopDestination.Books.route, inclusive = false)
            return
        }
        if (destination == TopDestination.Settings && currentRoute == ROUTE_ABOUT) {
            navController.popBackStack(TopDestination.Settings.route, inclusive = false)
            return
        }
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = colors.pageBackground,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        bottomBar = {
            if (!isReading) {
                NaijaBottomBar(
                    currentRoute = selectedRoute,
                    onSelect = { goTo(it) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopDestination.Books.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TopDestination.Books.route) {
                BooksScreen(
                    index = bibleIndex,
                    onOpenSettings = { goTo(TopDestination.Settings) },
                    onOpenBook = { navController.navigate(chaptersRoute(it)) }
                )
            }
            composable(
                route = ROUTE_CHAPTERS,
                arguments = listOf(navArgument(ARG_BOOK) { type = NavType.IntType })
            ) { entry ->
                val bookNumber = entry.arguments?.getInt(ARG_BOOK) ?: 0
                val book = BookCatalog.byNumber(bookNumber)
                if (book != null) {
                    ChaptersScreen(
                        book = book,
                        index = bibleIndex,
                        lastReadChapter = lastRead[book.number],
                        onBack = { navController.popBackStack() },
                        onOpenChapter = { chapter ->
                            navController.navigate(readingRoute(book.number, chapter))
                        }
                    )
                }
            }
            composable(
                route = ROUTE_READING,
                arguments = listOf(
                    navArgument(ARG_BOOK) { type = NavType.IntType },
                    navArgument(ARG_CHAPTER) { type = NavType.IntType },
                    navArgument(ARG_VERSE) {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { entry ->
                ReadingScreen(
                    initialBook = entry.arguments?.getInt(ARG_BOOK) ?: 1,
                    initialChapter = entry.arguments?.getInt(ARG_CHAPTER) ?: 1,
                    initialVerse = entry.arguments?.getInt(ARG_VERSE) ?: 0,
                    index = bibleIndex,
                    repository = repository,
                    bookmarks = bookmarks,
                    textSizeStep = settings.textSizeStep,
                    onTextSizeStepChange = { step ->
                        scope.launch { preferences.setTextSizeStep(step) }
                    },
                    onToggleBookmark = { bookmark: BookmarkKey ->
                        scope.launch { readerStore.toggleBookmark(bookmark) }
                    },
                    onChapterShown = { bookNumber, chapter ->
                        scope.launch { readerStore.setLastRead(bookNumber, chapter) }
                    },
                    onBack = { currentBook -> leaveReading(currentBook) },
                    onOpenBookmarks = { navController.navigate(TopDestination.Bookmarks.route) }
                )
            }
            composable(TopDestination.Bookmarks.route) {
                BookmarksScreen(
                    bookmarks = bookmarks,
                    repository = repository,
                    onOpenBookmark = { bookmark: BookmarkKey ->
                        navController.navigate(
                            readingRoute(bookmark.bookNumber, bookmark.chapter, bookmark.verse)
                        )
                    },
                    onToggleBookmark = { bookmark: BookmarkKey ->
                        scope.launch { readerStore.toggleBookmark(bookmark) }
                    }
                )
            }
            composable(TopDestination.Settings.route) {
                SettingsScreen(
                    textSizeStep = settings.textSizeStep,
                    onTextSizeStepChange = { step ->
                        scope.launch { preferences.setTextSizeStep(step) }
                    },
                    onDarkModeChange = { dark ->
                        scope.launch { preferences.setDarkMode(dark) }
                    },
                    onOpenAbout = { navController.navigate(ROUTE_ABOUT) }
                )
            }
            composable(ROUTE_ABOUT) {
                AboutScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun NaijaBottomBar(
    currentRoute: String?,
    onSelect: (TopDestination) -> Unit
) {
    val colors = NaijaTheme.colors
    Column {
        HorizontalDivider(color = colors.navBarBorder, thickness = 1.dp)
        NavigationBar(
            containerColor = colors.navBar,
            contentColor = colors.navInactive,
            tonalElevation = 0.dp
        ) {
            TopDestination.entries.forEach { destination ->
                val selected = currentRoute == destination.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelect(destination) },
                    icon = {
                        when (destination) {
                            TopDestination.Books -> OpenBookIcon(filled = selected)
                            TopDestination.Bookmarks -> BookmarkIcon(filled = selected)
                            TopDestination.Settings -> Icon(
                                imageVector = if (selected) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = null
                            )
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(destination.labelRes),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.gold,
                        selectedTextColor = colors.goldText,
                        unselectedIconColor = colors.navInactive,
                        unselectedTextColor = colors.navInactive,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
