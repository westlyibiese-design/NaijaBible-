package com.westly.naijabible.core.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.readerDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "naijabible_reader"
)

/** One bookmarked verse, identified by book number, chapter and verse. */
data class BookmarkKey(val bookNumber: Int, val chapter: Int, val verse: Int)

/**
 * Saved on the phone only: the bookmarked verses and, for every book, the chapter opened last.
 */
class ReaderStore(context: Context) {

    private val store: DataStore<Preferences> = context.applicationContext.readerDataStore

    private val data: Flow<Preferences> = store.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }

    /** All bookmarks. Entries that cannot be read are skipped. */
    val bookmarks: Flow<Set<BookmarkKey>> = data.map { prefs ->
        (prefs[BOOKMARKS_KEY] ?: emptySet()).mapNotNull { decode(it) }.toSet()
    }

    /** Book number to the chapter opened last in that book. */
    val lastRead: Flow<Map<Int, Int>> = data.map { prefs ->
        val result = HashMap<Int, Int>()
        for ((key, value) in prefs.asMap()) {
            if (key.name.startsWith(LAST_READ_PREFIX) && value is Int) {
                key.name.removePrefix(LAST_READ_PREFIX).toIntOrNull()?.let { result[it] = value }
            }
        }
        result
    }

    /** Adds the bookmark, or removes it when it is already there. */
    suspend fun toggleBookmark(bookmark: BookmarkKey) {
        val encoded = encode(bookmark)
        store.edit { prefs ->
            val current = prefs[BOOKMARKS_KEY] ?: emptySet()
            prefs[BOOKMARKS_KEY] = if (encoded in current) current - encoded else current + encoded
        }
    }

    suspend fun setLastRead(bookNumber: Int, chapter: Int) {
        store.edit { prefs ->
            prefs[intPreferencesKey(LAST_READ_PREFIX + bookNumber)] = chapter
        }
    }

    private companion object {
        val BOOKMARKS_KEY = stringSetPreferencesKey("bookmarks")
        const val LAST_READ_PREFIX = "last_read_"

        fun encode(bookmark: BookmarkKey): String =
            "${bookmark.bookNumber}:${bookmark.chapter}:${bookmark.verse}"

        fun decode(text: String): BookmarkKey? {
            val parts = text.split(':')
            if (parts.size != 3) return null
            val book = parts[0].toIntOrNull() ?: return null
            val chapter = parts[1].toIntOrNull() ?: return null
            val verse = parts[2].toIntOrNull() ?: return null
            return BookmarkKey(book, chapter, verse)
        }
    }
}
