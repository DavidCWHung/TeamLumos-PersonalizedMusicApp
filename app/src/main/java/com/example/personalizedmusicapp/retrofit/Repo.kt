package com.example.personalizedmusicapp.retrofit

import android.util.Log
import com.example.personalizedmusicapp.data.ContentDetails
import com.example.personalizedmusicapp.data.Item
// A class represents a repository
class Repo {

    private val apiService: ApiService = RetrofitClient.retrofit.create(ApiService::class.java)

    // Return a consolidated playlistItems including ContentDetails
    suspend fun getPlaylistItems(
        part: String,
        maxResults: String,
        playlistId: String,
        key: String
    ): List<Item> {
        // Store the playlist items
        var playlistItems: List<Item> = emptyList()
        var _playlistItems: List<Item> = emptyList()
        var itemList: List<Item> = emptyList()
        var response = apiService.getPlaylistItems(part, maxResults, playlistId, key)

        if (response.isSuccessful) {
            var responseBody = response.body()
            if (responseBody != null) {
                playlistItems = responseBody.items
            }
            Log.d("MyApp", "Fetched PlaylistItems successfully.")
        }

        // Iterate playlistItems to get the api response based on each youtudeId
        // The response will contain ContentDetails with duration
        playlistItems.forEach(){
            val id = it.snippet.resourceId.videoId
            Log.d("MyApp", it.snippet.resourceId.videoId)
            response = apiService.getVideos(id, "contentDetails", key)

            var contentDetails = ContentDetails(id, "03:00") // Default values

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {

                    _playlistItems = responseBody.items

                    if (!_playlistItems.isEmpty()) {

                        // Convert YouTube duration format PT[mm]M[ss]S into mm:ss
                        // Hour is not handled
                        val durationStr = _playlistItems[0].contentDetails.duration
                        var duration: String = "03:00" // Default value
                        if (durationStr.length == 7) // Minute is in single digit
                            duration = "0" + durationStr.substring(2, 3) + ":" + durationStr.substring(4, 6)
                        else if (durationStr.length == 8) // Minute is in double digit
                            duration =
                                durationStr.substring(2, 4) + ":" + durationStr.substring(5, 7)

                        // Construct a new playlistItem
                        val item = Item(
                            etag = it.etag,
                            id = it.id,
                            kind = it.kind,
                            snippet = it.snippet,
                            contentDetails = ContentDetails(
                                duration = duration,
                                videoId = id
                            )
                        )
                        itemList += item
                    }
                }
                Log.d("MyApp", "Fetched ContentDetails successfully.")
            }
        }
        return itemList
    }
}
