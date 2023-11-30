package com.example.personalizedmusicapp.model

import com.example.personalizedmusicapp.room.Video

sealed interface VideoEvent {
    object SaveVideo : VideoEvent
    data class SetVideo(val youtubeId: String, val title: String, val duration: String) : VideoEvent
    object ShowDialog : VideoEvent
    object HideDialog : VideoEvent
    data class DeleteVideo(val video: Video) : VideoEvent
    data class DeleteVideoByYoutubeId(val youtubeId: String) : VideoEvent
}