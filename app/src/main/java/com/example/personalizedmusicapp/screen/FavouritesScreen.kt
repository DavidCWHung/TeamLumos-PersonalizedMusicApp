package com.example.personalizedmusicapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalizedmusicapp.YoutubePlayer
import com.example.personalizedmusicapp.data.Item
import com.example.personalizedmusicapp.model.VideoEvent
import com.example.personalizedmusicapp.model.VideoState

@Composable
fun FavouritesScreen(
    state: VideoState,
    onEvent: (VideoEvent) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    )
    {
        items(state.videos) { video ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${video.youtubeId}",
                        fontSize = 20.sp
                    )
                }
                    IconButton(onClick = {
                        onEvent(VideoEvent.DeleteVideo(video))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete video"
                        )
                    }
            }
        }
    }
}

@Composable
fun FavItemCard(item: Item, state: VideoState, onEvent: (VideoEvent) -> Unit) {

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(item.snippet.title)
        Text(item.snippet.position)
        Text(item.snippet.resourceId.videoId)
        YoutubePlayer(youtubeVideoId = item.snippet.resourceId.videoId)
        IconButton(onClick = {
        onEvent(VideoEvent.DeleteVideoByYoutubeId(item.snippet.resourceId.videoId))
        }) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete video"
        )
        }
    }
}
