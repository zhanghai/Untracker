package me.zhanghai.android.untracker.util

import android.content.ClipData
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry

suspend fun Clipboard.setText(text: String) {
    setClipEntry(ClipData.newPlainText(null, text).toClipEntry())
}
