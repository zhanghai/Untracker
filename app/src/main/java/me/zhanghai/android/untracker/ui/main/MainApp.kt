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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.zhanghai.android.untracker.ui.license.licensesScreen
import me.zhanghai.android.untracker.ui.license.navigateToLicensesScreen
import me.zhanghai.android.untracker.ui.rule.addRuleScreen
import me.zhanghai.android.untracker.ui.rule.navigateToAddRuleScreen
import me.zhanghai.android.untracker.ui.rule.navigateToRuleScreen
import me.zhanghai.android.untracker.ui.rule.ruleScreen
import me.zhanghai.android.untracker.util.activityEnter
import me.zhanghai.android.untracker.util.activityExit
import me.zhanghai.android.untracker.util.activityPopEnter
import me.zhanghai.android.untracker.util.activityPopExit

@Composable
fun MainApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MainScreenRoute,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { activityEnter() },
        exitTransition = { activityExit() },
        popEnterTransition = { activityPopEnter() },
        popExitTransition = { activityPopExit() }
    ) {
        mainScreen(
            { navController.navigateToRuleScreen(it) },
            navController::navigateToAddRuleScreen,
            navController::navigateToLicensesScreen
        )
        ruleScreen(navController::popBackStack)
        addRuleScreen(navController::popBackStack)
        licensesScreen(navController::popBackStack)
    }
}
