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

package me.zhanghai.android.untracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import me.zhanghai.android.untracker.ui.content.UntrackerApp
import me.zhanghai.android.untracker.ui.text.TextBottomSheet

class TextActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val text: String?
        val onShareText: (String) -> Unit
        val onSetProcessedText: ((String) -> Unit)?
        @SuppressLint("InlinedApi")
        when (intent.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
                onShareText = { shareText(it) }
                val readOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
                onSetProcessedText =
                    if (!readOnly) {
                        { setResult(RESULT_OK, Intent().putExtra(Intent.EXTRA_PROCESS_TEXT, it)) }
                    } else {
                        null
                    }
            }
            Intent.ACTION_SEND -> {
                text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
                onShareText = { shareText(it, intent) }
                onSetProcessedText = null
            }
            else -> {
                text = null
                onSetProcessedText = null
                onShareText = {}
            }
        }
        if (text == null) {
            finish()
            return
        }

        setContent {
            UntrackerApp {
                TextBottomSheet(
                    text = text,
                    onShareText = onShareText,
                    onSetProcessedText = onSetProcessedText,
                    onDismiss = this::finish
                )
            }
        }
    }

    private fun shareText(text: String, originalIntent: Intent? = null) {
        startActivity(
            Intent.createChooser(
                    Intent(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .apply {
                            if (originalIntent != null) {
                                putExtras(originalIntent)
                            }
                        }
                        .putExtra(Intent.EXTRA_TEXT, text),
                    null
                )
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && originalIntent != null) {
                        putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, arrayOf(originalIntent.component))
                    }
                }
        )
    }
}
