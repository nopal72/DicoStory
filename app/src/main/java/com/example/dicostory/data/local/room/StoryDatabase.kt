package com.example.dicostory.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dicostory.data.local.entity.StoryEntity

@Database(entities = [StoryEntity::class], version = 1, exportSchema = false)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun storyDao(): StoryDao

    companion object {
        @Volatile
        private var instance: StoryDatabase? = null
        fun getInstance(context: Context): StoryDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java,
                    "story.db"
                ).build()
            }
    }
}