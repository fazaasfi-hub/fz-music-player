package com.example.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MusicViewModel, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.refreshLibrary()
            Toast.makeText(context, "Library refreshed!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission denied to read music files", Toast.LENGTH_SHORT).show()
        }
    }

    // Collect settings states from the viewmodel to make them functional
    val appTheme by viewModel.appTheme.collectAsState()
    val playerTheme by viewModel.playerTheme.collectAsState()
    val navBarStyle by viewModel.navBarStyle.collectAsState()
    val carouselStyle by viewModel.carouselStyle.collectAsState()
    val defaultTab by viewModel.defaultTab.collectAsState()
    val keepPlayingAfterClosing by viewModel.keepPlayingAfterClosing.collectAsState()
    val autoPlayCast by viewModel.autoPlayCast.collectAsState()
    val crossfade by viewModel.crossfade.collectAsState()
    val crossfadeDuration by viewModel.crossfadeDuration.collectAsState()
    val geminiApiKey by viewModel.geminiApiKey.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // --- APPEARANCE SECTION ---
        SettingsSectionTitle("Appearance")
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingsItemWithDropdown(
                    icon = Icons.Default.LightMode,
                    title = "App Theme",
                    subtitle = "Switch between light, dark, or follow system appearance.",
                    selectedOption = appTheme,
                    options = listOf("Follow System", "Dark", "Light"),
                    onOptionSelected = { viewModel.updateAppTheme(it) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)
                SettingsItemWithDropdown(
                    icon = Icons.Default.MusicNote,
                    title = "Player Theme",
                    subtitle = "Choose the appearance for the floating player.",
                    selectedOption = playerTheme,
                    options = listOf("Classic", "Modern Glow", "Minimalist"),
                    onOptionSelected = { viewModel.updatePlayerTheme(it) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)
                SettingsItemWithDropdown(
                    icon = Icons.Default.Style,
                    title = "NavBar Style",
                    subtitle = "Choose the appearance for the navigation bar.",
                    selectedOption = navBarStyle,
                    options = listOf("Full Width", "Floating Rounded", "Compact"),
                    onOptionSelected = { viewModel.updateNavBarStyle(it) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)
                SettingsItemWithDropdown(
                    icon = Icons.Default.ViewCarousel,
                    title = "Carousel Style",
                    subtitle = "Choose the appearance for the album carousel.",
                    selectedOption = carouselStyle,
                    options = listOf("No Peek", "Peek Next", "Cards Carousel"),
                    onOptionSelected = { viewModel.updateCarouselStyle(it) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)
                SettingsItemWithDropdown(
                    icon = Icons.Default.Tab,
                    title = "Default Tab",
                    subtitle = "Choose the Default launch tab.",
                    selectedOption = defaultTab,
                    options = listOf("Search", "Home", "Library"),
                    onOptionSelected = { viewModel.updateDefaultTab(it) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // --- PLAYBACK SECTION (as in screenshot) ---
        SettingsSectionTitle("Playback")
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingsItemWithDropdown(
                    icon = Icons.Default.MusicNote,
                    title = "Keep playing after closing",
                    subtitle = "If off, removing the app from recents will stop playback.",
                    selectedOption = keepPlayingAfterClosing,
                    options = listOf("On", "Off"),
                    onOptionSelected = { viewModel.updateKeepPlayingAfterClosing(it) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)
                SettingsItemWithDropdown(
                    icon = Icons.Default.Cast,
                    title = "Auto-play on cast connect/disconnect",
                    subtitle = "Start playing immediately after switching cast connections.",
                    selectedOption = autoPlayCast,
                    options = listOf("Enabled", "Disabled"),
                    onOptionSelected = { viewModel.updateAutoPlayCast(it) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)
                SettingsItemWithDropdown(
                    icon = Icons.Default.FormatLineSpacing,
                    title = "Crossfade",
                    subtitle = "Enable smooth transition between songs.",
                    selectedOption = crossfade,
                    options = listOf("Enabled", "Disabled"),
                    onOptionSelected = { viewModel.updateCrossfade(it) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)
                CrossfadeDurationSetting(
                    durationSeconds = crossfadeDuration,
                    onDurationChanged = { viewModel.updateCrossfadeDuration(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- AI INTEGRATION SECTION (as in screenshot) ---
        SettingsSectionTitle("AI Integration (Beta)")
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            GeminiApiKeySetting(
                apiKey = geminiApiKey,
                onKeyChanged = { viewModel.updateGeminiApiKey(it) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- MUSIC MANAGEMENT SECTION ---
        SettingsSectionTitle("Music Management")
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingsItem(
                    icon = Icons.Default.FolderOpen,
                    title = "Allowed Directories",
                    subtitle = "Choose the directories you want to get the music files from.",
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForwardIos
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)
                SettingsItem(
                    icon = Icons.Default.Sync,
                    title = "Refresh Library",
                    subtitle = "Rescan MediaStore and update the local database.",
                    onClick = {
                        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Manifest.permission.READ_MEDIA_AUDIO
                        } else {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        }
                        permissionLauncher.launch(permission)
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Subject,
                    title = "Reset Imported Lyrics",
                    subtitle = "Remove all imported lyrics from the database."
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // --- ABOUT SECTION ---
        SettingsSectionTitle("About")
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingsItem(
                icon = Icons.Default.Person,
                title = "Developer",
                subtitle = "Zerss"
            )
        }
        
        Spacer(modifier = Modifier.height(100.dp)) // Floating player offset
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingIcon: ImageVector? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SettingsItemWithDropdown(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                        .clickable { expanded = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedOption,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onOptionSelected(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CrossfadeDurationSetting(
    durationSeconds: Int,
    onDurationChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Crossfade Duration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${durationSeconds}s",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Interactive Dots with vertical divider bar as shown in the screenshot
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val maxSeconds = 15
            
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..maxSeconds) {
                    if (i == durationSeconds) {
                        // The active vertical divider
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    } else {
                        // Small dot representing seconds
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i < durationSeconds) MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                                .clickable { onDurationChanged(i) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GeminiApiKeySetting(
    apiKey: String,
    onKeyChanged: (String) -> Unit
) {
    var textValue by remember(apiKey) { mutableStateOf(apiKey) }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Gemini API Key",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Needed for AI-powered features.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (isEditing) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    placeholder = { Text("Enter API Key") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                Button(
                    onClick = {
                        onKeyChanged(textValue)
                        isEditing = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save")
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                    .clickable { isEditing = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (apiKey.isEmpty()) "Tap to set API Key" else "••••••••••••••••",
                        color = if (apiKey.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
