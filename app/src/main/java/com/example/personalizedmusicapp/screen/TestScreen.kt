package com.example.personalizedmusicapp.screen

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Favorite
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalizedmusicapp.YoutubePlayer
import com.example.personalizedmusicapp.data.Item
import com.example.personalizedmusicapp.data.PlayListItemsResponse
import com.example.personalizedmusicapp.model.VideoEvent
import com.example.personalizedmusicapp.model.VideoState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("playlistItems")
    suspend fun getPlaylistItems(
        @Query("part") part: String,
        @Query("maxResults") maxResults: String,
        @Query("playlistId") playlistId: String,
        @Query("key") key: String
    ): Response<PlayListItemsResponse>
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    state: VideoState,
    onEvent: (VideoEvent) -> Unit
) {
    var playListItems by remember { mutableStateOf(emptyList<Item>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/youtube/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            val part = "snippet"
            val maxResults = "50"
            val playlistId = "PL9JwhzITbbGZGA5qjHDbVfNQnK5Sc_XWG"
            val key = "AIzaSyBKxF26cbuvhSHdc0otnKePjQMi4MLp5GQ"

            val response = apiService.getPlaylistItems(part, maxResults, playlistId, key)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    playListItems = responseBody.items
                }
                Log.d("MyApp", "Success")
            } else {
                // Handle API error here
                Log.d("MyApp", "Failed to retrieve!")
            }
        }
    }
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                )
                {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
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
                    contentDescription = "Add contact"
                )
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
        )
        {
            item() {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                }
            }
            items(playListItems) { item ->
                ItemCard(item)
                }
            }

            /*items(state.videos){video ->
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
            }*/
        }
    }

@Composable
fun ItemCard(item: Item) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(item.snippet.title)
        Text(item.snippet.position)
        Text(item.snippet.resourceId.videoId)
        YoutubePlayer(youtubeVideoId = item.snippet.resourceId.videoId)
        Icon(Icons.Outlined.Favorite, contentDescription = null)
    }
}