package me.zhanghai.android.untracker.ui.license

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Library

@Composable
fun LibraryList(
    libraries: StableLibraries,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        items(libraries.libraries, { it.library.uniqueId }) { library ->
            LibraryItem(library = library)
            HorizontalDivider(thickness = 1.dp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LibraryItem(library: StableLibrary) {
    val version = library.library.artifactVersion
    var openDialog by rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = Modifier.clickable(onClick = {
            if (library.library.licenses.isNotEmpty() || library.library.website != null) {
                openDialog = true
            }
        })
    ) {
        Box(modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 16.dp)) {
            Column {
                RenderName(library.library.name)
                RenderUniqueID(library.library.uniqueId)
                RenderDevelopers(library.library.developers)
                RenderDescription(library.library.description)
                RenderVersionAndLicenses(library.library, version)
            }
        }
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

@Composable
private fun RenderName(name: String) {
    return Text(
        modifier = Modifier.padding(0.dp, 2.dp, 0.dp, 0.dp),
        text = name, style = typography.titleMedium
    )
}

@Composable
private fun RenderUniqueID(uniqueID: String) {
    return Text(
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 2.dp),
        text = uniqueID, style = typography.labelSmall
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RenderDevelopers(developers: List<Developer>) {
    if (!developers.isEmpty()) {
        val thisContext = LocalContext.current
        FlowRow(
            modifier = Modifier
                .padding(0.dp, 2.dp, 0.dp, 2.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            val colorScheme = MaterialTheme.colorScheme
            developers.withIndex().forEach { (index, developer) ->
                if (!developer.organisationUrl.isNullOrEmpty()) Text(
                    text = developer.name.toString(),
                    style = typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { thisContext.openUrlWithChromeCustomTab(developer.organisationUrl.toString()) }),
                    color = colorScheme.primary
                ) else Text(
                    text = developer.name.toString(),
                    style = typography.bodyMedium,
                )
                if (index < developers.size - 1) Text(
                    text = ", ", style = typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun RenderDescription(description: String?) {
    if (!description.isNullOrBlank()) Text(
        text = description,
        style = typography.bodySmall,
        modifier = Modifier.padding(0.dp, 2.dp, 0.dp, 2.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RenderVersionAndLicenses(library: Library, version: String?) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = if (version.toString().first()
                        .isDigit()
                ) "v${version.toString()}" else version.toString(),
                modifier = Modifier.padding(8.dp, 3.dp, 8.dp, 3.dp),
                style = typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
        if (library.licenses.isNotEmpty()) library.licenses.forEach {
            Card(
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ), shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    modifier = Modifier.padding(8.dp, 3.dp, 8.dp, 3.dp),
                    text = it.name,
                    style = typography.labelSmall
                )
            }
        }
    }
}

private fun Context.openUrlWithChromeCustomTab(url: String){
    val intent = CustomTabsIntent.Builder().build()
    intent.launchUrl(this, url.toUri())
}
