package com.westly.naijabible.core.data

/** A chapter of a book: used to say where Previous and Next lead. */
class ChapterRef(val bookNumber: Int, val chapter: Int)

private const val LAST_BOOK_NUMBER = 66

/**
 * The next chapter that has text: the next available chapter of the same book, or, after the
 * last available chapter of the book, the first available chapter of the next book that has text.
 * Null when there is nothing further.
 */
fun BibleIndex.nextChapter(bookNumber: Int, chapter: Int): ChapterRef? {
    availableChapters(bookNumber).firstOrNull { it > chapter }?.let {
        return ChapterRef(bookNumber, it)
    }
    for (otherBook in (bookNumber + 1)..LAST_BOOK_NUMBER) {
        val first = availableChapters(otherBook).firstOrNull()
        if (first != null) return ChapterRef(otherBook, first)
    }
    return null
}

/**
 * The previous chapter that has text: the previous available chapter of the same book, or, before
 * the first available chapter of the book, the last available chapter of the previous book that has text.
 * Null when there is nothing before.
 */
fun BibleIndex.previousChapter(bookNumber: Int, chapter: Int): ChapterRef? {
    availableChapters(bookNumber).lastOrNull { it < chapter }?.let {
        return ChapterRef(bookNumber, it)
    }
    for (otherBook in (bookNumber - 1) downTo 1) {
        val last = availableChapters(otherBook).lastOrNull()
        if (last != null) return ChapterRef(otherBook, last)
    }
    return null
}
