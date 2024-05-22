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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import me.zhanghai.android.untracker.R
import me.zhanghai.android.untracker.model.Rule
import me.zhanghai.android.untracker.util.plus

@Composable
fun RuleView(
    rule: Rule,
    onRuleChange: ((Rule) -> Unit),
    readOnly: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding + PaddingValues(bottom = 16.dp))
    ) {
        OutlinedTextField(
            value = rule.name,
            onValueChange = { onRuleChange(rule.copy(name = it)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            readOnly = readOnly,
            label = { Text(text = stringResource(R.string.rule_name)) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = rule.description,
            onValueChange = { onRuleChange(rule.copy(description = it)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            readOnly = readOnly,
            label = { Text(text = stringResource(R.string.rule_description)) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = rule.script,
            onValueChange = { onRuleChange(rule.copy(script = it)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            readOnly = readOnly,
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
            label = { Text(text = stringResource(R.string.rule_script)) },
            supportingText = {
                val supportingText =
                    AnnotatedString.fromHtml(
                        stringResource(R.string.rule_script_supporting_text),
                    )
                Text(
                    text = supportingText,
                    linkStyles =
                        TextDefaults.linkStyles(
                            linkStyle =
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.secondary,
                                    textDecoration = TextDecoration.Underline
                                )
                        )
                )
            }
        )
    }
}
