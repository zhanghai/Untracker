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

package me.zhanghai.android.untracker.ui.license

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.EntryProviderScope
import com.mikepenz.aboutlibraries.entity.Library
import kotlinx.serialization.Serializable
import me.zhanghai.android.untracker.ui.component.Navigator
import me.zhanghai.android.untracker.ui.component.OverlaySceneStrategy
import me.zhanghai.android.untracker.ui.main.MainAppScreenKey

@Serializable data class LibraryDialogKey(val library: Library) : MainAppScreenKey

fun EntryProviderScope<MainAppScreenKey>.libraryDialogEntry(
    navigator: Navigator<MainAppScreenKey>
) {
    entry<LibraryDialogKey>(metadata = OverlaySceneStrategy.overlay()) {
        LibraryDialog(it.library, navigator)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LibraryDialog(library: Library, navigator: Navigator<MainAppScreenKey>) {
    AlertDialog(
        onDismissRequest = { navigator.navigateBack() },
        confirmButton = {
            TextButton(onClick = { navigator.navigateBack() }) {
                Text(text = stringResource(android.R.string.ok))
            }
        },
        title = { Text(text = library.name) },
        text =
            library.licenses.firstOrNull()?.licenseContent?.let {
                { Text(text = it, modifier = Modifier.verticalScroll(rememberScrollState())) }
            },
    )
}
