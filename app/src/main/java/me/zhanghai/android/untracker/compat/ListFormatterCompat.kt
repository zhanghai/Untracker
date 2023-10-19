package me.zhanghai.android.untracker.compat

import android.icu.text.ListFormatter
import android.os.Build

class ListFormatterCompat {
    fun format(vararg items: Any?): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ListFormatter.getInstance().format(*items)
        } else {
            formatCompat(items.asList())
        }

    fun format(items: Collection<*>): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ListFormatter.getInstance().format(items)
        } else {
            formatCompat(items)
        }

    private fun formatCompat(items: Collection<*>): String = items.joinToString(", ")

    companion object {
        fun getInstance(): ListFormatterCompat = ListFormatterCompat()
    }
}
