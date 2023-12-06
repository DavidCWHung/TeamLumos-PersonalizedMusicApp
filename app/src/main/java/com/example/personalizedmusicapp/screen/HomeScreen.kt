package com.example.personalizedmusicapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.personalizedmusicapp.YoutubePlayer
import com.example.personalizedmusicapp.data.Item
import com.example.personalizedmusicapp.viewModel.VideoEvent
import com.example.personalizedmusicapp.viewModel.VideoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: VideoState,
    onEvent: (VideoEvent) -> Unit
) {
    var playlistIdText by remember { mutableStateOf(state.playlistIdText) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    )
    {
        TextField(
            value = playlistIdText,
            onValueChange = {
                playlistIdText = it
                onEvent(VideoEvent.SetPlaylistIdText(it)) },
            label = { Text("Enter YouTube Playlist ID") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(5.dp)
        ){
            Button(
                onClick = {
                    // Increment the key to trigger recomposition
                    // apiCallKey++
                    onEvent(VideoEvent.UpdatePlaylistItems(playlistIdText))
                },
                modifier = Modifier
                    .padding(5.dp)
            ) {
                Text("Fetch Playlist")
            }

            Button(
                onClick = {
                    // Increment the key to trigger recomposition
                    // apiCallKey++
                    onEvent(VideoEvent.SetPlaylistIdText("PL9JwhzITbbGZGA5qjHDbVfNQnK5Sc_XWG"))
                    playlistIdText = "PL9JwhzITbbGZGA5qjHDbVfNQnK5Sc_XWG"
                },
                modifier = Modifier
                    .padding(5.dp)
            ) {
                Text("Default ID")
            }

            Button(
                onClick = {
                    // Increment the key to trigger recomposition
                    // apiCallKey++
                    onEvent(VideoEvent.SetPlaylistIdText(""))
                    playlistIdText = ""
                },
                modifier = Modifier
                    .padding(5.dp)
            ) {
                Text("Clear ID")
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Show a spinning wheel
                CircularProgressIndicator(modifier = Modifier.size(50.dp).align(Alignment.Center))
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            items(state.playlistItems) { item ->
                ItemCard(item, state, onEvent = onEvent)
            }

            item { Row(modifier = Modifier.height(120.dp)) {} }
       }
    }
}

@Composable
fun ItemCard(
    item: Item,
    state: VideoState,
    onEvent: (VideoEvent) -> Unit
) {

    var isFound = false
    state.videos.forEach {
        if (it.youtubeId == item.snippet.resourceId.videoId)
            isFound = true
    }
    OutlinedCard(
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(" ${item.snippet.position.toInt() + 1} - ${item.contentDetails.duration} ${item.snippet.title}")
                }
                IconButton(onClick = {
                    if (isFound)
                        onEvent(VideoEvent.DeleteVideoByYoutubeId(item.snippet.resourceId.videoId))
                    else {
                        onEvent(
                            VideoEvent.SetVideo(
                                item.snippet.resourceId.videoId,
                                item.snippet.title,
                                item.contentDetails.duration
                            )
                        )
                        onEvent(VideoEvent.SaveVideo)
                    }
                }) {
                    if (isFound)
                        Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color.Red)
                    else
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.Red
                        )
                }
            }
            key(item.snippet.resourceId.videoId) {
                YoutubePlayer(youtubeVideoId = item.snippet.resourceId.videoId)
            }
        }
    }
}