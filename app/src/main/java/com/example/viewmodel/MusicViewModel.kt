package com.example.viewmodel

import android.app.Application
import android.content.ContentUris
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val defaultTracks = listOf(
        Track(
            id = "stream_1",
            title = "Summer Breeze",
            artist = "Lofi Dreamer",
            album = "Chilled Waves",
            genre = "Jazz",
            artworkUrl = "https://picsum.photos/seed/summer/400/400",
            durationSeconds = 372,
            uri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        ),
        Track(
            id = "stream_2",
            title = "Neon Highway",
            artist = "Synthwave King",
            album = "Retro Future",
            genre = "Electronic",
            artworkUrl = "https://picsum.photos/seed/neon/400/400",
            durationSeconds = 423,
            uri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        ),
        Track(
            id = "stream_3",
            title = "Midnight Coffee",
            artist = "Lofi Cafe",
            album = "Late Night Beats",
            genre = "Jazz",
            artworkUrl = "https://picsum.photos/seed/coffee/400/400",
            durationSeconds = 302,
            uri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        ),
        Track(
            id = "stream_4",
            title = "Cyber Run",
            artist = "Neon Rider",
            album = "Grid Runner",
            genre = "Rock",
            artworkUrl = "https://picsum.photos/seed/cyber/400/400",
            durationSeconds = 344,
            uri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
        ),
        Track(
            id = "stream_5",
            title = "Acoustic Sunset",
            artist = "Guitar Vibes",
            album = "Unplugged",
            genre = "Pop",
            artworkUrl = "https://picsum.photos/seed/sunset/400/400",
            durationSeconds = 368,
            uri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"
        )
    )

    private val _library = MutableStateFlow<List<Track>>(emptyList())
    val library: StateFlow<List<Track>> = _library.asStateFlow()

    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f) // 0.0 to 1.0
    val progress: StateFlow<Float> = _progress.asStateFlow()
    
    private val _volume = MutableStateFlow(0.8f)
    val volume: StateFlow<Float> = _volume.asStateFlow()
    
    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()
    
    private val _isRepeat = MutableStateFlow(false)
    val isRepeat: StateFlow<Boolean> = _isRepeat.asStateFlow()

    private val _playlists = MutableStateFlow<Map<String, List<Track>>>(
        mapOf(
            "Favorites" to emptyList(),
            "Workout" to emptyList(),
            "Chill" to emptyList()
        )
    )
    val playlists: StateFlow<Map<String, List<Track>>> = _playlists.asStateFlow()

    // Interactive Settings State Flows
    private val _appTheme = MutableStateFlow("Follow System")
    val appTheme: StateFlow<String> = _appTheme.asStateFlow()

    private val _playerTheme = MutableStateFlow("Modern Glow")
    val playerTheme: StateFlow<String> = _playerTheme.asStateFlow()

    private val _navBarStyle = MutableStateFlow("Full Width")
    val navBarStyle: StateFlow<String> = _navBarStyle.asStateFlow()

    private val _carouselStyle = MutableStateFlow("No Peek")
    val carouselStyle: StateFlow<String> = _carouselStyle.asStateFlow()

    private val _defaultTab = MutableStateFlow("Search")
    val defaultTab: StateFlow<String> = _defaultTab.asStateFlow()

    private val _keepPlayingAfterClosing = MutableStateFlow("On")
    val keepPlayingAfterClosing: StateFlow<String> = _keepPlayingAfterClosing.asStateFlow()

    private val _autoPlayCast = MutableStateFlow("Enabled")
    val autoPlayCast: StateFlow<String> = _autoPlayCast.asStateFlow()

    private val _connectedCastDevice = MutableStateFlow<String?>(null)
    val connectedCastDevice: StateFlow<String?> = _connectedCastDevice.asStateFlow()

    private val _listeningStats = MutableStateFlow(
        mapOf(
            "Mon" to 1.4f,
            "Tue" to 2.8f,
            "Wed" to 1.9f,
            "Thu" to 3.5f,
            "Fri" to 2.2f,
            "Sat" to 0.8f,
            "Sun" to 1.5f
        )
    )
    val listeningStats: StateFlow<Map<String, Float>> = _listeningStats.asStateFlow()

    fun incrementListeningTime() {
        val calendar = java.util.Calendar.getInstance()
        val dayKey = when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.MONDAY -> "Mon"
            java.util.Calendar.TUESDAY -> "Tue"
            java.util.Calendar.WEDNESDAY -> "Wed"
            java.util.Calendar.THURSDAY -> "Thu"
            java.util.Calendar.FRIDAY -> "Fri"
            java.util.Calendar.SATURDAY -> "Sat"
            java.util.Calendar.SUNDAY -> "Sun"
            else -> "Mon"
        }
        
        val currentStats = _listeningStats.value.toMutableMap()
        val currentVal = currentStats[dayKey] ?: 0f
        // Increment by a small amount for each 250ms tick:
        // 250ms = 0.25 seconds. 1 hour = 3600 seconds. So 0.25s is 0.25 / 3600 = ~0.0000694 hours.
        // To make the visual graph updates noticeable in development, let's increment by a slightly higher rate (e.g. 0.01 hours per second -> 0.0025f per 250ms)
        currentStats[dayKey] = currentVal + 0.0025f
        _listeningStats.value = currentStats
    }

    fun connectCastDevice(device: String?) {
        _connectedCastDevice.value = device
        if (device != null && _autoPlayCast.value == "Enabled" && !_isPlaying.value) {
            // Resume / play automatically on cast connect
            mediaPlayer?.start()
            _isPlaying.value = true
        } else if (device == null && _autoPlayCast.value == "Enabled" && _isPlaying.value) {
            // Pause automatically on cast disconnect
            mediaPlayer?.pause()
            _isPlaying.value = false
        }
    }

    private val _crossfade = MutableStateFlow("Enabled")
    val crossfade: StateFlow<String> = _crossfade.asStateFlow()

    private val _crossfadeDuration = MutableStateFlow(6)
    val crossfadeDuration: StateFlow<Int> = _crossfadeDuration.asStateFlow()

    private val _geminiApiKey = MutableStateFlow("")
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    fun updateAppTheme(theme: String) { _appTheme.value = theme }
    fun updatePlayerTheme(theme: String) { _playerTheme.value = theme }
    fun updateNavBarStyle(style: String) { _navBarStyle.value = style }
    fun updateCarouselStyle(style: String) { _carouselStyle.value = style }
    fun updateDefaultTab(tab: String) { _defaultTab.value = tab }
    fun updateKeepPlayingAfterClosing(value: String) { _keepPlayingAfterClosing.value = value }
    fun updateAutoPlayCast(value: String) { _autoPlayCast.value = value }
    fun updateCrossfade(value: String) { _crossfade.value = value }
    fun updateCrossfadeDuration(duration: Int) { _crossfadeDuration.value = duration }
    fun updateGeminiApiKey(key: String) { _geminiApiKey.value = key }

    private var mediaPlayer: MediaPlayer? = null

    init {
        // Start completely empty as requested so the user can populate the library themselves
        _library.value = emptyList()
        _queue.value = emptyList()
        _currentTrack.value = null
        startProgressTracker()
    }

    fun loadDemoTracks() {
        _library.value = defaultTracks
        _queue.value = defaultTracks
        if (defaultTracks.isNotEmpty()) {
            _currentTrack.value = defaultTracks[0]
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun createPlaylist(name: String) {
        val current = _playlists.value.toMutableMap()
        if (!current.containsKey(name)) {
            current[name] = emptyList()
            _playlists.value = current
        }
    }

    fun addTrackToPlaylist(playlistName: String, track: Track) {
        val current = _playlists.value.toMutableMap()
        val list = current[playlistName]?.toMutableList() ?: mutableListOf()
        if (!list.contains(track)) {
            list.add(track)
            current[playlistName] = list
            _playlists.value = current
        }
    }

    fun removeTrackFromPlaylist(playlistName: String, track: Track) {
        val current = _playlists.value.toMutableMap()
        val list = current[playlistName]?.toMutableList() ?: return
        if (list.remove(track)) {
            current[playlistName] = list
            _playlists.value = current
        }
    }

    fun refreshLibrary() {
        viewModelScope.launch {
            val trackList = mutableListOf<Track>()
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
            )
            
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
            
            val categories = listOf("Pop", "Rock", "Hip-Hop", "Jazz", "Electronic", "Classical", "R&B", "Country")
            
            getApplication<Application>().contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn) ?: "Unknown"
                    val artist = cursor.getString(artistColumn) ?: "Unknown"
                    val albumName = cursor.getString(albumColumn) ?: "Unknown Album"
                    val durationMs = cursor.getInt(durationColumn)
                    val albumId = cursor.getLong(albumIdColumn)
                    val genre = categories.random()
                    
                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    
                    val artworkUri = Uri.parse("content://media/external/audio/albumart")
                    val albumArtUri = ContentUris.withAppendedId(artworkUri, albumId).toString()
                    
                    trackList.add(
                        Track(
                            id = id.toString(),
                            title = title,
                            artist = artist,
                            album = albumName,
                            genre = genre,
                            artworkUrl = albumArtUri,
                            durationSeconds = durationMs / 1000,
                            uri = contentUri.toString()
                        )
                    )
                }
            }
            _library.value = trackList
            _queue.value = trackList
            if (trackList.isNotEmpty() && _currentTrack.value == null) {
                _currentTrack.value = trackList[0]
            }
        }
    }

    private fun applyCrossfadeVolume(player: MediaPlayer, currentPositionMs: Int, durationMs: Int) {
        if (_crossfade.value != "Enabled") {
            val vol = _volume.value
            player.setVolume(vol, vol)
            return
        }
        
        val fadeDurationMs = _crossfadeDuration.value * 1000
        if (durationMs <= 0 || fadeDurationMs <= 0) return
        
        val baseVolume = _volume.value
        
        val currentVol = when {
            // Fade in at start
            currentPositionMs < fadeDurationMs -> {
                val fraction = currentPositionMs.toFloat() / fadeDurationMs.toFloat()
                baseVolume * fraction
            }
            // Fade out at end
            currentPositionMs > (durationMs - fadeDurationMs) -> {
                val remainingMs = durationMs - currentPositionMs
                val fraction = remainingMs.toFloat() / fadeDurationMs.toFloat()
                baseVolume * fraction
            }
            // Normal volume in middle
            else -> {
                baseVolume
            }
        }
        
        player.setVolume(currentVol, currentVol)
    }

    private fun playTrackUri(uriStr: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(getApplication(), Uri.parse(uriStr))
                prepare()
                
                // If crossfade is enabled, start volume at 0 for a smooth fade-in
                if (_crossfade.value == "Enabled") {
                    setVolume(0f, 0f)
                } else {
                    val vol = _volume.value
                    setVolume(vol, vol)
                }
                
                start()
                setOnCompletionListener {
                    nextTrack()
                }
            }
            _isPlaying.value = true
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
        } else {
            player.start()
            _isPlaying.value = true
        }
    }
    
    fun nextTrack() {
        val currentIdx = _queue.value.indexOf(_currentTrack.value)
        if (currentIdx != -1 && currentIdx < _queue.value.size - 1) {
            val nextTrack = _queue.value[currentIdx + 1]
            _currentTrack.value = nextTrack
            _progress.value = 0f
            nextTrack.uri?.let { playTrackUri(it) }
        } else if (_isRepeat.value && _queue.value.isNotEmpty()) {
            val firstTrack = _queue.value[0]
            _currentTrack.value = firstTrack
            _progress.value = 0f
            firstTrack.uri?.let { playTrackUri(it) }
        } else {
             _isPlaying.value = false
             _progress.value = 0f
             mediaPlayer?.stop()
             mediaPlayer?.prepare() // Reset
        }
    }
    
    fun previousTrack() {
        val currentIdx = _queue.value.indexOf(_currentTrack.value)
        if (currentIdx > 0) {
            val prevTrack = _queue.value[currentIdx - 1]
            _currentTrack.value = prevTrack
            _progress.value = 0f
            prevTrack.uri?.let { playTrackUri(it) }
        } else {
            mediaPlayer?.seekTo(0)
            _progress.value = 0f
        }
    }

    fun playTrack(track: Track) {
        _currentTrack.value = track
        _progress.value = 0f
        track.uri?.let { playTrackUri(it) }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            // Stop playback if current
            if (_currentTrack.value?.id == track.id) {
                mediaPlayer?.stop()
                _isPlaying.value = false
                _progress.value = 0f
                _currentTrack.value = null
            }
            
            // Remove from local/internal lists first
            _library.value = _library.value.filter { it.id != track.id }
            _queue.value = _queue.value.filter { it.id != track.id }
            
            // Remove from playlists
            val updatedPlaylists = _playlists.value.mapValues { (_, tracks) ->
                tracks.filter { it.id != track.id }
            }
            _playlists.value = updatedPlaylists
            
            // Attempt to delete from external device storage if it's a real media file
            track.uri?.let { uriStr ->
                if (uriStr.startsWith("content://") || uriStr.startsWith("file://")) {
                    try {
                        val fileUri = Uri.parse(uriStr)
                        getApplication<Application>().contentResolver.delete(fileUri, null, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    
    fun seekTo(fraction: Float) {
        _progress.value = fraction
        val player = mediaPlayer ?: return
        player.seekTo((fraction * player.duration).toInt())
    }
    
    fun setVolume(vol: Float) {
        _volume.value = vol
        mediaPlayer?.setVolume(vol, vol)
    }
    
    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }
    
    fun toggleRepeat() {
        _isRepeat.value = !_isRepeat.value
    }

    private fun startProgressTracker() {
        viewModelScope.launch {
            while (true) {
                delay(250) // High frequency polling for smooth volume fade & slider tracking
                val player = mediaPlayer
                if (_isPlaying.value && player != null) {
                    try {
                        if (player.isPlaying) {
                            val pos = player.currentPosition
                            val dur = player.duration
                            if (dur > 0) {
                                _progress.value = pos.toFloat() / dur.toFloat()
                                applyCrossfadeVolume(player, pos, dur)
                                incrementListeningTime()
                            }
                        }
                    } catch (e: Exception) {
                        // Suppress background exceptions during transition
                    }
                }
            }
        }
    }
}
