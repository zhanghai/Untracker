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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.zhanghai.android.untracker.R
import me.zhanghai.android.untracker.model.RuleList
import me.zhanghai.android.untracker.repository.RuleListRepository
import me.zhanghai.android.untracker.ui.component.NavigationItemInfo
import me.zhanghai.android.untracker.ui.component.TopAppBarContainer
import me.zhanghai.android.untracker.util.Stateful
import me.zhanghai.android.untracker.util.asInsets
import me.zhanghai.android.untracker.util.copy
import me.zhanghai.android.untracker.util.stateInUi

val RulesPaneInfo: NavigationItemInfo =
    NavigationItemInfo(
        route = "rules",
        iconResourceId = R.drawable.rules_icon_animated_24dp,
        labelResourceId = R.string.main_rules,
    )

fun NavGraphBuilder.rulesPane(
    contentPadding: PaddingValues,
    navigateToRuleScreen: (String) -> Unit,
    navigateToAddRuleScreen: () -> Unit,
) {
    composable(RulesPaneInfo.route) {
        RulesPane(
            contentPadding = contentPadding,
            navigateToRuleScreen = navigateToRuleScreen,
            navigateToAddRuleScreen = navigateToAddRuleScreen,
        )
    }
}

private val tabTextResourceIds = listOf(R.string.main_rules_builtin, R.string.main_rules_custom)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RulesPane(
    contentPadding: PaddingValues,
    navigateToRuleScreen: (String) -> Unit,
    navigateToAddRuleScreen: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val builtinRuleListStatefulFlow =
        remember(coroutineScope) {
            @Suppress("USELESS_CAST")
            RuleListRepository.getBuiltinRuleListFlow()
                .map { Stateful.Success(it) as Stateful<RuleList> }
                .catch { emit(Stateful.Failure(null, it)) }
                .stateInUi(coroutineScope, Stateful.Loading<RuleList>(null))
        }
    val builtinRuleListStateful by builtinRuleListStatefulFlow.collectAsStateWithLifecycle()
    val setBuiltinRuleList: (RuleList) -> Unit = { ruleList ->
        coroutineScope.launch { RuleListRepository.setBuiltinRuleList(ruleList) }
    }
    val customRuleListStatefulFlow =
        remember(coroutineScope) {
            @Suppress("USELESS_CAST")
            RuleListRepository.getCustomRuleListFlow()
                .map { Stateful.Success(it) as Stateful<RuleList> }
                .catch { emit(Stateful.Failure(null, it)) }
                .stateInUi(coroutineScope, Stateful.Loading<RuleList>(null))
        }
    val customRuleListStateful by customRuleListStatefulFlow.collectAsStateWithLifecycle()
    val setCustomRuleList: (RuleList) -> Unit = { ruleList ->
        coroutineScope.launch { RuleListRepository.setCustomRuleList(ruleList) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState { tabTextResourceIds.size }
        val scrollBehaviors =
            List(tabTextResourceIds.size) { TopAppBarDefaults.pinnedScrollBehavior() }
        TopAppBarContainer(scrollBehavior = scrollBehaviors[pagerState.currentPage]) {
            Column {
                TopAppBar(
                    title = { Text(text = stringResource(RulesPaneInfo.labelResourceId)) },
                    modifier = Modifier.fillMaxWidth(),
                    actions = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(1)
                                    navigateToAddRuleScreen()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = stringResource(R.string.add),
                            )
                        }
                    },
                    windowInsets = contentPadding.copy(bottom = 0.dp).asInsets(),
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                )
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color.Unspecified,
                    contentColor = LocalContentColor.current,
                    divider = {},
                ) {
                    tabTextResourceIds.forEachIndexed { index, textResourceId ->
                        Tab(
                            selected = index == pagerState.currentPage,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            },
                            text = {
                                Text(
                                    text = stringResource(textResourceId),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            selectedContentColor = TabRowDefaults.primaryContentColor,
                            unselectedContentColor = LocalContentColor.current,
                        )
                    }
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1,
        ) { page ->
            val (ruleListStateful, onRuleListChange) =
                when (page) {
                    0 -> builtinRuleListStateful to setBuiltinRuleList
                    1 -> customRuleListStateful to setCustomRuleList
                    else -> error(page)
                }
            RulesTab(
                ruleListStateful = ruleListStateful,
                onRuleListChange = onRuleListChange,
                onRuleClick = { navigateToRuleScreen(it) },
                contentPadding = contentPadding.copy(top = 0.dp),
                scrollBehavior = scrollBehaviors[page],
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun RulesTab(
    ruleListStateful: Stateful<RuleList>,
    onRuleListChange: (RuleList) -> Unit,
    onRuleClick: (String) -> Unit,
    contentPadding: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    Column(modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection)) {
        val ruleList = ruleListStateful.value
        if (ruleList?.rules?.isNotEmpty() == true) {
            RuleList(
                ruleList = ruleList,
                onRuleListChange = onRuleListChange,
                onRuleClick = onRuleClick,
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
                when (ruleListStateful) {
                    is Stateful.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center).padding(16.dp)
                        )
                    }
                    else -> {
                        Text(
                            text =
                                if (ruleListStateful is Stateful.Failure) {
                                    ruleListStateful.throwable.toString()
                                } else {
                                    stringResource(R.string.main_rules_empty)
                                },
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
