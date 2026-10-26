# NaijaBible release rules (R8).
#
# The app reads Bible text with android.util / org.json from the Android framework, uses DataStore,
# Compose and Navigation (they ship their own consumer rules), and has no reflection of its own.
# Fonts and icons are referenced directly from code (R.font, R.mipmap), so resource shrinking keeps them.
#
# Readable crash reports: keep line numbers, hide the real file name.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
