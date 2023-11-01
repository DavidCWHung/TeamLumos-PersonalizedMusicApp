package com.example.personalizedmusicapp.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Video::class],
    version = 1 // 1,2,3... for data migration
)
abstract class VideoDatabase: RoomDatabase() {

    abstract val dao: VideoDao

}
