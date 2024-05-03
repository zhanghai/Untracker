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

package me.zhanghai.android.untracker.ui.text

import android.app.Activity
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.zhanghai.android.untracker.R
import me.zhanghai.android.untracker.Untracker
import me.zhanghai.android.untracker.ui.component.UndecoratedTextField
import me.zhanghai.android.untracker.util.Stateful

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TextBottomSheet(
    text: String,
    onShareText: (String) -> Unit,
    onSetProcessedText: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val context = LocalContext.current
        val darkTheme = isSystemInDarkTheme()
        SideEffect {
            val window = (context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val dismiss: () -> Unit = {
        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
    }
    val untrackedTextStatefulFlow =
        remember(text, coroutineScope) {
            flow<Stateful<String>> {
                    val untrackedText =
                        try {
                            Untracker.untrack(text)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            emit(Stateful.Failure(null, e))
                            return@flow
                        }
                    emit(Stateful.Success(untrackedText))
                }
                .flowOn(Dispatchers.IO)
                .stateIn(coroutineScope, SharingStarted.Eagerly, Stateful.Loading(null))
        }
    val untrackedTextStateful by untrackedTextStatefulFlow.collectAsStateWithLifecycle()
    var modifiedText by rememberSaveable { mutableStateOf<String?>(null) }
    val currentText = modifiedText ?: untrackedTextStateful.value
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column {
            Text(
                text = stringResource(R.string.text_original),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            SelectionContainer(
                modifier =
                    Modifier.fillMaxWidth()
                        .weight(weight = 1f, fill = false)
                        .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = text,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.text_untracked),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .weight(weight = 1f, fill = false)
                        .verticalScroll(rememberScrollState())
            ) {
                if (currentText != null) {
                    UndecoratedTextField(
                        value = currentText,
                        onValueChange = { modifiedText = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        textStyle = MaterialTheme.typography.headlineSmall
                    )
                } else {
                    val placeholderText =
                        when (
                            @Suppress("NAME_SHADOWING")
                            val untrackedTextStateful = untrackedTextStateful
                        ) {
                            is Stateful.Loading -> stringResource(R.string.text_processing)
                            is Stateful.Failure ->
                                stringResource(
                                    R.string.text_error_format,
                                    untrackedTextStateful.throwable.toString()
                                )
                            else -> error(untrackedTextStateful)
                        }
                    Text(
                        text = placeholderText,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 40.dp) {
                    if (currentText != null) {
                        val clipboardManager = LocalClipboardManager.current
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(currentText))
                                dismiss()
                            },
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.copy))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = {
                                onShareText(currentText)
                                dismiss()
                            },
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.share))
                        }
                        if (onSetProcessedText != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    onSetProcessedText(currentText)
                                    dismiss()
                                },
                                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                                Text(stringResource(R.string.confirm))
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = dismiss,
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.cancel))
                        }
                    }
                }
            }
        }
    }
}
