package com.example.personalizedmusicapp.viewModel

import com.example.personalizedmusicapp.data.ContentDetails
import com.example.personalizedmusicapp.data.Item
import com.example.personalizedmusicapp.room.Video

data class VideoState(
    // Store a state of a video unit
    val youtubeId: String = "",
    val title: String = "",
    val duration: String ="",
    val isAddingVideo: Boolean = false,
    val playlistIdText: String ="",
    // Store a complete playlistItems fetched from Retrofit
    var playlistItems : List<Item> = emptyList(),
    // Store a complete video list of data fetched from Room
    val videos: List<Video> = emptyList(),
)