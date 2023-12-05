package com.example.personalizedmusicapp.viewModel

import com.example.personalizedmusicapp.room.Video

sealed interface VideoEvent {
    object SaveVideo : VideoEvent
    data class SetVideo(val youtubeId: String, val title: String, val duration: String) : VideoEvent
    data class SetPlaylistIdText(val playlistIdText: String): VideoEvent
    object ShowDialog : VideoEvent
    object HideDialog : VideoEvent
    data class DeleteVideo(val video: Video) : VideoEvent
    data class DeleteVideoByYoutubeId(val youtubeId: String) : VideoEvent
}