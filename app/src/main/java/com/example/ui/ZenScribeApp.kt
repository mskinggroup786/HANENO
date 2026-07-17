package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.JournalEntry
import com.example.data.StudyDeck
import com.example.viewmodel.ZenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenScribeApp(
    modifier: Modifier = Modifier,
    viewModel: ZenViewModel = viewModel()
) {
    val context = LocalContext.current
    val journalEntries by viewModel.journalEntries.collectAsStateWithLifecycle()
    val studyDecks by viewModel.studyDecks.collectAsStateWithLifecycle()
    val streakState by viewModel.streakState.collectAsStateWithLifecycle()

    val showJournalWriter by viewModel.showJournalWriter.collectAsStateWithLifecycle()
    val showDeckViewer by viewModel.showDeckViewer.collectAsStateWithLifecycle()
    val showBreathingSpace by viewModel.showBreathingSpace.collectAsStateWithLifecycle()

    // Clean Minimalism background color
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Main Scrollable Dashboard Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ZenScribe",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "your mindful space",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(
                        onClick = { viewModel.showBreathingSpace.value = true },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary, CircleShape)
                            .testTag("breathe_shortcut_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = "Quick Breathing",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Streak & Vital Board
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("streak_board_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = Color(0xFFDD6B20),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${streakState?.currentStreak ?: 0} Days",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Current Streak",
                                fontSize = 11.sp,
                                color = com.example.ui.theme.CleanTextMuted
                            )
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.outline)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = "Total Journals",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${streakState?.totalJournalCount ?: 0} Saved",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Zen Reflections",
                                fontSize = 11.sp,
                                color = com.example.ui.theme.CleanTextMuted
                            )
                        }
                    }
                }
            }

            // Hero Graphic Banner & Quick Actions
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_banner_card"),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.height(180.dp)) {
                        // Background Image Resource from generation step
                        Image(
                            painter = painterResource(id = R.drawable.img_zenscribe_hero_1784262729826),
                            contentDescription = "Mindfulness Sanctuary",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Dark Overlay Gradient for text readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color(0xBB000000))
                                    )
                                )
                        )
                        // Content Overlaid
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Align Your Inner State",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Calm your mind and explore concepts with Gemini AI.",
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.showBreathingSpace.value = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SelfImprovement,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text("Breathe Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Prompt Spark Chamber (Horizontal Row of Energy Selectors)
            item {
                Column {
                    Text(
                        text = "Reflective Sparks",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Select an energy to craft a custom daily reflection prompt:",
                        fontSize = 12.sp,
                        color = com.example.ui.theme.CleanTextMuted,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val energies = listOf(
                        Triple("🌸 Calm", "Calm & Peace", Color(0xFF0D9488)),
                        Triple("🔥 Creative", "Creative Inspiration", Color(0xFFD946EF)),
                        Triple("🌱 Gratitude", "Grateful Heart", Color(0xFF16A34A)),
                        Triple("🌌 Wisdom", "Deep Philosophy", Color(0xFF6366F1))
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(energies) { (label, theme, color) ->
                            Card(
                                onClick = { viewModel.generateReflectivePrompt(theme) },
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(90.dp)
                                    .testTag("energy_card_${label.lowercase()}"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(color.copy(alpha = 0.15f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = color,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Study Spark Generator (Flashcards creator)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("flashcard_generator_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "AI Study Deck Builder",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Type any topic below to let Gemini generate a smart study flashcard deck saved offline.",
                            fontSize = 12.sp,
                            color = com.example.ui.theme.CleanTextMuted
                        )

                        var topicInput by remember { mutableStateOf("") }
                        val isGeneratingDeck by viewModel.isGeneratingDeck.collectAsStateWithLifecycle()
                        val deckError by viewModel.deckError.collectAsStateWithLifecycle()

                        OutlinedTextField(
                            value = topicInput,
                            onValueChange = { topicInput = it },
                            placeholder = { Text("e.g. Stoicism core values, Python basic syntax", fontSize = 12.sp, color = com.example.ui.theme.CleanTextMuted) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("study_topic_input")
                        )

                        if (deckError != null) {
                            Text(
                                text = deckError ?: "",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.generateStudyDeck(topicInput)
                                topicInput = ""
                            },
                            enabled = topicInput.isNotBlank() && !isGeneratingDeck,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White,
                                disabledContainerColor = MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("generate_deck_button")
                        ) {
                            if (isGeneratingDeck) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Text("Assemble Study Deck", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Saved Study Decks Section
            if (studyDecks.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Your Study Decks",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        studyDecks.forEach { deck ->
                            Card(
                                onClick = { viewModel.selectDeckForViewing(deck) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("deck_item_${deck.id}"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(MaterialTheme.colorScheme.secondary, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.School,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = deck.title,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = deck.description,
                                                fontSize = 12.sp,
                                                color = com.example.ui.theme.CleanTextMuted,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteDeck(deck.id) },
                                        modifier = Modifier.testTag("delete_deck_button_${deck.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Deck",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Saved Journals List Header
            item {
                Text(
                    text = "Reflective History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Journal List Items
            if (journalEntries.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.SentimentSatisfied,
                                contentDescription = "Empty History",
                                tint = com.example.ui.theme.CleanTextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your reflecting journal is empty",
                                fontSize = 13.sp,
                                color = com.example.ui.theme.CleanTextMuted
                            )
                            Text(
                                text = "Tap a reflective spark energy above to begin.",
                                fontSize = 11.sp,
                                color = com.example.ui.theme.CleanTextMuted.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            } else {
                items(journalEntries, key = { it.id }) { entry ->
                    JournalHistoryCard(
                        entry = entry,
                        onDelete = { viewModel.deleteJournalEntry(entry.id) }
                    )
                }
            }
        }

        // Fullscreen Overlays

        // 1. Mindful Breathing Overlay
        AnimatedVisibility(
            visible = showBreathingSpace,
            enter = fadeIn(animationSpec = tween(400)),
            exit = fadeOut(animationSpec = tween(400))
        ) {
            BreathingOverlayScreen(
                onClose = { viewModel.showBreathingSpace.value = false }
            )
        }

        // 2. Reflective Writing Overlay Dialog
        AnimatedVisibility(
            visible = showJournalWriter,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(400)) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(400)) + fadeOut()
        ) {
            JournalWriterOverlayScreen(
                viewModel = viewModel,
                onClose = { viewModel.showJournalWriter.value = false }
            )
        }

        // 3. Flashcard Study Deck Viewer Overlay
        AnimatedVisibility(
            visible = showDeckViewer != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            showDeckViewer?.let { deck ->
                val cards by viewModel.activeDeckCards.collectAsStateWithLifecycle()
                FlashcardDeckViewerScreen(
                    deck = deck,
                    cards = cards,
                    onClose = { viewModel.showDeckViewer.value = null }
                )
            }
        }
    }
}

// Sub-component for individual journal history rows with expand transitions
@Composable
fun JournalHistoryCard(
    entry: JournalEntry,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("journal_history_card_${entry.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = entry.mood,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    val dateFormatted = remember {
                        val date = Date(entry.timestamp)
                        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        sdf.format(date)
                    }
                    Text(
                        text = dateFormatted,
                        fontSize = 12.sp,
                        color = com.example.ui.theme.CleanTextMuted
                    )
                }

                IconButton(
                    onClick = { onDelete() },
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("delete_journal_button_${entry.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Journal",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = entry.prompt,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            AnimatedVisibility(visible = expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "Your Reflection:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = entry.content,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// Mindful Breathing Screen
@Composable
fun BreathingOverlayScreen(
    onClose: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breath")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Calculate breath message based on expand state
    val message = if (pulse > 1.1f) {
        "Hold gently..."
    } else if (pulse > 0.95f) {
        "Inhale light..."
    } else {
        "Exhale slowly..."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(enabled = false) {} // block clickthroughs
            .testTag("breathing_overlay_root"),
        contentAlignment = Alignment.Center
    ) {
        // Backdrop
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                        .testTag("close_breathing_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Screen", tint = MaterialTheme.colorScheme.primary)
                }
            }

            // Glowing Breathing Concentric Circles
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                // outer ring
                Box(
                    modifier = Modifier
                        .size(240.dp * pulse)
                        .graphicsLayer(alpha = 0.2f)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                // inner pulsing circle
                Box(
                    modifier = Modifier
                        .size(160.dp * pulse)
                        .graphicsLayer(alpha = 0.4f)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                // core solid center
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = message,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Release your shoulders. Relax your eyes. Rest in the present moment.",
                    fontSize = 12.sp,
                    color = com.example.ui.theme.CleanTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}

// Reflective Journal Writer Room Screen
@Composable
fun JournalWriterOverlayScreen(
    viewModel: ZenViewModel,
    onClose: () -> Unit
) {
    val activePrompt by viewModel.activePrompt.collectAsStateWithLifecycle()
    val activeContent by viewModel.activeJournalContent.collectAsStateWithLifecycle()
    val isGeneratingResponse by viewModel.isGeneratingResponse.collectAsStateWithLifecycle()
    val aiResponse by viewModel.aiReflectiveResponse.collectAsStateWithLifecycle()
    val activeMood by viewModel.activeMood.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(enabled = false) {}
            .testTag("journal_writer_overlay_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Close Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reflection Chamber",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                        .testTag("close_writer_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Screen", tint = MaterialTheme.colorScheme.primary)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Prompt Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Prompt",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "Your Guide's Reflection Prompt",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activePrompt,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // AI Response / Feedback Room (If exists)
                if (aiResponse != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_reflection_feedback_card"),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SelfImprovement,
                                        contentDescription = "Mindfulness Guide",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "AI Companion Reflection",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = aiResponse ?: "",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                } else {
                    // Mood Selector and Text Editor
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "How is your alignment right now?",
                                fontSize = 12.sp,
                                color = com.example.ui.theme.CleanTextMuted
                            )

                            val moods = listOf("Peaceful", "Inspired", "Grateful", "Anxious", "Neutral")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(moods) { mood ->
                                    val isSelected = activeMood == mood
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { viewModel.activeMood.value = mood }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                            .testTag("mood_chip_$mood")
                                    ) {
                                        Text(
                                            text = mood,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = activeContent,
                            onValueChange = { viewModel.activeJournalContent.value = it },
                            placeholder = {
                                Text(
                                    text = "Pour your thoughts, feelings, and heart here... no judgment.",
                                    fontSize = 13.sp,
                                    color = com.example.ui.theme.CleanTextMuted
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .testTag("journal_input_text"),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            )
                        )
                    }

                    item {
                        Button(
                            onClick = { viewModel.saveJournalAndGetFeedback() },
                            enabled = activeContent.isNotBlank() && !isGeneratingResponse,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White,
                                disabledContainerColor = MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("commit_to_zen_button")
                        ) {
                            if (isGeneratingResponse) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Text("Commit to Zen", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Flashcard Study Deck Viewer Screen
@Composable
fun FlashcardDeckViewerScreen(
    deck: StudyDeck,
    cards: List<com.example.data.Flashcard>,
    onClose: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var showBack by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(enabled = false) {}
            .testTag("flashcard_viewer_overlay_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deck.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Study Active Space",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                        .testTag("close_deck_viewer_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Screen", tint = MaterialTheme.colorScheme.primary)
                }
            }

            if (cards.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                val currentCard = cards[currentIndex]

                // Flip Card Animation setup
                val rotation by animateFloatAsState(
                    targetValue = if (showBack) 180f else 0f,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                    label = "flip"
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Flash Card Box
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 12f * density
                            }
                            .clickable { showBack = !showBack }
                            .testTag("flashcard_box"),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, if (showBack) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (rotation > 90f) {
                                // BACK SIDE (Definition / Mindful Answer)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SelfImprovement,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = currentCard.back,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 24.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "TAP TO FLIP FRONT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = com.example.ui.theme.CleanTextMuted
                                    )
                                }
                            } else {
                                // FRONT SIDE (Question / Core Term)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = currentCard.front,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "TAP TO FLIP BACK",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = com.example.ui.theme.CleanTextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Progress indicators
                    Text(
                        text = "Card ${currentIndex + 1} of ${cards.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Nav Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (currentIndex > 0) {
                                showBack = false
                                currentIndex--
                            }
                        },
                        enabled = currentIndex > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("prev_card_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Prev")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Prev")
                    }

                    Button(
                        onClick = {
                            if (currentIndex < cards.size - 1) {
                                showBack = false
                                currentIndex++
                            }
                        },
                        enabled = currentIndex < cards.size - 1,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("next_card_button")
                    ) {
                        Text("Next")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                }
            }
        }
    }
}
