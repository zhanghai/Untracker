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

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import me.zhanghai.android.untracker.compat.removeLastCompat

abstract class Navigator<T : NavKey>(val backStack: NavBackStack<T>) {
    val currentKey: T
        get() = backStack.last()

    abstract fun navigateTo(key: T)

    fun navigateBack() {
        backStack.removeLastCompat()
    }
}

class ScreenNavigator<T : NavKey>(backStack: NavBackStack<T>) : Navigator<T>(backStack) {
    override fun navigateTo(key: T) {
        backStack.add(key)
    }
}

class PaneNavigator<T : NavKey>(backStack: NavBackStack<T>) : Navigator<T>(backStack) {
    override fun navigateTo(key: T) {
        if (backStack.last() != key) {
            while (backStack.size > 1) {
                backStack.removeLastCompat()
            }
            if (backStack.last() != key) {
                backStack.add(key)
            }
        }
    }
}
