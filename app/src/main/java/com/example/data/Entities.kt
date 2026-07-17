package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prompt: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mood: String
)

@Entity(tableName = "study_decks")
data class StudyDeck(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckId: Int,
    val front: String,
    val back: String
)

@Entity(tableName = "user_streak")
data class UserStreak(
    @PrimaryKey val id: Int = 1,
    val lastActiveDate: String, // YYYY-MM-DD
    val currentStreak: Int,
    val totalJournalCount: Int
)

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntry)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteEntry(id: Int)
}

@Dao
interface DeckDao {
    @Query("SELECT * FROM study_decks ORDER BY timestamp DESC")
    fun getAllDecks(): Flow<List<StudyDeck>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: StudyDeck): Long

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId")
    fun getCardsForDeck(deckId: Int): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(card: Flashcard)

    @Query("DELETE FROM study_decks WHERE id = :deckId")
    suspend fun deleteDeck(deckId: Int)

    @Query("DELETE FROM flashcards WHERE deckId = :deckId")
    suspend fun deleteCardsForDeck(deckId: Int)
}

@Dao
interface StreakDao {
    @Query("SELECT * FROM user_streak WHERE id = 1")
    suspend fun getStreak(): UserStreak?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: UserStreak)
}
