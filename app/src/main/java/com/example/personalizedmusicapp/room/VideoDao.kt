package com.example.personalizedmusicapp.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.personalizedmusicapp.model.VideoEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Upsert
    suspend fun upsertVideo(video: Video)

    @Query("DELETE FROM video WHERE youtube_id= :youtubeId")
    suspend fun deleteVideoByYoutubeId(youtubeId: String)

    @Delete
    suspend fun deleteVideo(video: Video)

    @Query("SELECT * FROM video")
    fun getVideos(): Flow<List<Video>>
}