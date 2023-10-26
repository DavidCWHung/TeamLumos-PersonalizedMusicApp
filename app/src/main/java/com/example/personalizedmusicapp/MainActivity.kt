package com.example.personalizedmusicapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.personalizedmusicapp.data.Item
import com.example.personalizedmusicapp.data.PlayListItemsResponse
import com.example.personalizedmusicapp.ui.theme.PersonalizedMusicAppTheme
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

@Composable
fun MyApp() {
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(playListItems) { item ->
            ItemCard(item)
        }
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
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalizedMusicAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}