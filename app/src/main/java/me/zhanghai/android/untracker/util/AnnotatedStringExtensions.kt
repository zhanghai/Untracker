package me.zhanghai.android.untracker.util

import android.text.style.ClickableSpan
import android.text.style.URLSpan
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans

// Currently only supports ClickableSpan.
@Composable
@OptIn(ExperimentalTextApi::class)
fun String.htmlToAnnotatedString() = buildAnnotatedString {
    val spanned = HtmlCompat.fromHtml(this@htmlToAnnotatedString, HtmlCompat.FROM_HTML_MODE_LEGACY)
    append(spanned.toString())
    for (span in spanned.getSpans<Any>()) {
        val start = spanned.getSpanStart(span)
        val end = spanned.getSpanEnd(span)
        when (span) {
            is ClickableSpan -> {
                addStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        textDecoration = TextDecoration.Underline
                    ),
                    start,
                    end
                )
                if (span is URLSpan) {
                    addUrlAnnotation(UrlAnnotation(span.url), start, end)
                }
            }
        }
    }
}
