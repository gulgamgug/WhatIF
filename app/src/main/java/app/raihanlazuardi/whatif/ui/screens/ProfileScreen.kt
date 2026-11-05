package app.raihanlazuardi.whatif.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.raihanlazuardi.whatif.BuildConfig
import app.raihanlazuardi.whatif.data.model.UserProfile
import app.raihanlazuardi.whatif.data.model.WhatIFData
import app.raihanlazuardi.whatif.ui.components.AboutDialog
import app.raihanlazuardi.whatif.ui.components.OpenSourceLicensesDialog
import app.raihanlazuardi.whatif.ui.theme.Coral500

@Composable
fun ProfileScreen(
    appData: WhatIFData,
    onUpdateName: (String) -> Unit,
    onToggleDynamicColor: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onSendTestNotification: () -> Unit,
    onExportJson: () -> String,
    onImportJson: (String) -> Result<Unit>,
    onResetData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showExportPreviewDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }

    // Notification permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onToggleNotifications(true)
            Toast.makeText(context, "Izin notifikasi diberikan! Pengingat aktif.", Toast.LENGTH_SHORT).show()
        } else {
            onToggleNotifications(false)
            Toast.makeText(context, "Izin notifikasi ditolak.", Toast.LENGTH_SHORT).show()
        }
    }

    // SAF Create Document launcher for file export
    val exportFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    stream.write(onExportJson().toByteArray())
                }
                Toast.makeText(context, "Database berhasil diekspor ke file!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal mengekspor: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // SAF Open Document launcher for file import
    val importFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val jsonString = context.contentResolver.openInputStream(it)?.bufferedReader().use { reader ->
                    reader?.readText()
                } ?: ""
                val result = onImportJson(jsonString)
                if (result.isSuccess) {
                    Toast.makeText(context, "Database berhasil dipulihkan! 🎉", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Format file tidak valid!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membaca file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Profile Hero Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = appData.profile.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Target: ${appData.schedule.fastingDurationHours.toInt()} Jam IF Sehari",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { showEditNameDialog = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Nama",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Appearance & Dynamic Theming Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ColorLens,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Material You Dynamic Theme",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Gunakan palet warna wallpaper sistem Android 12+",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = appData.profile.useDynamicColor,
                        onCheckedChange = onToggleDynamicColor
                    )
                }
            }
        }

        // Notification Settings Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (appData.profile.notificationsEnabled) Icons.Rounded.NotificationsActive else Icons.Rounded.NotificationsNone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Notifikasi Pengingat Puasa",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Pengingat saat puasa mulai & jendela makan dibuka",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = appData.profile.notificationsEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    onToggleNotifications(enabled)
                                }
                            }
                        )
                    }

                    if (appData.profile.notificationsEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = onSendTestNotification,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(percent = 50)
                        ) {
                            Icon(imageVector = Icons.Rounded.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kirim Notifikasi Tes Sekarang")
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                        data = Uri.parse("package:${context.packageName}")
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    try {
                                        val intent = Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Buka Pengaturan > Aplikasi > WhatIF > Baterai (Pilih 'Tidak Dibatasi')", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(percent = 50)
                        ) {
                            Icon(imageVector = Icons.Rounded.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Izin Latar Belakang (OneUI / HyperOS)")
                        }
                    }
                }
            }
        }

        // Database Backup & Restore Section Header
        item {
            Text(
                text = "Pengelolaan Database Lokal",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Export Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FileDownload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Ekspor Database Lokal",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Cadangkan semua riwayat mokel, profil, dan jadwal ke file JSON",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                exportFileLauncher.launch("whatif_backup_${System.currentTimeMillis()}.json")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(percent = 50)
                        ) {
                            Text("Simpan File JSON")
                        }

                        FilledTonalButton(
                            onClick = {
                                exportedJsonText = onExportJson()
                                showExportPreviewDialog = true
                            },
                            shape = RoundedCornerShape(percent = 50)
                        ) {
                            Icon(imageVector = Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Salin")
                        }
                    }
                }
            }
        }

        // Import Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FileUpload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Impor Database Lokal",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pulihkan data dari cadangan file JSON yang telah disimpan",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                importFileLauncher.launch(arrayOf("application/json", "text/*"))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(percent = 50)
                        ) {
                            Text("Buka File JSON")
                        }

                        FilledTonalButton(
                            onClick = { showImportDialog = true },
                            shape = RoundedCornerShape(percent = 50)
                        ) {
                            Text("Tempel Teks")
                        }
                    }
                }
            }
        }

        // App Info & Legal Section Header
        item {
            Text(
                text = "Informasi & Lisensi",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // About Card
        item {
            Card(
                onClick = { showAboutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Tentang WhatIF",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pengembang: ${BuildConfig.DEVELOPER_NAME} @ ${BuildConfig.BUILD_HOST}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = "Buka Tentang WhatIF",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Open Source Licenses Card
        item {
            Card(
                onClick = { showLicensesDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
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
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Open Source Licenses",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pustaka sumber terbuka & lisensi pihak ketiga",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = "Buka Open Source Licenses",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Danger Zone: Reset
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteForever,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Reset Seluruh Data",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Hapus semua jadwal dan catatan mokel",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    TextButton(onClick = { showResetDialog = true }) {
                        Text("Reset", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Dialog Edit Nama
    if (showEditNameDialog) {
        var newName by remember { mutableStateOf(appData.profile.name) }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Atur Nama Pengguna", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nama") },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onUpdateName(newName.trim())
                            showEditNameDialog = false
                        }
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog Export Preview & Copy
    if (showExportPreviewDialog) {
        AlertDialog(
            onDismissRequest = { showExportPreviewDialog = false },
            title = { Text("Cadangan JSON Database", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Data database lokal Anda siap disalin:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = exportedJsonText.take(300) + if (exportedJsonText.length > 300) "\n..." else "",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("WhatIF Backup", exportedJsonText))
                        Toast.makeText(context, "Data berhasil disalin ke papan klip!", Toast.LENGTH_SHORT).show()
                        showExportPreviewDialog = false
                    }
                ) {
                    Text("Salin ke Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportPreviewDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Dialog Import Text
    if (showImportDialog) {
        var jsonInput by remember { mutableStateOf("") }
        var inputError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Tempel JSON Cadangan", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Tempel teks cadangan JSON database WhatIF yang telah diekspor:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = jsonInput,
                        onValueChange = {
                            jsonInput = it
                            inputError = null
                        },
                        placeholder = { Text("{\"profile\": ...}") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        maxLines = 6,
                        isError = inputError != null,
                        supportingText = inputError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (jsonInput.isBlank()) {
                            inputError = "Teks JSON tidak boleh kosong"
                            return@Button
                        }
                        val result = onImportJson(jsonInput.trim())
                        if (result.isSuccess) {
                            Toast.makeText(context, "Database berhasil dipulihkan! 🎉", Toast.LENGTH_SHORT).show()
                            showImportDialog = false
                        } else {
                            inputError = "Format JSON tidak valid atau rusak"
                        }
                    }
                ) {
                    Text("Impor")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog Reset Confirmation
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = { Icon(imageVector = Icons.Rounded.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Reset Semua Data?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Tindakan ini akan menghapus semua riwayat catatan mokel dan mengembalikan pengaturan ke awal. Tindakan ini tidak dapat dibatalkan.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetData()
                        Toast.makeText(context, "Data telah direset.", Toast.LENGTH_SHORT).show()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya, Hapus Semua")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog Tentang WhatIF (About)
    if (showAboutDialog) {
        AboutDialog(
            onDismissRequest = { showAboutDialog = false },
            onOpenLicenses = {
                showAboutDialog = false
                showLicensesDialog = true
            }
        )
    }

    // Dialog Open Source Licenses
    if (showLicensesDialog) {
        OpenSourceLicensesDialog(
            onDismissRequest = { showLicensesDialog = false }
        )
    }
}
