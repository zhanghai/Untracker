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

package me.zhanghai.android.untracker.ui.main

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import me.zhanghai.android.untracker.ui.about.AboutPaneInfo
import me.zhanghai.android.untracker.ui.about.aboutPaneEntry
import me.zhanghai.android.untracker.ui.component.Navigator
import me.zhanghai.android.untracker.ui.component.PaneNavigator
import me.zhanghai.android.untracker.ui.home.HomePaneInfo
import me.zhanghai.android.untracker.ui.home.HomePaneKey
import me.zhanghai.android.untracker.ui.home.homePaneEntry
import me.zhanghai.android.untracker.ui.rule.RulesPaneInfo
import me.zhanghai.android.untracker.ui.rule.rulesPaneEntry
import me.zhanghai.android.untracker.util.topLevelPopTransitionSpec
import me.zhanghai.android.untracker.util.topLevelTransitionSpec

@Serializable data object MainScreenKey : MainAppScreenKey

fun EntryProviderScope<MainAppScreenKey>.mainScreenEntry(navigator: Navigator<MainAppScreenKey>) {
    entry<MainScreenKey> { MainScreen(navigator) }
}

interface MainScreenPaneKey : NavKey

@Composable
@OptIn(ExperimentalAnimationGraphicsApi::class)
fun MainScreen(navigator: Navigator<MainAppScreenKey>) {
    @Suppress("UNCHECKED_CAST")
    val paneBackStack = rememberNavBackStack(HomePaneKey) as NavBackStack<MainScreenPaneKey>
    val paneNavigator = remember { PaneNavigator(paneBackStack) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                windowInsets =
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                    )
            ) {
                for (navigationItem in listOf(HomePaneInfo, RulesPaneInfo, AboutPaneInfo)) {
                    val selected = paneNavigator.currentKey == navigationItem.key
                    NavigationBarItem(
                        selected = selected,
                        onClick = { paneNavigator.navigateTo(navigationItem.key) },
                        icon = {
                            Icon(
                                painter =
                                    rememberAnimatedVectorPainter(
                                        AnimatedImageVector.animatedVectorResource(
                                            navigationItem.iconResourceId
                                        ),
                                        selected,
                                    ),
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(navigationItem.labelResourceId),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        },
                    )
                }
            }
        },
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { contentPadding ->
        NavDisplay(
            backStack = paneBackStack,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = { topLevelTransitionSpec() },
            popTransitionSpec = { topLevelPopTransitionSpec() },
            predictivePopTransitionSpec = { topLevelPopTransitionSpec() },
            entryProvider =
                entryProvider {
                    homePaneEntry(contentPadding)
                    rulesPaneEntry(contentPadding, navigator)
                    aboutPaneEntry(contentPadding, navigator)
                },
        )
    }
}
