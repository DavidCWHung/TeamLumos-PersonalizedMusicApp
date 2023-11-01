package com.example.personalizedmusicapp.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Upsert
    suspend fun upsertVideo(video: Video)

    @Delete
    suspend fun deleteVideo(video: Video)

    @Query("SELECT * FROM video")
    fun getVideos(): Flow<List<Video>>
}