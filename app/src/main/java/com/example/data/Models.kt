package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PoliticalQuestion(
    val id: Int,
    val module: Int,
    val pack30: Int,
    val question: String,
    val options: List<String>,
    val answer: Int,
    val keyword: String
)

data class ModuleTopic(
    val id: Int,
    val name: String,
    val sub: String
)

val MODULES_LIST = listOf(
    ModuleTopic(
        id = 1,
        name = "Phần I: Chuyên đề Sĩ quan năm 2026",
        sub = "60 câu • Xây dựng Quân đội hiện đại"
    ),
    ModuleTopic(
        id = 2,
        name = "Phần II: Tư tưởng, đạo đức, phong cách Hồ Chí Minh",
        sub = "60 câu • Tình thương yêu đồng chí, đồng đội"
    ),
    ModuleTopic(
        id = 3,
        name = "Phần III: Đại hội Đảng bộ Quân đội lần thứ XII",
        sub = "60 câu • Nhiệm kỳ 2025 – 2030"
    ),
    ModuleTopic(
        id = 4,
        name = "Phần IV: Đại hội đại biểu toàn quốc lần thứ XIV",
        sub = "60 câu • Quốc phòng, an ninh trong tình hình mới"
    ),
    ModuleTopic(
        id = 5,
        name = "Phần V: Hội nghị Trung ương 2, 3, 4 khóa XIV",
        sub = "60 câu • Các Nghị quyết & Kết luận mới"
    )
)

@Entity(tableName = "question_progress")
data class QuestionProgressEntity(
    @PrimaryKey val questionId: Int,
    val chosenOption: Int? = null,
    val status: String? = null, // "correct" | "wrong" | null
    val saved: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_pref")
data class AppPrefEntity(
    @PrimaryKey val id: Int = 1,
    val darkTheme: Boolean = false,
    val lastQuestionId: Int = 1,
    val filterPart: String = "all",
    val filterState: String = "all"
)

data class ShuffledOption(
    val text: String,
    val originalIndex: Int
)

data class ExamQuestionItem(
    val question: PoliticalQuestion,
    val options: List<ShuffledOption>,
    val pickedShuffledIndex: Int? = null
)
