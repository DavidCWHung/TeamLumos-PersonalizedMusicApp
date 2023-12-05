package com.example.personalizedmusicapp.retrofit
import com.example.personalizedmusicapp.data.PlayListItemsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
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