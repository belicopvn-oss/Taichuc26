package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoseError
import java.util.Locale

private val EXAM_COUNT_OPTIONS = listOf(
    "30" to "Đề chuẩn: 30 câu (30 phút)",
    "50" to "Đề tổng hợp: 50 câu (45 phút)",
    "15" to "Đề kiểm tra nhanh: 15 câu (15 phút)"
)

private val EXAM_PART_OPTIONS = listOf(
    "all" to "Toàn bộ 300 câu (5 Phần)",
    "1" to "Chỉ Phần I: Chuyên đề Sĩ quan 2026",
    "2" to "Chỉ Phần II: Tư tưởng, đạo đức HCM",
    "3" to "Chỉ Phần III: ĐH Đảng bộ Quân đội XII",
    "4" to "Chỉ Phần IV: Đại hội XIV của Đảng",
    "5" to "Chỉ Phần V: Hội nghị TW 2, 3, 4"
)

@Composable
fun ExamScreen(
    uiState: QuizUiState,
    onSelectExamCount: (Int) -> Unit,
    onSelectExamPart: (String) -> Unit,
    onStartExam: () -> Unit,
    onPickExamOption: (Int) -> Unit,
    onNavigateExamQuestion: (Int) -> Unit,
    onSubmitExam: () -> Unit,
    onQuitExam: () -> Unit,
    onGoRedoTab: () -> Unit
) {
    val exam = uiState.exam
    val letters = listOf("A", "B", "C", "D")

    when {
        exam.isActive && exam.items.isNotEmpty() -> {
            val currentItem = exam.items[exam.currentIndex.coerceIn(0, exam.items.lastIndex)]
            val answeredCount = exam.items.count { it.pickedShuffledIndex != null }
            val mins = (exam.remainingSeconds / 60).coerceAtLeast(0)
            val secs = (exam.remainingSeconds % 60).coerceAtLeast(0)
            val timeFormatted = String.format(Locale.US, "%02d:%02d", mins, secs)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("exam_active_screen"),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Timer & Submit Bar
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
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFFE4E6)
                                ) {
                                    Text(
                                        text = "⏳ $timeFormatted",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = RoseError,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }

                                Text(
                                    text = "Đã chọn: $answeredCount/${exam.items.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = onSubmitExam,
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("submit_exam_button")
                                ) {
                                    Text(
                                        text = "✓ Nộp bài",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = onQuitExam,
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("quit_exam_button")
                                ) {
                                    Text("Thoát", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Active Question Card
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                ) {
                                    Text(
                                        text = "Câu ${exam.currentIndex + 1}/${exam.items.size}",
                                        color = MaterialTheme.colorScheme.surface,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }

                                Text(
                                    text = "Mã #${currentItem.question.id}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = currentItem.question.question,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                currentItem.options.forEachIndexed { sIdx, shuffledOpt ->
                                    val isSelected = currentItem.pickedShuffledIndex == sIdx
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelected) {
                                            if (uiState.darkTheme) Color(0xFF7F1D1D).copy(alpha = 0.5f) else Color(0xFFFEF2F2)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        },
                                        border = BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) CrimsonRed else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable { onPickExamOption(sIdx) }
                                            .testTag("exam_option_$sIdx")
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
                                                    .background(
                                                        if (isSelected) CrimsonRed else MaterialTheme.colorScheme.surfaceVariant
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = letters.getOrElse(sIdx) { "A" },
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Text(
                                                text = shuffledOpt.text,
                                                fontSize = 14.sp,
                                                lineHeight = 20.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onNavigateExamQuestion(-1) },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(vertical = 12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("exam_prev_button")
                                ) {
                                    Text("← Câu trước", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Button(
                                    onClick = { onNavigateExamQuestion(1) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                    contentPadding = PaddingValues(vertical = 12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("exam_next_button")
                                ) {
                                    Text("Câu tiếp →", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        exam.isFinished -> {
            val passed = exam.score10 >= 5.0f
            val scoreFormatted = String.format(Locale.US, "%.1f", exam.score10)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("exam_result_screen"),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (passed) Color(0xFFD1FAE5) else Color(0xFFFFE4E6)
                            ) {
                                Text(
                                    text = if (passed) "ĐẠT YÊU CẦU" else "CẦN ÔN LẠI",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (passed) EmeraldGreen else RoseError,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "$scoreFormatted / 10 điểm",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = if (uiState.darkTheme) GoldAccent else CrimsonRed
                            )

                            Text(
                                text = "Số câu đúng: ${exam.correctCount}/${exam.items.size} (Các câu sai đã tự động lưu sang mục Làm Lại)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onGoRedoTab,
                                    colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("exam_go_redo_button")
                                ) {
                                    Text(
                                        text = "🔄 Xem câu vừa sai",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                Button(
                                    onClick = onQuitExam,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("exam_new_button")
                                ) {
                                    Text(
                                        text = "Làm đề mới",
                                        color = MaterialTheme.colorScheme.surface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Detailed Review List
                itemsIndexed(exam.items, key = { _, it -> it.question.id }) { idx, item ->
                    val pickedOpt = item.pickedShuffledIndex?.let { item.options.getOrNull(it) }
                    val isOk = pickedOpt != null && pickedOpt.originalIndex == item.question.answer
                    val corrShuffledIdx = item.options.indexOfFirst { it.originalIndex == item.question.answer }.coerceAtLeast(0)
                    val corrText = item.options.getOrNull(corrShuffledIdx)?.text.orEmpty()
                    val highlightedCorr = buildHighlightedKeywordText(
                        fullText = corrText,
                        keyword = item.question.keyword,
                        isDark = uiState.darkTheme
                    )

                    val answerLine = buildAnnotatedString {
                        append(
                            AnnotatedString(
                                text = "Đáp án đúng: ${letters.getOrElse(corrShuffledIdx) { "A" }}. ",
                                spanStyle = SpanStyle(
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        )
                        append(highlightedCorr)
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isOk) {
                                if (uiState.darkTheme) Color(0xFF064E3B).copy(alpha = 0.25f) else Color(0xFFECFDF5)
                            } else {
                                if (uiState.darkTheme) Color(0xFF881337).copy(alpha = 0.25f) else Color(0xFFFFF1F2)
                            }
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isOk) EmeraldGreen.copy(alpha = 0.5f) else RoseError.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = if (isOk) {
                                    "✓ Câu ${idx + 1} (Đúng) • Mã #${item.question.id}"
                                } else {
                                    "✗ Câu ${idx + 1} (Sai) • Mã #${item.question.id}"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isOk) EmeraldGreen else RoseError
                            )
                            Text(
                                text = item.question.question,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = answerLine,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        else -> {
            // Setup Card
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("exam_setup_screen"),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "⏱️ Thi Thử Ngẫu Nhiên",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Rút ngẫu nhiên câu hỏi và tự động đảo vị trí đáp án A, B, C, D.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Số lượng câu hỏi",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                FilterDropdownSelector(
                                    selectedKey = exam.countOption.toString(),
                                    options = EXAM_COUNT_OPTIONS,
                                    onSelect = { onSelectExamCount(it.toIntOrNull() ?: 30) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("exam_count_selector")
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Phạm vi câu hỏi",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                FilterDropdownSelector(
                                    selectedKey = exam.partOption,
                                    options = EXAM_PART_OPTIONS,
                                    onSelect = onSelectExamPart,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("exam_part_selector")
                                )
                            }

                            Button(
                                onClick = onStartExam,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                contentPadding = PaddingValues(vertical = 14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("start_exam_button")
                            ) {
                                Text(
                                    text = "🚀 Bắt Đầu Làm Bài Thi",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
