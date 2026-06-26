package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.TrackListItem
import com.example.model.Track
import com.example.model.displayTitle
import com.example.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(viewModel: MusicViewModel, onNavigateToSettings: () -> Unit) {
    val library by viewModel.library.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    var selectedTab by remember { mutableStateOf("SONGS") }
    val tabs = listOf("SONGS", "ALBUMS", "ARTIST", "PLAYLISTS")

    var trackToDelete by remember { mutableStateOf<Track?>(null) }
    var trackToRemoveFromPlaylist by remember { mutableStateOf<Pair<String, Track>?>(null) }

    if (trackToRemoveFromPlaylist != null) {
        AlertDialog(
            onDismissRequest = { trackToRemoveFromPlaylist = null },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Keluarkan dari Playlist?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Apakah Anda yakin ingin mengeluarkan \"${trackToRemoveFromPlaylist?.second?.displayTitle}\" dari playlist \"${trackToRemoveFromPlaylist?.first}\"?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Lagu ini tetap tersimpan di HP Anda dan hanya dihapus dari daftar playlist ini.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        trackToRemoveFromPlaylist?.let { (playlistName, track) ->
                            viewModel.removeTrackFromPlaylist(playlistName, track)
                        }
                        trackToRemoveFromPlaylist = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text("Keluarkan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { trackToRemoveFromPlaylist = null },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text("Batal")
                }
            }
        )
    }

    if (trackToDelete != null) {
        AlertDialog(
            onDismissRequest = { trackToDelete = null },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Hapus Lagu?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Apakah Anda yakin ingin menghapus \"${trackToDelete?.displayTitle}\"?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Lagu ini juga akan dihapus secara permanen dari penyimpanan HP Anda. Tindakan ini tidak dapat dibatalkan.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        trackToDelete?.let { viewModel.deleteTrack(it) }
                        trackToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { trackToDelete = null },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text("Batal")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Library",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Tabs
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tabs) { tab ->
                val isSelected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Shuffle & Sort
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { viewModel.toggleShuffle() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Shuffle",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "Sort",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List
        val playlistsMap by viewModel.playlists.collectAsState()
        val playlistNames = remember(playlistsMap) { playlistsMap.keys.toList() }

        when (selectedTab) {
            "SONGS" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(library) { track ->
                        TrackListItem(
                            track = track,
                            isPlaying = track.id == currentTrack?.id,
                            onClick = { viewModel.playTrack(track) },
                            playlists = playlistNames,
                            onAddToPlaylist = { playlistName -> viewModel.addTrackToPlaylist(playlistName, track) },
                            onDeleteTrack = { trackToDelete = it },
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }
            }
            "ALBUMS" -> {
                val albums = remember(library) { library.groupBy { it.album } }
                var expandedAlbum by remember { mutableStateOf<String?>(null) }
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    albums.forEach { (albumName, tracks) ->
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { 
                                        expandedAlbum = if (expandedAlbum == albumName) null else albumName 
                                    }
                                    .padding(16.dp)
                            ) {
                                Text(text = albumName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                Text(text = "${tracks.size} songs", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            }
                        }
                        if (expandedAlbum == albumName) {
                            items(tracks) { track ->
                                TrackListItem(
                                    track = track,
                                    isPlaying = track.id == currentTrack?.id,
                                    onClick = { viewModel.playTrack(track) },
                                    playlists = playlistNames,
                                    onAddToPlaylist = { playlistName -> viewModel.addTrackToPlaylist(playlistName, track) },
                                    onDeleteTrack = { trackToDelete = it },
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                        }
                    }
                }
            }
            "ARTIST" -> {
                val artists = remember(library) { library.groupBy { it.artist } }
                var expandedArtist by remember { mutableStateOf<String?>(null) }
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    artists.forEach { (artistName, tracks) ->
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { 
                                        expandedArtist = if (expandedArtist == artistName) null else artistName 
                                    }
                                    .padding(16.dp)
                            ) {
                                Text(text = artistName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                Text(text = "${tracks.size} songs", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            }
                        }
                        if (expandedArtist == artistName) {
                            items(tracks) { track ->
                                TrackListItem(
                                    track = track,
                                    isPlaying = track.id == currentTrack?.id,
                                    onClick = { viewModel.playTrack(track) },
                                    playlists = playlistNames,
                                    onAddToPlaylist = { playlistName -> viewModel.addTrackToPlaylist(playlistName, track) },
                                    onDeleteTrack = { trackToDelete = it },
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                        }
                    }
                }
            }
            "PLAYLISTS" -> {
                var showAddDialog by remember { mutableStateOf(false) }
                var newPlaylistName by remember { mutableStateOf("") }
                var expandedPlaylist by remember { mutableStateOf<String?>(null) }
                
                if (showAddDialog) {
                    AlertDialog(
                        onDismissRequest = { showAddDialog = false },
                        title = { Text("New Playlist") },
                        text = {
                            OutlinedTextField(
                                value = newPlaylistName,
                                onValueChange = { newPlaylistName = it },
                                label = { Text("Playlist Name") }
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                if (newPlaylistName.isNotBlank()) {
                                    viewModel.createPlaylist(newPlaylistName)
                                    newPlaylistName = ""
                                }
                                showAddDialog = false
                            }) { Text("Add") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                        }
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Button(
                            onClick = { showAddDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Create New Playlist")
                        }
                    }
                    playlistNames.forEach { playlist ->
                        val tracks = playlistsMap[playlist] ?: emptyList()
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { 
                                        expandedPlaylist = if (expandedPlaylist == playlist) null else playlist
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shuffle, 
                                        contentDescription = null, 
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = playlist, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                    Text(text = "${tracks.size} songs", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                }
                            }
                        }
                        if (expandedPlaylist == playlist) {
                            if (tracks.isEmpty()) {
                                item {
                                    Text(
                                        text = "Empty playlist. Tap 3 dots on any song to add it here.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp)
                                    )
                                }
                            } else {
                                items(tracks) { track ->
                                    TrackListItem(
                                        track = track,
                                        isPlaying = track.id == currentTrack?.id,
                                        onClick = { viewModel.playTrack(track) },
                                        playlists = playlistNames,
                                        onAddToPlaylist = { playlistName -> viewModel.addTrackToPlaylist(playlistName, track) },
                                        onRemoveFromPlaylist = { trackToRemoveFromPlaylist = Pair(playlist, it) },
                                        modifier = Modifier
                                            .padding(start = 16.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
