package com.example.personalizedmusicapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalizedmusicapp.room.Video
import com.example.personalizedmusicapp.room.VideoDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VideoViewModel (private val dao: VideoDao): ViewModel() {

    private val _state = MutableStateFlow(VideoState())
    private val _videos = dao.getVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val state = combine(_state, _videos) { state, videos ->
        state.copy(
            videos= videos
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VideoState())

    fun onEvent(event: VideoEvent) {
        when(event) {
            is VideoEvent.DeleteVideo -> {
                viewModelScope.launch {
                    dao.deleteVideo(event.video)
                }
            }

            is VideoEvent.DeleteVideoByYoutubeId -> {
                viewModelScope.launch {
                    dao.deleteVideoByYoutubeId(event.youtubeId)
                }
            }

            VideoEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingVideo = false
                ) }
            }

            VideoEvent.SaveVideo -> {
                val ytVideoId = state.value.youtubeId

                if (ytVideoId.isBlank()) {
                    return
                }

                val video = Video(
                    youtubeId = ytVideoId
                )
                viewModelScope.launch {
                    dao.upsertVideo(video)
                }
                _state.update { it.copy(
                    isAddingVideo = false,
                    youtubeId = ""
                ) }
            }

            is VideoEvent.SetYoutubeId -> {
                _state.update { it.copy(
                    youtubeId = event.youtubeId
                ) }
            }

            VideoEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingVideo = true
                ) }
            }
        }
    }
}