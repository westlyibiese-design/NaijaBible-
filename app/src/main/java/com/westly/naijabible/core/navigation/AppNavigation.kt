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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.westly.naijabible.R
import com.westly.naijabible.core.data.BibleIndex
import com.westly.naijabible.core.theme.NaijaTheme
import com.westly.naijabible.core.ui.BookmarkIcon
import com.westly.naijabible.core.ui.OpenBookIcon
import com.westly.naijabible.feature.bookmarks.BookmarksScreen
import com.westly.naijabible.feature.books.BooksScreen
import com.westly.naijabible.feature.settings.SettingsScreen

/** The three tabs of the bottom bar. */
enum class TopDestination(val route: String, @StringRes val labelRes: Int) {
    Books("books", R.string.nav_books),
    Bookmarks("bookmarks", R.string.nav_bookmarks),
    Settings("settings", R.string.nav_settings)
}

@Composable
fun NaijaBibleApp(bibleIndex: BibleIndex) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val colors = NaijaTheme.colors

    fun goTo(destination: TopDestination) {
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
            NaijaBottomBar(
                currentRoute = currentRoute,
                onSelect = { goTo(it) }
            )
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
                    onOpenSettings = { goTo(TopDestination.Settings) }
                )
            }
            composable(TopDestination.Bookmarks.route) {
                BookmarksScreen()
            }
            composable(TopDestination.Settings.route) {
                SettingsScreen()
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
