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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import me.zhanghai.android.untracker.R
import me.zhanghai.android.untracker.model.Rule
import me.zhanghai.android.untracker.model.RuleList
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.TwoTargetSwitchPreference

@Composable
fun RuleList(
    builtinRuleList: RuleList,
    onBuiltinRuleListChange: (RuleList) -> Unit,
    customRuleList: RuleList,
    onCustomRuleListChange: (RuleList) -> Unit,
    onRuleClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        item(key = "BuiltinRuleCategory", contentType = RuleListContentType.CATEGORY) {
            RuleCategory(
                title = stringResource(R.string.main_rules_builtin),
                modifier = Modifier.fillMaxWidth()
            )
        }
        items(builtinRuleList.rules, { it.id }, { RuleListContentType.ITEM }) { rule ->
            RuleItem(
                rule = rule,
                onRuleChange = { newRule ->
                    onBuiltinRuleListChange(
                        builtinRuleList.copy(
                            rules =
                                builtinRuleList.rules.map { oldRule ->
                                    if (oldRule.id == newRule.id) newRule else oldRule
                                }
                        )
                    )
                },
                onRuleClick = onRuleClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (customRuleList.rules.isNotEmpty()) {
            item(key = "CustomRuleCategory", contentType = RuleListContentType.CATEGORY) {
                RuleCategory(
                    title = stringResource(R.string.main_rules_custom),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(customRuleList.rules, { it.id }, { RuleListContentType.ITEM }) { rule ->
                RuleItem(
                    rule = rule,
                    onRuleChange = { newRule ->
                        onCustomRuleListChange(
                            customRuleList.copy(
                                rules =
                                    customRuleList.rules.map { oldRule ->
                                        if (oldRule.id == newRule.id) newRule else oldRule
                                    }
                            )
                        )
                    },
                    onRuleClick = onRuleClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private enum class RuleListContentType {
    CATEGORY,
    ITEM
}

@Composable
private fun RuleCategory(title: String, modifier: Modifier = Modifier) {
    PreferenceCategory(title = { Text(text = title) }, modifier = modifier)
}

@Composable
private fun RuleItem(
    rule: Rule,
    onRuleChange: (Rule) -> Unit,
    onRuleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TwoTargetSwitchPreference(
        value = rule.enabled,
        onValueChange = { onRuleChange(rule.copy(enabled = it)) },
        title = { Text(text = rule.name) },
        modifier = modifier,
        summary = { Text(text = rule.description) }
    ) {
        onRuleClick(rule.id)
    }
}
