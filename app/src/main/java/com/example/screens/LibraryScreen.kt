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
import com.example.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(viewModel: MusicViewModel, onNavigateToSettings: () -> Unit) {
    val library by viewModel.library.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    var selectedTab by remember { mutableStateOf("SONGS") }
    val tabs = listOf("SONGS", "ALBUMS", "ARTIST", "PLAYLISTS")

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
