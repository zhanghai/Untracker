/*
 * Copyright 2025 Google LLC
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

package me.zhanghai.android.untracker.ui.component

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene as AndroidXOverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

// @see androidx.navigation3.scene.DialogScene.DialogScene
private class OverlayScene<T : Any>(
    override val key: Any,
    private val entry: NavEntry<T>,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
) : AndroidXOverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = { entry.Content() }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || this::class != other::class) {
            return false
        }
        other as OverlayScene<*>
        return key == other.key &&
            entry == other.entry &&
            previousEntries == other.previousEntries &&
            overlaidEntries == other.overlaidEntries
    }

    override fun hashCode(): Int =
        key.hashCode() * 31 +
            entry.hashCode() * 31 +
            previousEntries.hashCode() * 31 +
            overlaidEntries.hashCode() * 31

    override fun toString(): String =
        "OverlayContentScene(key=$key, entry=$entry, previousEntries=$previousEntries, overlaidEntries=$overlaidEntries"
}

// @see androidx.navigation3.scene.DialogSceneStrategy.DialogSceneStrategy
class OverlaySceneStrategy<T : Any>() : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.last()
        if (lastEntry.metadata[OVERLAY_KEY] != true) {
            return null
        }
        return OverlayScene(
            key = lastEntry.contentKey,
            entry = lastEntry,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
        )
    }

    companion object {
        fun overlay(): Map<String, Any> = OVERLAY_METADATA

        private const val OVERLAY_KEY = "overlay"

        private val OVERLAY_METADATA = mapOf(OVERLAY_KEY to true)
    }
}
