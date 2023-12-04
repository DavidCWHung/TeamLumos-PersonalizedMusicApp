
package com.example.personalizedmusicapp.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.personalizedmusicapp.BuildConfig
import com.example.personalizedmusicapp.YoutubePlayer
import com.example.personalizedmusicapp.data.ContentDetails
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

    @GET("videos")
    suspend fun getVideos(
        @Query("id") id: String,
        @Query("part") part: String,
        @Query("key") key: String
    ): Response<PlayListItemsResponse>
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: VideoState,
    onEvent: (VideoEvent) -> Unit){

    var playListItems by remember { mutableStateOf(emptyList<Item>()) }
    var _playListItems by remember { mutableStateOf(emptyList<Item>()) }
    var contentDetailsList by remember { mutableStateOf(emptyList<ContentDetails>()) }

    val coroutineScope = rememberCoroutineScope()

    var playlistIdText by remember { mutableStateOf("PL9JwhzITbbGZGA5qjHDbVfNQnK5Sc_XWG") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    )
    {
        TextField(
            value = playlistIdText,
            onValueChange = { playlistIdText = it },
            label = { Text("Enter YouTube Playlist ID") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Button(
            onClick = {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://www.googleapis.com/youtube/v3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(ApiService::class.java)

                val part = "snippet"
                val maxResults = "50"
                val key = BuildConfig.API_KEY

                // Use the updated playlistIdText value for API call
                val playlistId = playlistIdText

                coroutineScope.launch(Dispatchers.IO) {
                    val response = apiService.getPlaylistItems(part, maxResults, playlistId, key)

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            playListItems = responseBody.items
                        }
                        Log.d("MyApp", "Fetched PlaylistItems successfully.")

                        playListItems.forEach{
                            val part = "contentDetails"
                            val key = BuildConfig.API_KEY
                            val id = it.snippet.resourceId.videoId
                            val response = apiService.getVideos(id, part, key)
                            var contentDetails = ContentDetails(id, "00:05") // Default values
                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                if (responseBody != null) {
                                    _playListItems = responseBody.items

                                    if (!_playListItems.isEmpty()){
                                        val durationStr = _playListItems[0].contentDetails.duration
                                        var duration: String = "00:05"
                                        if (durationStr.length == 7)
                                            duration = "0" + durationStr.substring(2,3) + ":" + durationStr.substring(4,6)
                                        else if (durationStr.length == 8)
                                            duration = durationStr.substring(2,4) + ":" + durationStr.substring(5,7)
                                        contentDetails = ContentDetails(
                                            duration = duration,
                                            videoId = id
                                        )
                                    }
                                }
                            }
                            contentDetailsList += contentDetails
                            Log.d("MyApp", "Fetched ContentDetails successfully.")
                        }
                    } else {
                        // Handle API error here
                        Log.d("MyApp", "Failed to retrieve PlaylistItem!")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Fetch Playlist")
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            var duration = ""
            items(playListItems) { item ->
                contentDetailsList.forEach {
                    if (it.videoId == item.snippet.resourceId.videoId)
                        duration = it.duration
                }
                ItemCard(item, duration, state, onEvent = onEvent)
            }

            item { Row(modifier = Modifier.height(120.dp)){} }
        }
    }
}

fun showToastMessage(context: Context, message: String){
    Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
}

@Composable
fun ItemCard(item: Item, duration: String, state: VideoState, onEvent: (VideoEvent) -> Unit) {

    var isFound = false
    state.videos.forEach{
        if (it.youtubeId == item.snippet.resourceId.videoId)
            isFound = true
    }
    OutlinedCard(
    ){
        Column(modifier = Modifier.padding(10.dp)){
            Row (modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically){

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(" ${item.snippet.position} - ${duration} ${item.snippet.title}")
                }
                    IconButton(onClick = {
                        if (isFound)
                            onEvent(VideoEvent.DeleteVideoByYoutubeId(item.snippet.resourceId.videoId))
                        else {
                            onEvent(VideoEvent.SetVideo(item.snippet.resourceId.videoId, item.snippet.title, duration))
                            onEvent(VideoEvent.SaveVideo)
                        }
                    }) {
                        if (isFound)
                            Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color.Red)
                        else
                            Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, tint = Color.Red)
                    }
            }
            YoutubePlayer(youtubeVideoId = item.snippet.resourceId.videoId)
        }
    }
}