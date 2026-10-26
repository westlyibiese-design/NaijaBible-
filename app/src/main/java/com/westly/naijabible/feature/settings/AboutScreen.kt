package com.westly.naijabible.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.westly.naijabible.R
import com.westly.naijabible.core.theme.NaijaTheme
import com.westly.naijabible.core.ui.NaijaBackHeader

/** About page: opened from Settings. The version comes from the build. */
@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NaijaTheme.colors
    val context = LocalContext.current
    val version = remember { appVersionName(context) }
    val shape = RoundedCornerShape(24.dp)

    Column(
        modifier
            .fillMaxSize()
            .background(colors.pageBackground)
    ) {
        NaijaBackHeader(onBack = onBack)

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
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
                    )
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_title),
                    color = colors.bookTitle,
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = stringResource(R.string.app_tagline),
                    color = colors.textSecondary,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.about_text_1),
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    lineHeight = 26.sp
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.about_text_2),
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    lineHeight = 26.sp
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.about_version, version),
                    color = colors.textSecondary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.copyright),
                    color = colors.textSecondary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.about_contact),
                    color = colors.textSecondary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
