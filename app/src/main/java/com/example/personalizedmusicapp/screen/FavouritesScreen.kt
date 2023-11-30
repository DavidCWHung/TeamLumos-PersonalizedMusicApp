package com.example.personalizedmusicapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalizedmusicapp.YoutubePlayer
import com.example.personalizedmusicapp.data.Item
import com.example.personalizedmusicapp.model.VideoEvent
import com.example.personalizedmusicapp.model.VideoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    state: VideoState,
    onEvent: (VideoEvent) -> Unit) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary ) {
                Row(
                    horizontalArrangement = Arrangement.Center) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Library"
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(VideoEvent.ShowDialog)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add contact" )
            }
        },
    )
    { _ ->
        if (state.isAddingVideo) {
            AddVideoDialog(state = state, onEvent = onEvent)
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.videos) { video ->
                key(video.youtubeId)
                {
                    FavItemCard(youtubeId = video.youtubeId, title = video.title, onEvent = onEvent)
                }
            }
            item { Row(modifier = Modifier.height(120.dp)){} }
        }
    }
}

@Composable
fun FavItemCard(youtubeId: String, title: String, onEvent: (VideoEvent) -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(5.dp),
            verticalAlignment = Alignment.CenterVertically){
            Text(title)
            Row(modifier= Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End) {
                IconButton(onClick = {
                    onEvent(VideoEvent.DeleteVideoByYoutubeId(youtubeId))
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete video"
                    )
                }
            }
        }
        YoutubePlayer(youtubeVideoId = youtubeId)
    }
}