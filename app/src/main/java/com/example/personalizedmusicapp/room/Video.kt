package com.example.personalizedmusicapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Video(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="video_id")
    val videoId: Int = 0,
    @ColumnInfo(name="youtube_id")
    val youtubeId: String = ""
)
