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
