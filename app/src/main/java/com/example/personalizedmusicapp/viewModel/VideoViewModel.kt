package com.example.personalizedmusicapp.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalizedmusicapp.BuildConfig
import com.example.personalizedmusicapp.data.ContentDetails
import com.example.personalizedmusicapp.data.Item
import com.example.personalizedmusicapp.retrofit.ApiService
import com.example.personalizedmusicapp.retrofit.Repo
import com.example.personalizedmusicapp.retrofit.RetrofitClient
import com.example.personalizedmusicapp.room.Video
import com.example.personalizedmusicapp.room.VideoDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class VideoViewModel (private val dao: VideoDao): ViewModel() {

    private var _state = MutableStateFlow(VideoState())
    private var _pl = MutableStateFlow<List<Item>>(emptyList())
    private var _cd = MutableStateFlow<List<ContentDetails>>(emptyList())
    private val _videos = dao.getVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    // var playlistItems : List<Item> = emptyList()
    // private val _playlistItems = MutableStateFlow(playlistItems)

    val state = combine(_state, _videos, _pl, _cd ) { state, videos, pl, cd ->
        state.copy(
            videos= videos,
            playlistItems = pl,
            contentDetailsList = cd
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VideoState())

    private val repo : Repo = Repo()

    fun getPlaylistItems(playlistId: String){
        viewModelScope.launch{
            val part = "snippet"
            val maxResults = "50"
            val key = BuildConfig.API_KEY
            _pl.value  = repo.getPlaylistItems(part, maxResults, playlistId, key)
        }
    }

    fun getContentDetailsList(id: String){
        viewModelScope.launch{
            val part = "contentDetails"
            val key = BuildConfig.API_KEY
            _cd.value  = repo.getContentDetailsList(id, part, key)
        }
    }

    val playlistId= "PL9JwhzITbbGZGA5qjHDbVfNQnK5Sc_XWG"

    fun onEvent(event: VideoEvent) {
        when(event) {

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