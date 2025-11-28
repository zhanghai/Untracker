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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import me.zhanghai.android.untracker.ui.component.ScreenNavigator
import me.zhanghai.android.untracker.ui.license.licensesScreenEntry
import me.zhanghai.android.untracker.ui.rule.addRuleScreenEntry
import me.zhanghai.android.untracker.ui.rule.ruleScreenEntry
import me.zhanghai.android.untracker.util.sharedAxisXPopTransitionSpec
import me.zhanghai.android.untracker.util.sharedAxisXTransitionSpec

interface MainAppScreenKey : NavKey

@Composable
fun MainApp() {
    @Suppress("UNCHECKED_CAST")
    val backStack = rememberNavBackStack(MainScreenKey) as NavBackStack<MainAppScreenKey>
    val navigator = remember { ScreenNavigator(backStack) }
    NavDisplay(
        backStack = backStack,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = { sharedAxisXTransitionSpec() },
        popTransitionSpec = { sharedAxisXPopTransitionSpec() },
        predictivePopTransitionSpec = { sharedAxisXPopTransitionSpec() },
        entryProvider =
            entryProvider {
                mainScreenEntry(navigator)
                ruleScreenEntry(navigator)
                addRuleScreenEntry(navigator)
                licensesScreenEntry(navigator)
            },
    )
}
