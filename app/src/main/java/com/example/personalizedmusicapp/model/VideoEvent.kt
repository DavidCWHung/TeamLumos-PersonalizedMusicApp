package com.example.personalizedmusicapp.model

import com.example.personalizedmusicapp.room.Video

sealed interface VideoEvent {

    object SaveVideo: VideoEvent
    data class SetYoutubeId(val youtubeId: String): VideoEvent
    object ShowDialog: VideoEvent
    object HideDialog: VideoEvent
    data class DeleteVideo (val video: Video): VideoEvent

}