package com.westly.naijabible.feature.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

private const val REPORT_EMAIL = "westlyibiese@gmail.com"
private const val REPORT_SUBJECT = "Naija Bible: Report a mistake"
private const val REPORT_BODY = "Verse: \nWhat is wrong: \nSuggested fix: "

/** The version shown in Settings and About. It comes from the build (versionName), never typed by hand. */
@Suppress("DEPRECATION")
fun appVersionName(context: Context): String {
    return try {
        val info = if (Build.VERSION.SDK_INT >= 33) {
            context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        info.versionName.orEmpty()
    } catch (e: Exception) {
        ""
    }
}

/**
 * Opens the email app with the address, subject and body for "Report a mistake".
 * Returns false when the phone has no email app.
 */
fun openReportEmail(context: Context): Boolean {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(REPORT_EMAIL))
        putExtra(Intent.EXTRA_SUBJECT, REPORT_SUBJECT)
        putExtra(Intent.EXTRA_TEXT, REPORT_BODY)
    }
    return try {
        context.startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    }
}
