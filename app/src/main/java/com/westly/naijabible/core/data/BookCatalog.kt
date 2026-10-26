package com.westly.naijabible.core.data

import java.text.Normalizer

enum class Testament { OT, NT }

private val accentMarks = Regex("\\p{M}+")

/** Lower-cases, removes accents, spaces and punctuation: "1 Sam." and "1sam" both become "1sam". */
fun normalizeForSearch(text: String): String =
    Normalizer.normalize(text, Normalizer.Form.NFD)
        .replace(accentMarks, "")
        .lowercase()
        .filter { it.isLetterOrDigit() }

/** One of the 66 books. Fixed data: it never depends on which text files exist. */
class BookInfo(
    val number: Int,
    val name: String,
    val testament: Testament,
    val chapterCount: Int
) {
    /** Name as written in the text file names: spaces become underscores. */
    val fileKey: String = name.replace(' ', '_')

    /** Search form of the name. */
    val searchKey: String = normalizeForSearch(name)
}

/** The fixed list of the 66 books of the Bible, in Bible order. */
object BookCatalog {

    val books: List<BookInfo> = listOf(
        BookInfo(1, "Genesis", Testament.OT, 50),
        BookInfo(2, "Exodus", Testament.OT, 40),
        BookInfo(3, "Leviticus", Testament.OT, 27),
        BookInfo(4, "Numbers", Testament.OT, 36),
        BookInfo(5, "Deuteronomy", Testament.OT, 34),
        BookInfo(6, "Joshua", Testament.OT, 24),
        BookInfo(7, "Judges", Testament.OT, 21),
        BookInfo(8, "Ruth", Testament.OT, 4),
        BookInfo(9, "1 Samuel", Testament.OT, 31),
        BookInfo(10, "2 Samuel", Testament.OT, 24),
        BookInfo(11, "1 Kings", Testament.OT, 22),
        BookInfo(12, "2 Kings", Testament.OT, 25),
        BookInfo(13, "1 Chronicles", Testament.OT, 29),
        BookInfo(14, "2 Chronicles", Testament.OT, 36),
        BookInfo(15, "Ezra", Testament.OT, 10),
        BookInfo(16, "Nehemiah", Testament.OT, 13),
        BookInfo(17, "Esther", Testament.OT, 10),
        BookInfo(18, "Job", Testament.OT, 42),
        BookInfo(19, "Psalms", Testament.OT, 150),
        BookInfo(20, "Proverbs", Testament.OT, 31),
        BookInfo(21, "Ecclesiastes", Testament.OT, 12),
        BookInfo(22, "Song of Solomon", Testament.OT, 8),
        BookInfo(23, "Isaiah", Testament.OT, 66),
        BookInfo(24, "Jeremiah", Testament.OT, 52),
        BookInfo(25, "Lamentations", Testament.OT, 5),
        BookInfo(26, "Ezekiel", Testament.OT, 48),
        BookInfo(27, "Daniel", Testament.OT, 12),
        BookInfo(28, "Hosea", Testament.OT, 14),
        BookInfo(29, "Joel", Testament.OT, 3),
        BookInfo(30, "Amos", Testament.OT, 9),
        BookInfo(31, "Obadiah", Testament.OT, 1),
        BookInfo(32, "Jonah", Testament.OT, 4),
        BookInfo(33, "Micah", Testament.OT, 7),
        BookInfo(34, "Nahum", Testament.OT, 3),
        BookInfo(35, "Habakkuk", Testament.OT, 3),
        BookInfo(36, "Zephaniah", Testament.OT, 3),
        BookInfo(37, "Haggai", Testament.OT, 2),
        BookInfo(38, "Zechariah", Testament.OT, 14),
        BookInfo(39, "Malachi", Testament.OT, 4),
        BookInfo(40, "Matthew", Testament.NT, 28),
        BookInfo(41, "Mark", Testament.NT, 16),
        BookInfo(42, "Luke", Testament.NT, 24),
        BookInfo(43, "John", Testament.NT, 21),
        BookInfo(44, "Acts", Testament.NT, 28),
        BookInfo(45, "Romans", Testament.NT, 16),
        BookInfo(46, "1 Corinthians", Testament.NT, 16),
        BookInfo(47, "2 Corinthians", Testament.NT, 13),
        BookInfo(48, "Galatians", Testament.NT, 6),
        BookInfo(49, "Ephesians", Testament.NT, 6),
        BookInfo(50, "Philippians", Testament.NT, 4),
        BookInfo(51, "Colossians", Testament.NT, 4),
        BookInfo(52, "1 Thessalonians", Testament.NT, 5),
        BookInfo(53, "2 Thessalonians", Testament.NT, 3),
        BookInfo(54, "1 Timothy", Testament.NT, 6),
        BookInfo(55, "2 Timothy", Testament.NT, 4),
        BookInfo(56, "Titus", Testament.NT, 3),
        BookInfo(57, "Philemon", Testament.NT, 1),
        BookInfo(58, "Hebrews", Testament.NT, 13),
        BookInfo(59, "James", Testament.NT, 5),
        BookInfo(60, "1 Peter", Testament.NT, 5),
        BookInfo(61, "2 Peter", Testament.NT, 3),
        BookInfo(62, "1 John", Testament.NT, 5),
        BookInfo(63, "2 John", Testament.NT, 1),
        BookInfo(64, "3 John", Testament.NT, 1),
        BookInfo(65, "Jude", Testament.NT, 1),
        BookInfo(66, "Revelation", Testament.NT, 22),
    )

    val oldTestament: List<BookInfo> = books.filter { it.testament == Testament.OT }
    val newTestament: List<BookInfo> = books.filter { it.testament == Testament.NT }

    /** Book by its number 1 to 66, or null. */
    fun byNumber(number: Int): BookInfo? = books.getOrNull(number - 1)

    /**
     * Live search over all 66 books. Case, accents and spaces are ignored.
     * Books whose name starts with the typed text come first, then books that contain it
     * anywhere in the name; each group is in Bible order.
     */
    fun search(query: String): List<BookInfo> {
        val key = normalizeForSearch(query)
        if (key.isEmpty()) return emptyList()
        val startsWith = books.filter { it.searchKey.startsWith(key) }
        val contains = books.filter { !it.searchKey.startsWith(key) && it.searchKey.contains(key) }
        return startsWith + contains
    }
}
