package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.animation.core.*
import coil.compose.AsyncImage
import com.example.components.MovingText
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    viewModel: MusicViewModel,
    onBack: () -> Unit
) {
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val isShuffle by viewModel.isShuffle.collectAsState()
    val isRepeat by viewModel.isRepeat.collectAsState()

    if (currentTrack == null) return

    Box(modifier = Modifier.fillMaxSize()) {
        // Blurred Background
        AsyncImage(
            model = currentTrack?.artworkUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.3f
        )
        
        // Dark Overlay Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            val connectedCastDevice by viewModel.connectedCastDevice.collectAsState()
            var showCastDialog by remember { mutableStateOf(false) }
            val context = LocalContext.current

            if (showCastDialog) {
                AlertDialog(
                    onDismissRequest = { showCastDialog = false },
                    title = { Text("Cast Audio") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Select a device to stream your music:")
                            Spacer(modifier = Modifier.height(8.dp))
                            val devices = listOf("Living Room Speaker", "Kitchen Smart TV", "Office Audio System")
                            devices.forEach { device ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.connectCastDevice(device)
                                            showCastDialog = false
                                            Toast.makeText(context, "Casting to $device", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (connectedCastDevice == device) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                         else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.Cast, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(text = device, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                            
                            if (connectedCastDevice != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        viewModel.connectCastDevice(null)
                                        showCastDialog = false
                                        Toast.makeText(context, "Disconnected from Cast", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Disconnect Cast")
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { showCastDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("Now Playing", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                
                IconButton(onClick = { showCastDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Cast,
                        contentDescription = "Cast",
                        tint = if (connectedCastDevice != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
                
                IconButton(onClick = { /* TODO */ }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options")
                }
            }
            
            if (connectedCastDevice != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CastConnected,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Casting to $connectedCastDevice",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Large Artwork
            AsyncImage(
                model = currentTrack?.artworkUrl,
                contentDescription = "Artwork",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title and Artist
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                MovingText(
                    text = currentTrack?.title ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentTrack?.artist ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Equalizer Visualizer Graph
            MusicVisualizer(isPlaying = isPlaying)

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Slider(
                value = progress,
                onValueChange = { viewModel.seekTo(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = SliderDefaults.colors(
                    thumbColor = NeonGreen,
                    activeTrackColor = NeonGreen,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.toggleShuffle() }) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffle) NeonPurple else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = { viewModel.previousTrack() }) {
                    Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(40.dp))
                }
                
                FloatingActionButton(
                    onClick = { viewModel.togglePlayPause() },
                    shape = CircleShape,
                    containerColor = NeonGreen,
                    contentColor = Color.Black,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                IconButton(onClick = { viewModel.nextTrack() }) {
                    Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(40.dp))
                }
                
                IconButton(onClick = { viewModel.toggleRepeat() }) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (isRepeat) NeonPurple else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun MusicVisualizer(isPlaying: Boolean, modifier: Modifier = Modifier) {
    val barCount = 18
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer")
    
    // Create an animated height factor for each frequency bar
    val heights = (0 until barCount).map { index ->
        val duration = remember { (450..950).random() }
        val delay = remember { (0..250).random() }
        infiniteTransition.animateFloat(
            initialValue = 0.15f,
            targetValue = if (isPlaying) 0.95f else 0.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, delayMillis = delay, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEach { heightState ->
            val heightFraction = heightState.value
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(heightFraction)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                NeonPurple,
                                NeonGreen
                            )
                        )
                    )
            )
        }
    }
}
