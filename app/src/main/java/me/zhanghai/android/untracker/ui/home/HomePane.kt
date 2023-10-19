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

package me.zhanghai.android.untracker.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.zhanghai.android.untracker.R
import me.zhanghai.android.untracker.ui.component.NavigationItemInfo
import me.zhanghai.android.untracker.util.asInsets
import me.zhanghai.android.untracker.util.copy
import me.zhanghai.android.untracker.util.plus

val HomePaneInfo: NavigationItemInfo =
    NavigationItemInfo(
        route = "home",
        iconResourceId = R.drawable.home_icon_animated_24dp,
        labelResourceId = R.string.main_home
    )

fun NavGraphBuilder.homePane(contentPadding: PaddingValues) {
    composable(HomePaneInfo.route) { HomePane(contentPadding = contentPadding) }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomePane(contentPadding: PaddingValues) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Column(modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection)) {
        TopAppBar(
            title = { Text(text = stringResource(HomePaneInfo.labelResourceId)) },
            modifier = Modifier.fillMaxWidth(),
            windowInsets = contentPadding.copy(bottom = 0.dp).asInsets(),
            scrollBehavior = scrollBehavior
        )
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding.copy(top = 0.dp) + PaddingValues(bottom = 16.dp))
        ) {
            OutlinedCard(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Image(
                        imageVector = shareIllustration(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.main_home_share_title),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.main_home_share_text),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedCard(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Image(
                        imageVector = selectIllustration(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.main_home_select_title),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.main_home_select_text),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
