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

package me.zhanghai.android.untracker.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.zhanghai.android.untracker.BuiltinRuleList
import me.zhanghai.android.untracker.application
import me.zhanghai.android.untracker.model.RuleList
import me.zhanghai.android.untracker.util.getObject
import me.zhanghai.android.untracker.util.getObjectFlow
import me.zhanghai.android.untracker.util.setObject

object RuleListRepository {
    private val sharedPreferences =
        application.getSharedPreferences("rule-list", Context.MODE_PRIVATE)

    private const val KEY_BUILTIN = "builtin"
    private const val KEY_CUSTOM = "custom"

    suspend fun getBuiltinRuleList(): RuleList =
        withContext(Dispatchers.IO) {
            getBuiltinRuleList(sharedPreferences.getObject(KEY_BUILTIN) ?: RuleList())
        }

    fun getBuiltinRuleListFlow(): Flow<RuleList> =
        sharedPreferences
            .getObjectFlow(KEY_BUILTIN, RuleList())
            .map { getBuiltinRuleList(it) }
            .flowOn(Dispatchers.IO)

    private fun getBuiltinRuleList(storedRuleList: RuleList): RuleList {
        val storedRuleMap = storedRuleList.rules.associateBy { it.id }
        return RuleList(
            rules =
                BuiltinRuleList.rules.map { rule ->
                    val storedRule = storedRuleMap[rule.id] ?: return@map rule
                    rule.copy(enabled = storedRule.enabled)
                }
        )
    }

    suspend fun setBuiltinRuleList(rules: RuleList) {
        withContext(Dispatchers.IO) { sharedPreferences.setObject(KEY_BUILTIN, rules) }
    }

    suspend fun getCustomRuleList(): RuleList =
        withContext(Dispatchers.IO) { sharedPreferences.getObject(KEY_CUSTOM) ?: RuleList() }

    fun getCustomRuleListFlow(): Flow<RuleList> =
        sharedPreferences.getObjectFlow(KEY_CUSTOM, RuleList()).flowOn(Dispatchers.IO)

    suspend fun setCustomRuleList(rules: RuleList) {
        withContext(Dispatchers.IO) { sharedPreferences.setObject(KEY_CUSTOM, rules) }
    }

    suspend fun getAllRuleList(): RuleList =
        RuleList(rules = getBuiltinRuleList().rules + getCustomRuleList().rules)
}
