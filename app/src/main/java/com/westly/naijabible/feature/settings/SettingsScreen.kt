package com.westly.naijabible.feature.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.westly.naijabible.R
import com.westly.naijabible.core.theme.NaijaTheme
import com.westly.naijabible.core.ui.ChevronIcon
import com.westly.naijabible.core.ui.FlagIcon
import com.westly.naijabible.core.ui.InfoIcon
import com.westly.naijabible.core.ui.MoonIcon
import com.westly.naijabible.core.ui.NaijaMainHeader
import com.westly.naijabible.core.ui.ShareIcon
import com.westly.naijabible.core.ui.StarIcon
import com.westly.naijabible.core.ui.TextSizeControl
import com.westly.naijabible.core.ui.TextSizeIcon

/**
 * Settings tab: Display (Dark mode, Text size) and About (About, Rate, Share, Report a mistake).
 * Dark mode shows the theme that is really in use. Text size here is the App text size (the whole app);
 * the "Aa" button on the reading screen has its own, separate Reading text size.
 */
@Composable
fun SettingsScreen(
    appTextSizeStep: Int,
    onAppTextSizeStepChange: (Int) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    val context = LocalContext.current
    val version = remember { appVersionName(context) }
    val noEmailMessage = stringResource(R.string.settings_no_email_app)

    Column(
        modifier
            .fillMaxSize()
            .background(colors.pageBackground)
    ) {
        NaijaMainHeader()

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            SettingsCard {
                SectionLabel(stringResource(R.string.settings_display))
                SettingRow(
                    title = stringResource(R.string.settings_dark_mode),
                    subtitle = stringResource(R.string.settings_dark_mode_sub),
                    icon = { MoonIcon(tint = colors.settingsIcon) },
                    modifier = Modifier.toggleable(
                        value = colors.isDark,
                        role = Role.Switch,
                        onValueChange = onDarkModeChange
                    ),
                    trailing = {
                        Switch(
                            checked = colors.isDark,
                            onCheckedChange = null,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.switchThumbOn,
                                checkedTrackColor = colors.switchTrackOn,
                                checkedBorderColor = colors.switchTrackOn,
                                uncheckedThumbColor = colors.switchThumbOff,
                                uncheckedTrackColor = colors.switchTrackOff,
                                uncheckedBorderColor = colors.switchBorderOff
                            )
                        )
                    }
                )
                RowDivider()
                SettingRow(
                    title = stringResource(R.string.settings_app_text_size),
                    subtitle = stringResource(R.string.settings_text_size_sub),
                    icon = { TextSizeIcon(tint = colors.settingsIcon) }
                )
                TextSizeControl(
                    step = appTextSizeStep,
                    onStepChange = onAppTextSizeStepChange,
                    label = stringResource(R.string.settings_app_text_size),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            SettingsCard {
                SectionLabel(stringResource(R.string.settings_about_section))
                SettingRow(
                    title = stringResource(R.string.settings_about_app),
                    subtitle = stringResource(R.string.settings_about_app_sub),
                    icon = { InfoIcon(tint = colors.settingsIcon) },
                    modifier = Modifier.clickable(role = Role.Button, onClick = onOpenAbout),
                    trailing = { ChevronIcon(tint = colors.gold) }
                )
                RowDivider()
                SettingRow(
                    title = stringResource(R.string.settings_rate),
                    subtitle = stringResource(R.string.settings_rate_sub),
                    icon = { StarIcon(tint = colors.settingsIcon) },
                    modifier = Modifier.clickable(role = Role.Button) {
                        // TODO: Rate this app. Will be connected after the app is on Google Play.
                    },
                    trailing = { ChevronIcon(tint = colors.gold) }
                )
                RowDivider()
                SettingRow(
                    title = stringResource(R.string.settings_share),
                    subtitle = stringResource(R.string.settings_share_sub),
                    icon = { ShareIcon(tint = colors.settingsIcon) },
                    modifier = Modifier.clickable(role = Role.Button) {
                        // TODO: Share this app. Will be connected after the app is on Google Play.
                    },
                    trailing = { ChevronIcon(tint = colors.gold) }
                )
                RowDivider()
                SettingRow(
                    title = stringResource(R.string.settings_report),
                    subtitle = stringResource(R.string.settings_report_sub),
                    icon = { FlagIcon(tint = colors.settingsIcon) },
                    modifier = Modifier.clickable(role = Role.Button) {
                        if (!openReportEmail(context)) {
                            Toast.makeText(context, noEmailMessage, Toast.LENGTH_SHORT).show()
                        }
                    },
                    trailing = { ChevronIcon(tint = colors.gold) }
                )
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.settings_footer_version, version),
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.copyright),
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** A rounded white (dark: charcoal) card, with the soft shadow in the light theme. */
@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    val colors = NaijaTheme.colors
    val shape = RoundedCornerShape(24.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .then(
                if (!colors.isDark) {
                    Modifier.shadow(
                        elevation = 2.dp,
                        shape = shape,
                        ambientColor = Color(0x22000000),
                        spotColor = Color(0x22000000)
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(colors.card, shape)
            .then(
                if (colors.isDark) Modifier.border(1.dp, colors.cardBorder, shape) else Modifier
            ),
        content = content
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = NaijaTheme.colors.textSecondary,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 2.dp)
    )
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        color = NaijaTheme.colors.divider,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

/** One row: icon, title and subtitle, and an optional switch or arrow at the end. */
@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    val colors = NaijaTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(48.dp), contentAlignment = Alignment.CenterStart) {
            icon()
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = colors.textPrimary,
                fontSize = 19.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = colors.textSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (trailing != null) {
            Spacer(Modifier.width(12.dp))
            trailing()
        }
    }
}
