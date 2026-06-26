package com.example.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "Unknown Album",
    val genre: String = "Pop",
    val artworkUrl: String = "",
    val durationSeconds: Int,
    val uri: String? = null
)

val Track.displayArtist: String
    get() = if (artist.isBlank() || artist.equals("<unknown>", ignoreCase = true)) "Unknown Artist" else artist

val Track.displayTitle: String
    get() = if (title.isBlank() || title.equals("<unknown>", ignoreCase = true)) "Unknown Title" else title

val Track.displayAlbum: String
    get() = if (album.isBlank() || album.equals("<unknown>", ignoreCase = true)) "Unknown Album" else album

