/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.zhanghai.android.untracker.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.zhanghai.android.untracker.R
import me.zhanghai.android.untracker.ui.component.NavigationItemInfo
import me.zhanghai.android.untracker.util.asInsets
import me.zhanghai.android.untracker.util.copy
import me.zhanghai.compose.preference.Preference

val AboutPaneInfo: NavigationItemInfo =
    NavigationItemInfo(
        route = "about",
        iconResourceId = R.drawable.about_icon_animated_24dp,
        labelResourceId = R.string.main_about
    )

fun NavGraphBuilder.aboutPane(contentPadding: PaddingValues, navigateToLicensesScreen: () -> Unit) {
    composable(AboutPaneInfo.route) {
        AboutPane(
            contentPadding = contentPadding,
            navigateToLicensesScreen = navigateToLicensesScreen
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AboutPane(contentPadding: PaddingValues, navigateToLicensesScreen: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Column(modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection)) {
        TopAppBar(
            title = { Text(text = stringResource(AboutPaneInfo.labelResourceId)) },
            modifier = Modifier.fillMaxWidth(),
            windowInsets = contentPadding.copy(bottom = 0.dp).asInsets(),
            scrollBehavior = scrollBehavior
        )
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding.copy(top = 0.dp))
        ) {
            Image(
                painter = painterResource(R.drawable.launcher_icon_monochrome),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(162.dp).padding(horizontal = 16.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.FillHeight,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
            )
            val context = LocalContext.current
            val packageInfo =
                remember(context) {
                    context.packageManager.getPackageInfo(context.packageName, 0)!!
                }
            @Suppress("DEPRECATION")
            val version =
                stringResource(
                    R.string.main_about_version_summary,
                    packageInfo.versionName.orEmpty(),
                    packageInfo.versionCode
                )
            val clipboardManager = LocalClipboardManager.current
            Preference(
                title = { Text(text = stringResource(R.string.main_about_version_title)) },
                modifier = Modifier.fillMaxWidth(),
                summary = { Text(text = version) }
            ) {
                clipboardManager.setText(AnnotatedString(version))
            }
            val uriHandler = LocalUriHandler.current
            val githubUri = stringResource(R.string.main_about_github_uri)
            Preference(
                title = { Text(text = stringResource(R.string.main_about_github_title)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                uriHandler.openUri(githubUri)
            }
            Preference(
                title = { Text(text = stringResource(R.string.main_about_licenses)) },
                modifier = Modifier.fillMaxWidth(),
                onClick = navigateToLicensesScreen
            )
        }
    }
}
