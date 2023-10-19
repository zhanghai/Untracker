package me.zhanghai.android.untracker.ui.license

import androidx.compose.runtime.Immutable
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Library
import me.zhanghai.android.untracker.compat.ListFormatterCompat

val Library.authorName: String?
    get() =
        if (developers.isNotEmpty()) {
            ListFormatterCompat.getInstance().format(developers.map(Developer::name))
        } else {
            organization?.name
        }

fun Library.toStable(): StableLibrary = StableLibrary(this)

@Immutable data class StableLibrary(val library: Library)

fun List<Library>.toStable(): StableLibraries = StableLibraries(map(Library::toStable))

@Immutable data class StableLibraries(val libraries: List<StableLibrary>)
