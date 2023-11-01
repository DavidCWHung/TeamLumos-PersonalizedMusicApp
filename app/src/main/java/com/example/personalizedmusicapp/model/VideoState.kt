package com.example.personalizedmusicapp.model

import com.example.personalizedmusicapp.room.Video

data class VideoState(
    val videos: List<Video> = emptyList(),
    val youtubeId: String ="",
    val isAddingVideo: Boolean = false
)