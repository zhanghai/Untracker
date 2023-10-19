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

package me.zhanghai.android.untracker.util

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> SharedPreferences.getObject(key: String): T? {
    val stringValue = getString(key, null) ?: return null
    return Json.decodeFromString(stringValue)
}

inline fun <reified T> SharedPreferences.setObject(key: String, value: T?) {
    val stringValue = value?.let { Json.encodeToString(it) }
    edit { putString(key, stringValue) }
}

inline fun <reified T> SharedPreferences.getObjectFlow(key: String, defaultValue: T): Flow<T> =
    getStringFlow(key, null).map { if (it != null) Json.decodeFromString(it) else defaultValue }

fun SharedPreferences.getStringFlow(key: String, defaultValue: String?): Flow<String?> =
    callbackFlow {
        val listener = OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key || changedKey == null) {
                trySend(getString(key, defaultValue))
            }
        }
        registerOnSharedPreferenceChangeListener(listener)
        listener.onSharedPreferenceChanged(this@getStringFlow, null)
        awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
    }
