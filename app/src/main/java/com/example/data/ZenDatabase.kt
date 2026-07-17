package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [JournalEntry::class, StudyDeck::class, Flashcard::class, UserStreak::class],
    version = 1,
    exportSchema = false
)
abstract class ZenDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun deckDao(): DeckDao
    abstract fun streakDao(): StreakDao

    companion object {
        @Volatile
        private var INSTANCE: ZenDatabase? = null

        fun getDatabase(context: Context): ZenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZenDatabase::class.java,
                    "zenscribe_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
