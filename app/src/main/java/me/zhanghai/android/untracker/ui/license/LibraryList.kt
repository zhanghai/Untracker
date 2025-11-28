package me.zhanghai.android.untracker.ui.license

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.zhanghai.android.untracker.ui.component.Navigator
import me.zhanghai.android.untracker.ui.main.MainAppScreenKey
import me.zhanghai.compose.preference.Preference

@Composable
fun LibraryList(
    libraries: StableLibraries,
    navigator: Navigator<MainAppScreenKey>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        items(libraries.libraries, { it.library.uniqueId }) { library ->
            LibraryItem(
                library = library,
                navigator = navigator,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun LibraryItem(
    library: StableLibrary,
    navigator: Navigator<MainAppScreenKey>,
    modifier: Modifier = Modifier,
) {
    Preference(
        title = { Text(text = library.library.name) },
        modifier = modifier,
        summary = library.library.authorName?.let { { Text(text = it) } },
    ) {
        navigator.navigateTo(LibraryDialogKey(library.library))
    }
}
