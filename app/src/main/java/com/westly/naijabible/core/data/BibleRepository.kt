package com.westly.naijabible.core.data

import android.content.res.AssetManager
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/** One verse exactly as it is in the text file. */
class Verse(val number: Int, val text: String)

/** One chapter: the verses, and the psalm title when the file has one (a title is not a verse). */
class ChapterText(
    val bookNumber: Int,
    val chapter: Int,
    val title: String?,
    val verses: List<Verse>
)

/**
 * Loads the verses of one chapter. Only the one file that covers the chapter is opened and parsed,
 * on a background thread. The last few parsed files are kept in memory.
 * Returns null ("not available") when no file covers the chapter or the file cannot be read.
 * Never throws.
 */
class BibleRepository(
    private val assets: AssetManager,
    private val index: BibleIndex
) {

    private val cache = LruCache<String, Map<Int, ChapterText>>(CACHED_FILES)

    suspend fun loadChapter(bookNumber: Int, chapter: Int): ChapterText? =
        withContext(Dispatchers.IO) {
            val file = index.fileFor(bookNumber, chapter) ?: return@withContext null
            val chapters = cache.get(file.assetPath) ?: run {
                val parsed = parseFile(file)
                if (parsed != null) cache.put(file.assetPath, parsed)
                parsed
            }
            chapters?.get(chapter)
        }

    private fun parseFile(file: BibleFileInfo): Map<Int, ChapterText>? {
        return try {
            val raw = assets.open(file.assetPath).bufferedReader(Charsets.UTF_8).use { it.readText() }
            val root = JSONObject(raw)
            if (root.optInt("bookNumber", -1) != file.bookNumber) return null
            val chapterArray = root.getJSONArray("chapters")
            val result = HashMap<Int, ChapterText>()
            for (i in 0 until chapterArray.length()) {
                val chapterObject = chapterArray.getJSONObject(i)
                val chapterNumber = chapterObject.optInt("chapter", -1)
                if (chapterNumber < 1) continue
                val title = if (chapterObject.has("title") && !chapterObject.isNull("title")) {
                    chapterObject.getString("title").takeIf { it.isNotBlank() }
                } else {
                    null
                }
                val verseArray = chapterObject.getJSONArray("verses")
                val verses = ArrayList<Verse>(verseArray.length())
                for (j in 0 until verseArray.length()) {
                    val verseObject = verseArray.getJSONObject(j)
                    val verseNumber = verseObject.optInt("verse", -1)
                    if (verseNumber < 1) continue
                    verses.add(Verse(verseNumber, verseObject.optString("text", "")))
                }
                result[chapterNumber] = ChapterText(file.bookNumber, chapterNumber, title, verses)
            }
            result
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        const val CACHED_FILES = 4
    }
}
