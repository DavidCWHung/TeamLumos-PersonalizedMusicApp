package com.example.personalizedmusicapp.retrofit

import android.util.Log
import com.example.personalizedmusicapp.data.ContentDetails
import com.example.personalizedmusicapp.data.Item
import com.example.personalizedmusicapp.viewModel.VideoEvent

class Repo {

    private val apiService: ApiService = RetrofitClient.retrofit.create(ApiService::class.java)

    suspend fun getPlaylistItems(
        part: String,
        maxResults: String,
        playlistId: String,
        key: String
    ): List<Item> {
        Log.d("MyApp", "Repo fun")
        var playlistItems: List<Item> = emptyList()

        val response = apiService.getPlaylistItems(part, maxResults, playlistId, key)

        if (response.isSuccessful) {
            var responseBody = response.body()
            if (responseBody != null) {
                playlistItems = responseBody.items
            }
            Log.d("MyApp", "Fetched PlaylistItems successfully.")
        }

        return playlistItems
    }

    suspend fun getContentDetailsList(
        id: String,
        part: String,
        key: String
    ): List<ContentDetails> {
        Log.d("MyApp", "Repo fun2")

        var _playlistItems: List<Item> = emptyList()
        var contentDetailsList: List<ContentDetails> = emptyList()

        val response = apiService.getVideos(id, part, key)

        var contentDetails = ContentDetails(id, "00:05") // Default values

        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {

                _playlistItems = responseBody.items

                if (!_playlistItems.isEmpty()) {
                    val durationStr = _playlistItems[0].contentDetails.duration
                    var duration: String = "00:05"
                    if (durationStr.length == 7)
                        duration = "0" + durationStr.substring(
                            2,
                            3
                        ) + ":" + durationStr.substring(4, 6)
                    else if (durationStr.length == 8)
                        duration =
                            durationStr.substring(
                                2,
                                4
                            ) + ":" + durationStr.substring(
                                5,
                                7
                            )
                    contentDetails = ContentDetails(
                        duration = duration,
                        videoId = id
                    )
                }
            }
        }
        contentDetailsList += contentDetails
        Log.d("MyApp", "Fetched ContentDetails successfully.")
        return contentDetailsList
    }
}
