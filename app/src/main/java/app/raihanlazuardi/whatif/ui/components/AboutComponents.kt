package app.raihanlazuardi.whatif.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Laptop
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.raihanlazuardi.whatif.BuildConfig
import app.raihanlazuardi.whatif.data.model.OpenSourceLibraries
import app.raihanlazuardi.whatif.data.model.OpenSourceLibrary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutDialog(
    onDismissRequest: () -> Unit,
    onOpenLicenses: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column {
                    Text(
                        text = "WhatIF",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Versi ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Aplikasi pelacak Intermittent Fasting modern dan pencatat mokel dengan prinsip 100% offline-first dan privasi lokal tanpa analitik pihak ketiga.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Feature Badges
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Jetpack Compose", fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        )
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text("M3 Expressive", fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        )
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Material You", fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    )
                }

                // Developer & Build Host Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "INFORMASI PENGEMBANG",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Developer Row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Pengembang",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = BuildConfig.DEVELOPER_NAME,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Host PC Row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Laptop,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Host PC Pengembang",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = BuildConfig.BUILD_HOST,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Privacy Row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Security,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Penyimpanan & Keamanan",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "100% Offline Lokal (JSON Internal)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onOpenLicenses,
                shape = RoundedCornerShape(percent = 50)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Code,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Source Licenses")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun OpenSourceLicensesDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Code,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Column {
                    Text(
                        text = "Open Source Licenses",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${OpenSourceLibraries.list.size} Pustaka Pihak Ketiga Digunakan",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(OpenSourceLibraries.list, key = { it.name }) { lib ->
                    LibraryLicenseItem(library = lib)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                shape = RoundedCornerShape(percent = 50)
            ) {
                Text("Tutup")
            }
        }
    )
}

@Composable
private fun LibraryLicenseItem(
    library: OpenSourceLibrary,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = library.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${library.author} • ${library.version}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = library.licenseName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = library.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Sembunyikan detail lisensi" else "Lihat teks lisensi",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )

                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            text = library.licenseSummary,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}
