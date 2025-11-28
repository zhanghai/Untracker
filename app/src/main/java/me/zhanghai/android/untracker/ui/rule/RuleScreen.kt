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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.zhanghai.android.untracker.R
import me.zhanghai.android.untracker.model.Rule
import me.zhanghai.android.untracker.model.RuleList
import me.zhanghai.android.untracker.repository.RuleListRepository
import me.zhanghai.android.untracker.ui.component.Navigator
import me.zhanghai.android.untracker.ui.main.MainAppScreenKey
import me.zhanghai.android.untracker.util.Stateful
import me.zhanghai.android.untracker.util.stateInUi

@Serializable data class RuleScreenKey(val ruleId: String) : MainAppScreenKey

fun EntryProviderScope<MainAppScreenKey>.ruleScreenEntry(navigator: Navigator<MainAppScreenKey>) {
    entry<RuleScreenKey> { RuleScreen(it.ruleId, navigator) }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RuleScreen(ruleId: String, navigator: Navigator<MainAppScreenKey>) {
    val coroutineScope = rememberCoroutineScope()
    val ruleListsStatefulFlow =
        remember(coroutineScope) {
            @Suppress("USELESS_CAST")
            combine(
                    RuleListRepository.getBuiltinRuleListFlow(),
                    RuleListRepository.getCustomRuleListFlow(),
                ) { builtinRuleList, customRuleList ->
                    builtinRuleList to customRuleList
                }
                .map { Stateful.Success(it) as Stateful<Pair<RuleList, RuleList>> }
                .catch { emit(Stateful.Failure(null, it)) }
                .stateInUi(coroutineScope, Stateful.Loading(null))
        }
    val ruleListsStateful by ruleListsStatefulFlow.collectAsStateWithLifecycle()
    val (builtinRuleList, customRuleList) = ruleListsStateful.value ?: (null to null)
    val (rule, isBuiltin) =
        builtinRuleList?.rules?.find { it.id == ruleId }?.let { it to true }
            ?: customRuleList?.rules?.find { it.id == ruleId }?.let { it to false }
            ?: (null to true)
    val removeCustomRule: (String, CompletionHandler) -> Unit =
        { ruleIdToRemove, completionHandler ->
            val rules = customRuleList!!.rules
            val index = rules.indexOfFirst { it.id == ruleIdToRemove }
            if (index != -1) {
                val newRules = rules.toMutableList().apply { removeAt(index) }
                val newRuleList = customRuleList.copy(rules = newRules)
                coroutineScope
                    .launch { RuleListRepository.setCustomRuleList(newRuleList) }
                    .invokeOnCompletion(completionHandler)
            }
        }
    val setCustomRule: (Rule, CompletionHandler) -> Unit = { newRule, completionHandler ->
        val rules = customRuleList!!.rules
        val index = rules.indexOfFirst { it.id == newRule.id }
        if (index != -1) {
            val newRules = rules.toMutableList().apply { this[index] = newRule }
            val newRuleList = customRuleList.copy(rules = newRules)
            coroutineScope
                .launch { RuleListRepository.setCustomRuleList(newRuleList) }
                .invokeOnCompletion(completionHandler)
        }
    }
    var editingRule by remember { mutableStateOf<Rule?>(null) }
    val currentRule = editingRule ?: rule

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =
                            stringResource(
                                if (isBuiltin) R.string.rule_view else R.string.rule_edit
                            )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_up),
                        )
                    }
                },
                actions = {
                    if (!isBuiltin) {
                        IconButton(
                            onClick = {
                                removeCustomRule(ruleId) {
                                    // Do nothing to prevent navigating back twice due to rule not
                                    // found after reloading
                                    // navigator.navigateBack()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete),
                            )
                        }
                        IconButton(
                            onClick = {
                                @Suppress("NAME_SHADOWING") val editingRule = editingRule
                                if (editingRule != null) {
                                    setCustomRule(editingRule) { navigator.navigateBack() }
                                } else {
                                    navigator.navigateBack()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = stringResource(R.string.save),
                            )
                        }
                    }
                },
                windowInsets =
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { contentPadding ->
        if (currentRule != null) {
            RuleView(
                rule = currentRule,
                onRuleChange = { editingRule = it },
                readOnly = isBuiltin,
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
                when (@Suppress("NAME_SHADOWING") val ruleListsStateful = ruleListsStateful) {
                    is Stateful.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center).padding(16.dp)
                        )
                    }
                    is Stateful.Success -> {
                        LaunchedEffect(null) { navigator.navigateBack() }
                    }
                    is Stateful.Failure -> {
                        Text(
                            text = ruleListsStateful.throwable.toString(),
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}
