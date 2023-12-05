package com.example.personalizedmusicapp.viewModel

import com.example.personalizedmusicapp.data.ContentDetails
import com.example.personalizedmusicapp.data.Item
import com.example.personalizedmusicapp.room.Video

data class VideoState(
    val videos: List<Video> = emptyList(),
    val contentDetailsList: List<ContentDetails> = emptyList(),
    var playlistItems : List<Item> = emptyList(),
    val youtubeId: String = "",
    val title: String = "",
    val duration: String ="",
    val isAddingVideo: Boolean = false,
    val playlistIdText: String ="",
)