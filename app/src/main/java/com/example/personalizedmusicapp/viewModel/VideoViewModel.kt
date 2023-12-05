package com.example.personalizedmusicapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalizedmusicapp.BuildConfig
import com.example.personalizedmusicapp.data.Item
import com.example.personalizedmusicapp.retrofit.Repo
import com.example.personalizedmusicapp.room.Video
import com.example.personalizedmusicapp.room.VideoDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
// A class represents a viewModel for video
class VideoViewModel (private val dao: VideoDao): ViewModel() {

    private var _state = MutableStateFlow(VideoState())
    private val _videos = dao.getVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private var _playlistItems = MutableStateFlow<List<Item>>(emptyList())

    val state = combine(_state, _videos, _playlistItems) { state, videos, playlistItems ->
        state.copy(
            videos= videos,
            playlistItems = playlistItems,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VideoState())

    private val repo : Repo = Repo()

    fun updatePlaylistItems(playlistId: String){
        viewModelScope.launch{
            val part = "snippet"
            val maxResults = "50"
            val key = BuildConfig.API_KEY
            _playlistItems.value  = repo.getPlaylistItems(part, maxResults, playlistId, key)
        }
    }

    fun onEvent(event: VideoEvent) {
        when(event) {

            is VideoEvent.UpdatePlaylistItems ->{
                updatePlaylistItems(event.playlistId)
            }

            is VideoEvent.SetPlaylistIdText ->{
                _state.update {it.copy(
                    playlistIdText = event.playlistIdText
                )}
            }

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

            is VideoEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingVideo = false
                ) }
            }

            is VideoEvent.SaveVideo -> {
                val youtubeId = state.value.youtubeId
                val title = state.value.title
                val duration = state.value.duration

                if (youtubeId.isBlank()) {
                    return
                }

                val video = Video(
                    youtubeId = youtubeId,
                    title = title,
                    duration = duration
                )
                viewModelScope.launch {
                    dao.upsertVideo(video)
                }
                _state.update { it.copy(
                    isAddingVideo = false,
                    youtubeId = ""
                ) }
            }

            is VideoEvent.SetVideo -> {
                _state.update { it.copy(
                    youtubeId = event.youtubeId,
                    title = event.title,
                    duration = event.duration
                ) }
            }

            is VideoEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingVideo = true
                ) }
            }
        }
    }
}