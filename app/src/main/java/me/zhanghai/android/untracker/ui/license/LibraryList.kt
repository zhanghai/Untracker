package me.zhanghai.android.untracker.ui.license

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import me.zhanghai.compose.preference.Preference

@Composable
fun LibraryList(
    libraries: StableLibraries,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        items(libraries.libraries, { it.library.uniqueId }) { library ->
            LibraryItem(library = library, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun LibraryItem(library: StableLibrary, modifier: Modifier = Modifier) {
    var openDialog by rememberSaveable { mutableStateOf(false) }
    Preference(
        title = { Text(text = library.library.name) },
        modifier = modifier,
        summary = library.library.authorName?.let { { Text(text = it) } },
    ) {
        openDialog = true
    }
    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            confirmButton = {
                TextButton(onClick = { openDialog = false }) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
            title = { Text(text = library.library.name) },
            text =
                library.library.licenses.firstOrNull()?.licenseContent?.let {
                    { Text(text = it, modifier = Modifier.verticalScroll(rememberScrollState())) }
                },
        )
    }
}
