package com.example.data

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class QuizRepository(
    private val context: Context,
    private val dao: QuizDao
) {
    val allProgressFlow: Flow<List<QuestionProgressEntity>> = dao.getAllProgress()
    val appPrefFlow: Flow<AppPrefEntity?> = dao.getAppPref()

    private var cachedQuestions: List<PoliticalQuestion>? = null

    suspend fun loadQuestions(): List<PoliticalQuestion> = withContext(Dispatchers.IO) {
        cachedQuestions?.let { return@withContext it }
        val json = context.assets.open("questions_300.json").bufferedReader().use { it.readText() }
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val listType = Types.newParameterizedType(List::class.java, PoliticalQuestion::class.java)
        val adapter = moshi.adapter<List<PoliticalQuestion>>(listType)
        val parsed = adapter.fromJson(json).orEmpty().sortedBy { it.id }
        cachedQuestions = parsed
        parsed
    }

    suspend fun answerQuestion(questionId: Int, chosenOption: Int, isCorrect: Boolean) {
        val existing = dao.getProgressById(questionId)
        val updated = QuestionProgressEntity(
            questionId = questionId,
            chosenOption = chosenOption,
            status = if (isCorrect) "correct" else "wrong",
            saved = existing?.saved ?: false,
            updatedAt = System.currentTimeMillis()
        )
        dao.upsertProgress(updated)
    }

    suspend fun toggleBookmark(questionId: Int): Boolean {
        val existing = dao.getProgressById(questionId)
        val newSaved = !(existing?.saved ?: false)
        val updated = QuestionProgressEntity(
            questionId = questionId,
            chosenOption = existing?.chosenOption,
            status = existing?.status,
            saved = newSaved,
            updatedAt = System.currentTimeMillis()
        )
        dao.upsertProgress(updated)
        return newSaved
    }

    suspend fun resetSingleQuestionAnswer(questionId: Int) {
        val existing = dao.getProgressById(questionId) ?: return
        val updated = existing.copy(
            chosenOption = null,
            status = null,
            updatedAt = System.currentTimeMillis()
        )
        dao.upsertProgress(updated)
    }

    suspend fun resetAllProgress() {
        dao.clearAllProgress()
        val currentPref = dao.getAppPrefOnce() ?: AppPrefEntity()
        dao.upsertAppPref(currentPref.copy(lastQuestionId = 1))
    }

    suspend fun clearRedoList() {
        dao.markAllWrongAsCorrect()
    }

    suspend fun saveExamOutcomes(outcomes: List<Pair<PoliticalQuestion, Int?>>) {
        val entities = outcomes.map { (q, pickedOrigIdx) ->
            val existing = dao.getProgressById(q.id)
            val isCorrect = pickedOrigIdx != null && pickedOrigIdx == q.answer
            QuestionProgressEntity(
                questionId = q.id,
                chosenOption = pickedOrigIdx ?: 0,
                status = if (isCorrect) "correct" else "wrong",
                saved = existing?.saved ?: false,
                updatedAt = System.currentTimeMillis()
            )
        }
        dao.upsertAllProgress(entities)
    }

    suspend fun updatePref(
        darkTheme: Boolean? = null,
        lastQuestionId: Int? = null,
        filterPart: String? = null,
        filterState: String? = null
    ) {
        val current = dao.getAppPrefOnce() ?: AppPrefEntity()
        dao.upsertAppPref(
            current.copy(
                darkTheme = darkTheme ?: current.darkTheme,
                lastQuestionId = lastQuestionId ?: current.lastQuestionId,
                filterPart = filterPart ?: current.filterPart,
                filterState = filterState ?: current.filterState
            )
        )
    }
}
