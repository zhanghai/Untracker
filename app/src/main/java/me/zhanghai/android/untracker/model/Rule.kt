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

package me.zhanghai.android.untracker.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import java.util.UUID

@Immutable
@Serializable
data class Rule(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val script: String = "",
    val enabled: Boolean = true,
    val zhCNValue: I18nStrings? = null
)

@Serializable
data class I18nStrings(
    val localeName: String? = null,
    val localeDescription: String? = null,
)
