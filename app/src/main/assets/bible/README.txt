NaijaBible: Bible text files

Put the Bible text files in this folder. The app finds them by their file names.

File name pattern:  NN_BookName_chAAA-BBB.json
  NN         the book number, 01 to 66
  BookName   the English book name, spaces written as underscores
  AAA-BBB    the first and last chapter in the file, 3 digits each

Examples:
  01_Genesis_ch001-010.json
  09_1_Samuel_ch001-010.json
  22_Song_of_Solomon_ch001-008.json

One book can have several files (up to 10 chapters each).
Different books are never in the same file.
A file with a wrong name, invalid JSON, or a bookNumber that does not match its name is ignored.

To add text later: drop the new files here, raise versionCode in app/build.gradle.kts, and build.
