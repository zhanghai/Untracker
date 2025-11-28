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

package me.zhanghai.android.untracker.ui.license

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import me.zhanghai.android.untracker.R
import me.zhanghai.android.untracker.ui.component.Navigator
import me.zhanghai.android.untracker.ui.main.MainAppScreenKey
import me.zhanghai.android.untracker.util.Stateful

@Serializable data object LicensesScreenKey : MainAppScreenKey

fun EntryProviderScope<MainAppScreenKey>.licensesScreenEntry(
    navigator: Navigator<MainAppScreenKey>
) {
    entry<LicensesScreenKey> { LicensesScreen(navigator) }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LicensesScreen(navigator: Navigator<MainAppScreenKey>) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.licenses_title)) },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_up),
                        )
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
        val context = LocalContext.current
        val librariesStateful by
            produceState<Stateful<StableLibraries>>(Stateful.Loading(null)) {
                val libraries =
                    withContext(Dispatchers.IO) {
                        Libs.Builder().withContext(context).build().libraries.toStable()
                    }
                value = Stateful.Success(libraries)
            }
        val libraries = librariesStateful.value
        if (libraries != null) {
            LibraryList(
                libraries = libraries,
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
                when (librariesStateful) {
                    is Stateful.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center).padding(16.dp)
                        )
                    }
                    else -> error(librariesStateful)
                }
            }
        }
    }
}
