package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import com.example.network.*
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
data class GeminiDeckResponse(
    val title: String,
    val description: String,
    val cards: List<GeminiCardItem>
)

@JsonClass(generateAdapter = true)
data class GeminiCardItem(
    val front: String,
    val back: String
)

class ZenViewModel(application: Application) : AndroidViewModel(application) {
    private val db = ZenDatabase.getDatabase(application)
    private val journalDao = db.journalDao()
    private val deckDao = db.deckDao()
    private val streakDao = db.streakDao()

    // Observed flows from Database
    val journalEntries: StateFlow<List<JournalEntry>> = journalDao.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyDecks: StateFlow<List<StudyDeck>> = deckDao.getAllDecks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _streakState = MutableStateFlow<UserStreak?>(null)
    val streakState: StateFlow<UserStreak?> = _streakState.asStateFlow()

    // Screen State Controls
    val showJournalWriter = MutableStateFlow(false)
    val showDeckViewer = MutableStateFlow<StudyDeck?>(null)
    val showBreathingSpace = MutableStateFlow(false)

    // Current Active Deck Cards
    private val _activeDeckCards = MutableStateFlow<List<Flashcard>>(emptyList())
    val activeDeckCards: StateFlow<List<Flashcard>> = _activeDeckCards.asStateFlow()

    // Gemini states
    val isGeneratingPrompt = MutableStateFlow(false)
    val isGeneratingResponse = MutableStateFlow(false)
    val isGeneratingDeck = MutableStateFlow(false)
    val promptError = MutableStateFlow<String?>(null)
    val deckError = MutableStateFlow<String?>(null)

    // Active Journaling Draft states
    val activePrompt = MutableStateFlow("")
    val activeJournalContent = MutableStateFlow("")
    val aiReflectiveResponse = MutableStateFlow<String?>(null)
    val activeMood = MutableStateFlow("Peaceful")

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    init {
        loadStreak()
    }

    private fun loadStreak() {
        viewModelScope.launch {
            val streak = streakDao.getStreak() ?: UserStreak(
                lastActiveDate = "",
                currentStreak = 0,
                totalJournalCount = 0
            )
            _streakState.value = streak
        }
    }

    fun generateReflectivePrompt(energy: String) {
        viewModelScope.launch {
            isGeneratingPrompt.value = true
            promptError.value = null
            aiReflectiveResponse.value = null
            activeJournalContent.value = ""

            val prompt = "You are a calming, wise mindfulness guide. Craft a single, deep, thought-provoking reflective journal prompt (max 30 words) to help the user connect with their thoughts today. Base the prompt on the theme of: $energy."
            val apiKey = BuildConfig.GEMINI_API_KEY

            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                activePrompt.value = "How can you bring a sense of mindful warmth and peace to your day right now?"
                isGeneratingPrompt.value = false
                return@launch
            }

            try {
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(temperature = 0.8f)
                )
                val response = RetrofitClient.service.generateContent("gemini-3.5-flash", apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                activePrompt.value = responseText?.trim() ?: "Write about a moment of stillness you experienced recently."
            } catch (e: Exception) {
                promptError.value = "Couldn't connect to Gemini. Using standard prompt instead."
                activePrompt.value = "Write about a moment of stillness you experienced recently."
            } finally {
                isGeneratingPrompt.value = false
                showJournalWriter.value = true
            }
        }
    }

    fun saveJournalAndGetFeedback() {
        val contentText = activeJournalContent.value
        val promptText = activePrompt.value
        val moodText = activeMood.value

        if (contentText.isBlank()) return

        viewModelScope.launch {
            isGeneratingResponse.value = true

            // 1. Instantly save entry to DB
            val newEntry = JournalEntry(
                prompt = promptText,
                content = contentText,
                mood = moodText
            )
            journalDao.insertEntry(newEntry)

            // 2. Update Streaks using robust offline calendar logic
            updateStreakOnJournalSaved()

            // 3. Ask Gemini for wise reflection/feedback
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                aiReflectiveResponse.value = "Your reflection is beautifully saved in your Zen space. Pause for a deep breath and appreciate this moment of connection."
                isGeneratingResponse.value = false
                return@launch
            }

            val aiPrompt = "You are a calming, wise mindfulness guide. The user has shared their reflective journal entry: '$contentText'. Write a compassionate, short, and grounding reflection (max 80 words) to validate their feelings, offer a gentle perspective, and close with a supportive, mindful blessing. Keep it brief, peaceful, and warm."

            try {
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = aiPrompt)))),
                    generationConfig = GenerationConfig(temperature = 0.7f)
                )
                val response = RetrofitClient.service.generateContent("gemini-3.5-flash", apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                aiReflectiveResponse.value = responseText?.trim()
            } catch (e: Exception) {
                aiReflectiveResponse.value = "Thank you for sharing your thoughts. May you carry peace, clarity, and kindness in your heart as you continue your day."
            } finally {
                isGeneratingResponse.value = false
            }
        }
    }

    private suspend fun updateStreakOnJournalSaved() {
        val today = getTodayString()
        val yesterday = getYesterdayString()
        val currentStreakRecord = streakDao.getStreak() ?: UserStreak(
            lastActiveDate = "",
            currentStreak = 0,
            totalJournalCount = 0
        )

        val updatedStreak = when (currentStreakRecord.lastActiveDate) {
            today -> {
                // Already active today, don't increment streak but increment total count
                currentStreakRecord.copy(
                    totalJournalCount = currentStreakRecord.totalJournalCount + 1
                )
            }
            yesterday -> {
                // Active yesterday, increment streak!
                currentStreakRecord.copy(
                    lastActiveDate = today,
                    currentStreak = currentStreakRecord.currentStreak + 1,
                    totalJournalCount = currentStreakRecord.totalJournalCount + 1
                )
            }
            else -> {
                // Streak broken or brand new
                currentStreakRecord.copy(
                    lastActiveDate = today,
                    currentStreak = 1,
                    totalJournalCount = currentStreakRecord.totalJournalCount + 1
                )
            }
        }

        streakDao.insertOrUpdateStreak(updatedStreak)
        _streakState.value = updatedStreak
    }

    fun generateStudyDeck(topic: String) {
        if (topic.isBlank()) return

        viewModelScope.launch {
            isGeneratingDeck.value = true
            deckError.value = null

            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                // Generate a lovely offline placeholder deck
                createOfflinePlaceholderDeck(topic)
                isGeneratingDeck.value = false
                return@launch
            }

            val prompt = """
                Generate a structured educational or mindful study deck on the topic of '$topic'.
                You MUST return a valid JSON object matching this schema exactly:
                {
                  "title": "A short, beautiful title for the deck",
                  "description": "A brief description of what this deck covers",
                  "cards": [
                    {
                      "front": "Term, concept, or question",
                      "back": "Clear, beautiful, mindful explanation"
                    }
                  ]
                }
                You must generate exactly 5 cards. Return ONLY raw JSON. No markdown wrappers.
            """.trimIndent()

            try {
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(
                        responseMimeType = "application/json",
                        temperature = 0.7f
                    )
                )
                val response = RetrofitClient.service.generateContent("gemini-3.5-flash", apiKey, request)
                val jsonString = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: throw Exception("Empty response from Gemini")

                val cleanJson = cleanJsonString(jsonString)
                val adapter = moshi.adapter(GeminiDeckResponse::class.java)
                val parsedDeck = adapter.fromJson(cleanJson) ?: throw Exception("JSON Parsing Failed")

                // Insert Deck to Room
                val deckId = deckDao.insertDeck(
                    StudyDeck(
                        title = parsedDeck.title,
                        description = parsedDeck.description
                    )
                ).toInt()

                // Insert Cards to Room
                parsedDeck.cards.forEach { card ->
                    deckDao.insertFlashcard(
                        Flashcard(
                            deckId = deckId,
                            front = card.front,
                            back = card.back
                        )
                    )
                }
            } catch (e: Exception) {
                deckError.value = "Failed to generate AI deck: ${e.message}. Creating standard guide instead."
                createOfflinePlaceholderDeck(topic)
            } finally {
                isGeneratingDeck.value = false
            }
        }
    }

    private suspend fun createOfflinePlaceholderDeck(topic: String) {
        val deckId = deckDao.insertDeck(
            StudyDeck(
                title = "$topic Guide",
                description = "Offline reflective study guide about $topic"
            )
        ).toInt()

        val cards = listOf(
            Flashcard(deckId = deckId, front = "What is $topic?", back = "An exploration into the nature, values, and practices surrounding $topic."),
            Flashcard(deckId = deckId, front = "Core Value of $topic", back = "Understanding the central focus, principles, and inner alignment of $topic."),
            Flashcard(deckId = deckId, front = "Daily Practice", back = "Integrating $topic into daily mindful routines to foster clarity and presence."),
            Flashcard(deckId = deckId, front = "Common Obstacle", back = "Recognizing distractions or doubts, and meeting them with non-judgmental awareness."),
            Flashcard(deckId = deckId, front = "Mindful Reflection", back = "A quiet pause to observe how $topic resonates with your current state of being.")
        )

        cards.forEach { card ->
            deckDao.insertFlashcard(card)
        }
    }

    private fun cleanJsonString(raw: String): String {
        var clean = raw.trim()
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json")
        }
        if (clean.startsWith("```")) {
            clean = clean.removePrefix("```")
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```")
        }
        return clean.trim()
    }

    fun selectDeckForViewing(deck: StudyDeck) {
        viewModelScope.launch {
            deckDao.getCardsForDeck(deck.id).collect { cards ->
                _activeDeckCards.value = cards
                showDeckViewer.value = deck
            }
        }
    }

    fun deleteDeck(deckId: Int) {
        viewModelScope.launch {
            deckDao.deleteDeck(deckId)
            deckDao.deleteCardsForDeck(deckId)
            if (showDeckViewer.value?.id == deckId) {
                showDeckViewer.value = null
            }
        }
    }

    fun deleteJournalEntry(id: Int) {
        viewModelScope.launch {
            journalDao.deleteEntry(id)
        }
    }

    // Helper functions for date formatting
    private fun getTodayString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getYesterdayString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return sdf.format(cal.time)
    }
}
