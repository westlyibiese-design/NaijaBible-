package com.westly.naijabible.core.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "naijabible_settings"
)

/** Dark mode choice. Until the person picks one, the app follows the phone. */
enum class DarkModeChoice { FOLLOW_SYSTEM, DARK, LIGHT }

/** Reading text sizes. The step is saved; the scale is multiplied on top of the phone's font size. */
object ReadingTextScale {
    val steps: List<Float> = listOf(0.85f, 0.92f, 1.00f, 1.15f, 1.30f)
    const val DEFAULT_STEP: Int = 2

    fun forStep(step: Int): Float = steps[step.coerceIn(0, steps.lastIndex)]
}

data class AppSettings(
    val darkMode: DarkModeChoice = DarkModeChoice.FOLLOW_SYSTEM,
    val textSizeStep: Int = ReadingTextScale.DEFAULT_STEP
) {
    /** Multiplier for verse text. The reading screen (Phase 4) multiplies this on top of the phone's font scale. */
    val readingTextScale: Float get() = ReadingTextScale.forStep(textSizeStep)
}

class AppPreferences(context: Context) {

    private val store: DataStore<Preferences> = context.applicationContext.settingsDataStore

    val settings: Flow<AppSettings> = store.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { prefs ->
            AppSettings(
                darkMode = when (prefs[DARK_MODE_KEY]) {
                    VALUE_DARK -> DarkModeChoice.DARK
                    VALUE_LIGHT -> DarkModeChoice.LIGHT
                    else -> DarkModeChoice.FOLLOW_SYSTEM
                },
                textSizeStep = (prefs[TEXT_SIZE_STEP_KEY] ?: ReadingTextScale.DEFAULT_STEP)
                    .coerceIn(0, ReadingTextScale.steps.lastIndex)
            )
        }

    suspend fun setDarkMode(dark: Boolean) {
        store.edit { it[DARK_MODE_KEY] = if (dark) VALUE_DARK else VALUE_LIGHT }
    }

    suspend fun setTextSizeStep(step: Int) {
        store.edit { it[TEXT_SIZE_STEP_KEY] = step.coerceIn(0, ReadingTextScale.steps.lastIndex) }
    }

    private companion object {
        val DARK_MODE_KEY = stringPreferencesKey("dark_mode")
        val TEXT_SIZE_STEP_KEY = intPreferencesKey("text_size_step")
        const val VALUE_DARK = "dark"
        const val VALUE_LIGHT = "light"
    }
}
