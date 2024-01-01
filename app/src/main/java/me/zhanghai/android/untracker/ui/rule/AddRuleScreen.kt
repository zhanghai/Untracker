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

package me.zhanghai.android.untracker.ui.rule

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.launch
import me.zhanghai.android.untracker.R
import me.zhanghai.android.untracker.model.Rule
import me.zhanghai.android.untracker.repository.RuleListRepository

fun NavGraphBuilder.addRuleScreen(onPopBackStack: () -> Unit) {
    composable("addRule") { AddRuleScreen(onPopBackStack = onPopBackStack) }
}

fun NavController.navigateToAddRuleScreen() {
    navigate("addRule")
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddRuleScreen(onPopBackStack: () -> Unit) {
    var rule by remember { mutableStateOf(Rule()) }
    val coroutineScope = rememberCoroutineScope()
    val addRule: (Rule, CompletionHandler) -> Unit = { newRule, completionHandler ->
        coroutineScope
            .launch {
                val ruleList = RuleListRepository.getCustomRuleList()
                val newRules = ruleList.rules + newRule
                val newRuleList = ruleList.copy(rules = newRules)
                RuleListRepository.setCustomRuleList(newRuleList)
            }
            .invokeOnCompletion(completionHandler)
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.rule_add)) },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = onPopBackStack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_up)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { addRule(rule) { onPopBackStack() } }) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = stringResource(R.string.save)
                        )
                    }
                },
                windowInsets =
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                    ),
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { contentPadding ->
        RuleView(
            rule = rule,
            onRuleChange = { rule = it },
            readOnly = false,
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding
        )
    }
}
