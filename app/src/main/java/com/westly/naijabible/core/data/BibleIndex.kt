package com.westly.naijabible.core.data

import android.content.res.AssetManager

/** One Bible text file that passed the name check: which book and which chapters it covers. */
class BibleFileInfo(
    val bookNumber: Int,
    val firstChapter: Int,
    val lastChapter: Int,
    val assetPath: String
)

/**
 * Which books and chapters have text, worked out ONLY from the file names in assets/bible/.
 * The JSON inside the files is not opened here, so building the index is fast.
 * Nothing in this class knows which books exist: adding files needs no code change.
 */
class BibleIndex private constructor(
    private val filesByBook: Map<Int, List<BibleFileInfo>>
) {

    /** Numbers (1 to 66) of the books that have at least one text file. */
    val availableBookNumbers: Set<Int> get() = filesByBook.keys

    fun isBookAvailable(bookNumber: Int): Boolean = filesByBook.containsKey(bookNumber)

    /** The file that covers this chapter, or null when no file covers it. */
    fun fileFor(bookNumber: Int, chapter: Int): BibleFileInfo? =
        filesByBook[bookNumber]?.firstOrNull { chapter in it.firstChapter..it.lastChapter }

    fun isChapterAvailable(bookNumber: Int, chapter: Int): Boolean =
        fileFor(bookNumber, chapter) != null

    /** The chapter numbers of a book that have text, in order. */
    fun availableChapters(bookNumber: Int): List<Int> {
        val total = BookCatalog.byNumber(bookNumber)?.chapterCount ?: return emptyList()
        return (1..total).filter { isChapterAvailable(bookNumber, it) }
    }

    companion object {
        const val ASSET_DIR = "bible"

        private val FILE_NAME = Regex("^(\\d{2})_(.+)_ch(\\d{3})-(\\d{3})\\.json$")

        val EMPTY: BibleIndex = BibleIndex(emptyMap())

        /**
         * Checks one file name against the naming rules and the catalog.
         * Returns null for any file that does not pass (such a file is ignored by the app).
         */
        fun parseFileName(fileName: String): BibleFileInfo? {
            val match = FILE_NAME.matchEntire(fileName) ?: return null
            val number = match.groupValues[1].toIntOrNull() ?: return null
            val book = BookCatalog.byNumber(number) ?: return null
            if (match.groupValues[2] != book.fileKey) return null
            val first = match.groupValues[3].toIntOrNull() ?: return null
            val last = match.groupValues[4].toIntOrNull() ?: return null
            if (first < 1 || last < first || last > book.chapterCount) return null
            return BibleFileInfo(number, first, last, "$ASSET_DIR/$fileName")
        }

        fun fromFileNames(fileNames: List<String>): BibleIndex {
            val grouped = fileNames
                .sorted()
                .mapNotNull { parseFileName(it) }
                .groupBy { it.bookNumber }
            return BibleIndex(grouped)
        }

        /** Reads the file names in assets/bible/. Never throws: on any problem the index is empty. */
        fun build(assets: AssetManager): BibleIndex {
            return try {
                fromFileNames(assets.list(ASSET_DIR)?.toList().orEmpty())
            } catch (e: Exception) {
                EMPTY
            }
        }
    }
}
