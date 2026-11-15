package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PoliticalQuestion
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.RoseError

private val SAVED_FILTER_OPTIONS = listOf(
    "starred" to "Đã đánh dấu sao",
    "all_keys" to "Tất cả 300 từ khóa"
)

@Composable
fun SavedNotesScreen(
    uiState: QuizUiState,
    onSelectSavedFilter: (String) -> Unit,
    onJumpToQuestion: (Int) -> Unit
) {
    val letters = listOf("A", "B", "C", "D")
    val listToDisplay = if (uiState.savedFilterMode == "starred") {
        uiState.questions.filter { uiState.progressMap[it.id]?.saved == true }
    } else {
        uiState.questions
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("saved_notes_screen"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⭐ Sổ Tay Ghi Nhớ Từ Khóa",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Ôn nhanh các câu đã bấm Lưu ghi nhớ hoặc xem từ khóa 300 câu.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterDropdownSelector(
                        selectedKey = uiState.savedFilterMode,
                        options = SAVED_FILTER_OPTIONS,
                        onSelect = onSelectSavedFilter,
                        modifier = Modifier
                            .width(155.dp)
                            .testTag("saved_mode_selector")
                    )
                }
            }
        }

        if (listToDisplay.isEmpty()) {
            item {
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "⭐", fontSize = 32.sp)
                        Text(
                            text = "Bạn chưa lưu câu hỏi nào vào mục Ghi nhớ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Khi học ở tab Học 300, hãy bấm nút \"Ghi nhớ\" ở những câu khó nhớ hoặc xem toàn bộ 300 từ khóa.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { onSelectSavedFilter("all_keys") },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("view_all_300_keywords_button")
                        ) {
                            Text(
                                text = "Xem tất cả 300 từ khóa",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        } else {
            items(listToDisplay, key = { it.id }) { q ->
                val correctAnsText = q.options.getOrElse(q.answer) { "" }
                val highlightedAns = buildHighlightedKeywordText(
                    fullText = correctAnsText,
                    keyword = q.keyword,
                    isDark = uiState.darkTheme
                )
                val fullAnswerLine = buildAnnotatedString {
                    append(
                        AnnotatedString(
                            text = "✓ Đáp án ${letters.getOrElse(q.answer) { "A" }}: ",
                            spanStyle = SpanStyle(
                                color = EmeraldGreen,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    )
                    append(highlightedAns)
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onJumpToQuestion(q.id) }
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
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (uiState.darkTheme) Color(0xFF78350F) else Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = "Câu #${q.id}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (uiState.darkTheme) GoldAccent else GoldDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Text(
                                text = "Mở câu này →",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.darkTheme) GoldAccent else CrimsonRed
                            )
                        }

                        Text(
                            text = q.question,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = fullAnswerLine,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )

                        Text(
                            text = "🔑 Từ khóa nhớ nhanh: \"${q.keyword}\"",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.darkTheme) GoldAccent else GoldDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RedoWrongScreen(
    uiState: QuizUiState,
    onAnswerRedoQuestion: (PoliticalQuestion, Int) -> Unit,
    onClearAllRedo: () -> Unit
) {
    val wrongQuestions = uiState.questions.filter { uiState.progressMap[it.id]?.status == "wrong" }
    val letters = listOf("A", "B", "C", "D")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("redo_wrong_screen"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🔄 Làm Lại Các Câu Chưa Đúng (${wrongQuestions.size})",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Mọi câu bạn chọn sai đều tự động gom về đây để bạn làm lại cho thuộc.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (wrongQuestions.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = onClearAllRedo,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("clear_redo_list_button")
                        ) {
                            Text("Xóa hết", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        if (wrongQuestions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🎉", fontSize = 36.sp)
                        Text(
                            text = "Không có câu nào tồn đọng cần làm lại!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Khi bạn chọn sai ở mục Học 300 Câu hoặc Thi Thử, câu hỏi đó sẽ tự động xuất hiện tại đây.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(wrongQuestions, key = { it.id }) { q ->
                val feedbackMsg = uiState.redoInlineFeedback[q.id]

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFE4E6)
                            ) {
                                Text(
                                    text = "Câu #${q.id}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RoseError,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "Chọn lại đáp án đúng để hoàn thành",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = q.question,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            q.options.forEachIndexed { idx, optText ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onAnswerRedoQuestion(q, idx) }
                                        .testTag("redo_q_${q.id}_opt_$idx")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "${letters.getOrElse(idx) { "A" }}.",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp,
                                            color = CrimsonRed
                                        )
                                        Text(
                                            text = optText,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        if (feedbackMsg != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (uiState.darkTheme) Color(0xFF881337).copy(alpha = 0.4f) else Color(0xFFFFF1F2),
                                border = BorderStroke(1.dp, RoseError)
                            ) {
                                Text(
                                    text = feedbackMsg,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (uiState.darkTheme) Color(0xFFFECDD3) else RoseError,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
