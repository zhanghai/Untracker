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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.zhanghai.android.untracker.ui.about.AboutPaneInfo
import me.zhanghai.android.untracker.ui.about.aboutPane
import me.zhanghai.android.untracker.ui.home.HomePaneInfo
import me.zhanghai.android.untracker.ui.home.homePane
import me.zhanghai.android.untracker.ui.rule.RulesPaneInfo
import me.zhanghai.android.untracker.ui.rule.rulesPane
import me.zhanghai.android.untracker.util.fadeThroughEnter
import me.zhanghai.android.untracker.util.fadeThroughExit
import me.zhanghai.android.untracker.util.fadeThroughPopEnter
import me.zhanghai.android.untracker.util.fadeThroughPopExit

const val MainScreenRoute = "main"

fun NavGraphBuilder.mainScreen(
    navigateToRuleScreen: (String) -> Unit,
    navigateToAddRuleScreen: () -> Unit,
    navigateToLicensesScreen: () -> Unit,
) {
    composable(MainScreenRoute) {
        MainScreen(
            navigateToRuleScreen = navigateToRuleScreen,
            navigateToAddRuleScreen = navigateToAddRuleScreen,
            navigateToLicensesScreen = navigateToLicensesScreen,
        )
    }
}

@Composable
@OptIn(ExperimentalAnimationGraphicsApi::class)
fun MainScreen(
    navigateToRuleScreen: (String) -> Unit,
    navigateToAddRuleScreen: () -> Unit,
    navigateToLicensesScreen: () -> Unit,
) {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            NavigationBar(
                windowInsets =
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                    )
            ) {
                for (navigationItem in listOf(HomePaneInfo, RulesPaneInfo, AboutPaneInfo)) {
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == navigationItem.route } ==
                            true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
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
        NavHost(
            navController = navController,
            startDestination = HomePaneInfo.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { fadeThroughEnter() },
            exitTransition = { fadeThroughExit() },
            popEnterTransition = { fadeThroughPopEnter() },
            popExitTransition = { fadeThroughPopExit() },
        ) {
            homePane(contentPadding)
            rulesPane(contentPadding, navigateToRuleScreen, navigateToAddRuleScreen)
            aboutPane(contentPadding, navigateToLicensesScreen)
        }
    }
}
