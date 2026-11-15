package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldSurfaceLight
import com.example.ui.theme.Slate900

fun buildHighlightedKeywordText(
    fullText: String,
    keyword: String,
    isDark: Boolean
): AnnotatedString {
    if (keyword.length < 3) return AnnotatedString(fullText)
    val startIndex = fullText.indexOf(keyword, ignoreCase = true)
    if (startIndex < 0) return AnnotatedString(fullText)

    val highlightBg = if (isDark) GoldDark else Color(0xFFFDE047)
    val highlightFg = if (isDark) GoldSurfaceLight else Color(0xFF713F12)

    return buildAnnotatedString {
        append(fullText)
        addStyle(
            style = SpanStyle(
                background = highlightBg,
                color = highlightFg,
                fontWeight = FontWeight.ExtraBold
            ),
            start = startIndex,
            end = (startIndex + keyword.length).coerceAtMost(fullText.length)
        )
    }
}

@Composable
fun TopPoliticalAppBar(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenInfo: () -> Unit
) {
    val barBg = if (darkTheme) Slate900 else CrimsonRed
    Surface(
        color = barBg,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "300",
                        color = Color(0xFF7F1D1D),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Trắc Nghiệm Chính Trị 2026",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Tác giả: Nguyễn Thanh Tài",
                        color = if (darkTheme) GoldAccent else Color(0xFFFDE68A),
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenInfo,
                    modifier = Modifier.testTag("info_dialog_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Thông tin ứng dụng",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Đổi nền Sáng/Tối",
                        tint = if (darkTheme) GoldAccent else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun AppInfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "📲 Chính Trị 300 (Android App)",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Ứng dụng Ôn thi 300 câu Trắc nghiệm Nhận thức Chính trị năm 2026.",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "• Biên soạn & Phát triển: Nguyễn Thanh Tài\n" +
                        "• 5 Chuyên đề trọng tâm (60 câu/phần) & 10 Gói chia nhỏ (30 câu/gói)\n" +
                        "• Tự động lưu tiến độ học tập, câu làm sai và sổ tay từ khóa ngay trên thiết bị (hoạt động 100% Offline).\n" +
                        "• Hỗ trợ tô sáng từ khóa mẹo nhớ nhanh và chế độ Thi thử ngẫu nhiên đảo đáp án.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                modifier = Modifier.testTag("close_info_dialog_button")
            ) {
                Text("Đã hiểu", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ResetProgressConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Xóa tiến độ làm lại từ đầu?",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp
            )
        },
        text = {
            Text(
                text = "Toàn bộ câu đã làm, danh sách câu sai và câu ghi nhớ sẽ được đặt lại về trạng thái ban đầu.",
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                modifier = Modifier.testTag("confirm_reset_button")
            ) {
                Text("Xóa & Làm lại", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
