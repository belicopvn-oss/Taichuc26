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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MODULES_LIST
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.RoseError

@Composable
fun PartsHomeScreen(
    uiState: QuizUiState,
    onResumeClick: () -> Unit,
    onStudyAll300Click: () -> Unit,
    onOpenModuleOrPack: (String) -> Unit,
    onRequestReset: () -> Unit,
    onNavigateTab: (AppTab) -> Unit
) {
    val answered = uiState.answeredCount
    val correct = uiState.correctCount
    val wrong = uiState.wrongCount
    val saved = uiState.savedCount
    val progressFraction = (answered.toFloat() / 300f).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("parts_home_list"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Overall Progress Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "TIẾN ĐỘ ĐÃ LƯU TỰ ĐỘNG",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.darkTheme) GoldAccent else CrimsonRed
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Đã làm: $answered / 300 câu",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = onResumeClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("home_resume_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Học tiếp Câu #${uiState.lastQuestionId}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Gradient Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction)
                                .height(10.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(CrimsonRed, EmeraldGreen)
                                    )
                                )
                        )
                    }

                    // 3 Stat Tiles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatSummaryTile(
                            count = correct,
                            label = "Câu Đúng",
                            valueColor = EmeraldGreen,
                            containerColor = if (uiState.darkTheme) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFFECFDF5),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onStudyAll300Click() }
                        )
                        StatSummaryTile(
                            count = wrong,
                            label = "Làm Lại (Sai)",
                            valueColor = RoseError,
                            containerColor = if (uiState.darkTheme) Color(0xFF881337).copy(alpha = 0.4f) else Color(0xFFFFF1F2),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateTab(AppTab.REDO) }
                        )
                        StatSummaryTile(
                            count = saved,
                            label = "Đã Ghi Nhớ",
                            valueColor = if (uiState.darkTheme) GoldAccent else GoldDark,
                            containerColor = if (uiState.darkTheme) Color(0xFF78350F).copy(alpha = 0.4f) else Color(0xFFFEFCE8),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateTab(AppTab.SAVED) }
                        )
                    }
                }
            }
        }

        // Section Header: 5 Modules
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📚 ÔN LUYỆN THEO 5 CHUYÊN ĐỀ",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Học toàn bộ 300 câu →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.darkTheme) GoldAccent else CrimsonRed,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onStudyAll300Click() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("study_all_300_quick_button")
                )
            }
        }

        // 5 Module Cards
        items(MODULES_LIST, key = { it.id }) { module ->
            val moduleQuestions = uiState.questions.filter { it.module == module.id }
            val totalMod = moduleQuestions.size.coerceAtLeast(60)
            val doneMod = moduleQuestions.count { uiState.progressMap[it.id]?.status != null }
            val modFraction = (doneMod.toFloat() / totalMod.toFloat()).coerceIn(0f, 1f)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenModuleOrPack("mod_${module.id}") }
                    .testTag("module_card_${module.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = module.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$doneMod/$totalMod",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                        }

                        Text(
                            text = module.sub,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(modFraction)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (uiState.darkTheme) GoldAccent else CrimsonRed)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (uiState.darkTheme) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFFEF2F2),
                        modifier = Modifier.clickable { onOpenModuleOrPack("mod_${module.id}") }
                    ) {
                        Text(
                            text = "Ôn ngay",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.darkTheme) GoldAccent else CrimsonRed,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Section Header: 10 Packs of 30
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎲 ÔN THEO 10 GÓI (30 CÂU/GÓI)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick = onRequestReset,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("reset_progress_button")
                ) {
                    Text(
                        text = "Xóa làm lại từ đầu",
                        fontSize = 11.sp,
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 10 Packs Grid (5 rows of 2 columns)
        items((1..10).chunked(2)) { rowPacks ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (packNum in rowPacks) {
                    val startId = (packNum - 1) * 30 + 1
                    val endId = packNum * 30
                    val packQs = uiState.questions.filter { it.pack30 == packNum }
                    val donePack = packQs.count { uiState.progressMap[it.id]?.status != null }
                    val isComplete = donePack >= 30

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenModuleOrPack("pack_$packNum") }
                            .testTag("pack_card_$packNum")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Gói #$packNum",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Câu $startId–$endId",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isComplete) Color(0xFFD1FAE5) else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "$donePack/30",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isComplete) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Footer
        item {
            Text(
                text = "Ứng dụng Ôn thi Nhận thức Chính trị 2026 • Biên soạn & Phát triển: Nguyễn Thanh Tài",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun StatSummaryTile(
    count: Int,
    label: String,
    valueColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = valueColor
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
