package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PoliticalQuestion
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.RoseError

private val PART_FILTER_OPTIONS = listOf(
    "all" to "Tất cả 300 câu (A-B-C)",
    "mod_1" to "Phần I: Chuyên đề Sĩ quan (60 câu)",
    "mod_2" to "Phần II: Tư tưởng, đạo đức HCM (60 câu)",
    "mod_3" to "Phần III: ĐH Đảng bộ Quân đội XII (60 câu)",
    "mod_4" to "Phần IV: Đại hội XIV của Đảng (60 câu)",
    "mod_5" to "Phần V: Hội nghị TW 2, 3, 4 (60 câu)",
    "pack_1" to "Gói 1: Câu 1 – 30",
    "pack_2" to "Gói 2: Câu 31 – 60",
    "pack_3" to "Gói 3: Câu 61 – 90",
    "pack_4" to "Gói 4: Câu 91 – 120",
    "pack_5" to "Gói 5: Câu 121 – 150",
    "pack_6" to "Gói 6: Câu 151 – 180",
    "pack_7" to "Gói 7: Câu 181 – 210",
    "pack_8" to "Gói 8: Câu 211 – 240",
    "pack_9" to "Gói 9: Câu 241 – 270",
    "pack_10" to "Gói 10: Câu 271 – 300"
)

private val STATE_FILTER_OPTIONS = listOf(
    "all" to "Mọi câu hỏi",
    "unanswered" to "⚪ Câu chưa làm",
    "answered" to "✅ Câu đã làm",
    "wrong" to "🔄 Câu làm sai",
    "saved" to "⭐ Câu đã ghi nhớ"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Study300Screen(
    uiState: QuizUiState,
    onSelectPartFilter: (String) -> Unit,
    onSelectStateFilter: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenPalette: () -> Unit,
    onClosePalette: () -> Unit,
    onPickPaletteIndex: (Int) -> Unit,
    onSelectOption: (PoliticalQuestion, Int) -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onRedoSingleQuestion: (Int) -> Unit,
    onNavigateQuestion: (Int) -> Unit
) {
    val filteredList = uiState.filteredStudyQuestions
    val currentQuestion = uiState.currentStudyQuestion
    val safeIndex = if (filteredList.isEmpty()) 0 else uiState.studyIndex.coerceIn(0, filteredList.lastIndex)
    val letters = listOf("A", "B", "C", "D")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("study_300_screen"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Filter Bar Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterDropdownSelector(
                            selectedKey = uiState.filterPart,
                            options = PART_FILTER_OPTIONS,
                            onSelect = onSelectPartFilter,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("study_part_filter_selector")
                        )

                        FilterDropdownSelector(
                            selectedKey = uiState.filterState,
                            options = STATE_FILTER_OPTIONS,
                            onSelect = onSelectStateFilter,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("study_state_filter_selector")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = {
                                Text(
                                    text = "Tìm số câu, nội dung, từ khóa...",
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Xóa tìm kiếm",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("study_search_input")
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { onOpenPalette() }
                                .testTag("open_palette_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Chọn (${filteredList.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Question Content Card
        item {
            if (currentQuestion == null) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Không có câu hỏi nào trong mục lọc này.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        TextButtonOnEmpty(onResetFilter = {
                            onSelectPartFilter("all")
                            onSelectStateFilter("all")
                            onSearchQueryChange("")
                        })
                    }
                }
            } else {
                val qProgress = uiState.progressMap[currentQuestion.id]
                val hasAnswered = qProgress?.chosenOption != null
                val chosenIdx = qProgress?.chosenOption
                val isSaved = qProgress?.saved == true

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Top Row: Question Badge, Status Badge, Bookmark Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CrimsonRed
                                ) {
                                    Text(
                                        text = "Câu ${currentQuestion.id} (${safeIndex + 1}/${filteredList.size})",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }

                                val (statusText, statusBg, statusFg) = when (qProgress?.status) {
                                    "correct" -> Triple(
                                        "✓ Đã chọn ${letters.getOrElse(chosenIdx ?: 0) { "A" }} (Đúng)",
                                        Color(0xFFD1FAE5),
                                        EmeraldGreen
                                    )
                                    "wrong" -> Triple(
                                        "✗ Chọn ${letters.getOrElse(chosenIdx ?: 0) { "A" }} (Đúng: ${letters.getOrElse(currentQuestion.answer) { "A" }})",
                                        Color(0xFFFFE4E6),
                                        RoseError
                                    )
                                    else -> Triple(
                                        "⚪ Chưa làm",
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = statusBg
                                ) {
                                    Text(
                                        text = statusText,
                                        color = statusFg,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSaved) Color(0xFFFEF08A) else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSaved) GoldDark else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier
                                    .clickable { onToggleBookmark(currentQuestion.id) }
                                    .testTag("toggle_bookmark_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = null,
                                        tint = if (isSaved) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (isSaved) "Đã nhớ" else "Ghi nhớ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSaved) GoldDark else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                        // Question Text
                        Text(
                            text = currentQuestion.question,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 23.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("study_question_text")
                        )

                        // 4 Options
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            currentQuestion.options.forEachIndexed { idx, optText ->
                                val isAnswer = idx == currentQuestion.answer
                                val isChosen = chosenIdx == idx

                                val borderColor = when {
                                    hasAnswered && isAnswer -> EmeraldGreen
                                    hasAnswered && isChosen && !isAnswer -> RoseError
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }

                                val containerColor = when {
                                    hasAnswered && isAnswer -> if (uiState.darkTheme) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFFECFDF5)
                                    hasAnswered && isChosen && !isAnswer -> if (uiState.darkTheme) Color(0xFF881337).copy(alpha = 0.5f) else Color(0xFFFFF1F2)
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }

                                val badgeBg = when {
                                    hasAnswered && isAnswer -> EmeraldGreen
                                    hasAnswered && isChosen && !isAnswer -> RoseError
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }

                                val badgeFg = when {
                                    hasAnswered && (isAnswer || isChosen) -> Color.White
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                val displayAnnotated: AnnotatedString = if (hasAnswered && isAnswer) {
                                    buildHighlightedKeywordText(
                                        fullText = optText,
                                        keyword = currentQuestion.keyword,
                                        isDark = uiState.darkTheme
                                    )
                                } else {
                                    AnnotatedString(optText)
                                }

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = containerColor,
                                    border = BorderStroke(
                                        width = if (hasAnswered && (isAnswer || isChosen)) 1.5.dp else 1.dp,
                                        color = borderColor
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { onSelectOption(currentQuestion, idx) }
                                        .testTag("study_option_$idx")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(badgeBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = letters.getOrElse(idx) { "A" },
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = badgeFg
                                            )
                                        }

                                        Text(
                                            text = displayAnnotated,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        // Keyword Tip Box after answering
                        if (hasAnswered) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (uiState.darkTheme) Color(0xFF78350F).copy(alpha = 0.35f) else Color(0xFFFEFCE8),
                                border = BorderStroke(
                                    1.dp,
                                    if (uiState.darkTheme) GoldDark else Color(0xFFFDE047)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val isCorrect = chosenIdx == currentQuestion.answer
                                        Text(
                                            text = if (isCorrect) {
                                                "✓ CHÍNH XÁC!"
                                            } else {
                                                "✗ CHƯA ĐÚNG (Đã lưu vào Làm Lại) • Đáp án: ${letters.getOrElse(currentQuestion.answer) { "A" }}"
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isCorrect) EmeraldGreen else RoseError,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Text(
                                            text = "Làm lại câu này",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            textDecoration = TextDecoration.Underline,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .clickable { onRedoSingleQuestion(currentQuestion.id) }
                                                .padding(4.dp)
                                                .testTag("redo_single_question_button")
                                        )
                                    }

                                    Text(
                                        text = "🔑 Mẹo ghi nhớ: \"${currentQuestion.keyword}\"",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.darkTheme) GoldAccent else Color(0xFF92400E)
                                    )
                                }
                            }
                        }

                        // Prev / Next Thumb Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onNavigateQuestion(-1) },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(vertical = 13.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("study_prev_button")
                            ) {
                                Text(
                                    text = "← Câu trước",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Button(
                                onClick = { onNavigateQuestion(1) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                contentPadding = PaddingValues(vertical = 13.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("study_next_button")
                            ) {
                                Text(
                                    text = "Câu tiếp theo →",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Question Picker Bottom Sheet (Palette)
    if (uiState.showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = onClosePalette
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chọn nhanh câu hỏi (${filteredList.size} câu)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendDot(color = EmeraldGreen, label = "Đúng")
                    LegendDot(color = RoseError, label = "Sai")
                    LegendDot(color = MaterialTheme.colorScheme.surfaceVariant, label = "Chưa làm")
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    itemsIndexed(filteredList, key = { _, q -> q.id }) { idx, q ->
                        val st = uiState.progressMap[q.id]?.status
                        val isCurrent = idx == safeIndex
                        val bg = when (st) {
                            "correct" -> EmeraldGreen
                            "wrong" -> RoseError
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        val fg = if (st == "correct" || st == "wrong") {
                            Color.White
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }

                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(bg)
                                .then(
                                    if (isCurrent) {
                                        Modifier.border(2.dp, CrimsonRed, RoundedCornerShape(10.dp))
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { onPickPaletteIndex(idx) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = q.id.toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = fg
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun TextButtonOnEmpty(onResetFilter: () -> Unit) {
    Button(
        onClick = onResetFilter,
        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text("Xem lại tất cả 300 câu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun FilterDropdownSelector(
    selectedKey: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLabel = options.firstOrNull { it.first == selectedKey }?.second ?: options.first().second

    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currentLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (key, label) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (key == selectedKey) FontWeight.ExtraBold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelect(key)
                    }
                )
            }
        }
    }
}
