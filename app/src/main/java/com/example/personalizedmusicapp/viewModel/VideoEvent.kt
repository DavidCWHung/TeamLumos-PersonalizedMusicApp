package com.example.personalizedmusicapp.viewModel

import com.example.personalizedmusicapp.room.Video

sealed interface VideoEvent {
    // Home screen play list id text
    data class SetPlaylistIdText(val playlistIdText: String): VideoEvent
    // Favourite screen
    object ShowDialog : VideoEvent
    object HideDialog : VideoEvent
    object SaveVideo : VideoEvent
    data class SetVideo(val youtubeId: String, val title: String, val duration: String) : VideoEvent
    // For Room database
    data class DeleteVideo(val video: Video) : VideoEvent
    data class DeleteVideoByYoutubeId(val youtubeId: String) : VideoEvent
    // For Retrofit API
    data class UpdatePlaylistItems (val playlistId : String) : VideoEvent
}