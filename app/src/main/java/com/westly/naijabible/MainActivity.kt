package com.westly.naijabible

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.westly.naijabible.core.navigation.NaijaBibleApp
import com.westly.naijabible.core.prefs.AppPreferences
import com.westly.naijabible.core.prefs.AppSettings
import com.westly.naijabible.core.prefs.DarkModeChoice
import com.westly.naijabible.core.theme.NaijaBibleTheme
import com.westly.naijabible.feature.splash.AnimatedSplash
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Null until the saved settings are read. The system splash stays up until then,
    // so the first thing the person sees is already in the right theme.
    private var settings by mutableStateOf<AppSettings?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val preferences = AppPreferences(applicationContext)
        splashScreen.setKeepOnScreenCondition { settings == null }
        lifecycleScope.launch {
            preferences.settings.collect { settings = it }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            val current = settings
            if (current != null) {
                val darkTheme = when (current.darkMode) {
                    DarkModeChoice.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                    DarkModeChoice.DARK -> true
                    DarkModeChoice.LIGHT -> false
                }
                var splashDone by rememberSaveable { mutableStateOf(false) }
                var appPreloaded by remember { mutableStateOf(false) }

                // The header is green in both themes, so status bar icons are always light.
                // The bottom bar is white in the light theme, so its icons are dark there.
                DisposableEffect(darkTheme, splashDone) {
                    val navigationStyle = if (darkTheme || !splashDone) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    }
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
                        navigationBarStyle = navigationStyle
                    )
                    onDispose { }
                }

                NaijaBibleTheme(
                    darkTheme = darkTheme,
                    readingTextScale = current.readingTextScale
                ) {
                    Box(Modifier.fillMaxSize()) {
                        if (splashDone || appPreloaded) {
                            NaijaBibleApp()
                        }
                        if (!splashDone) {
                            AnimatedSplash(
                                dataReady = true,
                                onPreloadApp = { appPreloaded = true },
                                onFinished = { splashDone = true }
                            )
                        }
                    }
                }
            }
        }
    }
}
