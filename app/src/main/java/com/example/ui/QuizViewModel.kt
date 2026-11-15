package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ExamQuestionItem
import com.example.data.PoliticalQuestion
import com.example.data.QuestionProgressEntity
import com.example.data.QuizRepository
import com.example.data.ShuffledOption
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab(val route: String, val label: String) {
    PARTS("parts", "Từng Phần"),
    STUDY("study", "Học 300"),
    SAVED("saved", "Ghi Nhớ"),
    REDO("redo", "Làm Lại"),
    EXAM("exam", "Thi Thử")
}

data class ExamUiState(
    val isActive: Boolean = false,
    val isFinished: Boolean = false,
    val countOption: Int = 30,
    val partOption: String = "all",
    val items: List<ExamQuestionItem> = emptyList(),
    val currentIndex: Int = 0,
    val remainingSeconds: Int = 1800,
    val correctCount: Int = 0,
    val score10: Float = 0f
)

data class QuizUiState(
    val isLoading: Boolean = true,
    val currentTab: AppTab = AppTab.PARTS,
    val darkTheme: Boolean = false,
    val questions: List<PoliticalQuestion> = emptyList(),
    val progressMap: Map<Int, QuestionProgressEntity> = emptyMap(),
    val lastQuestionId: Int = 1,
    val filterPart: String = "all",
    val filterState: String = "all",
    val searchQuery: String = "",
    val studyIndex: Int = 0,
    val savedFilterMode: String = "starred", // "starred" | "all_keys"
    val showPaletteSheet: Boolean = false,
    val showInfoDialog: Boolean = false,
    val showResetConfirmDialog: Boolean = false,
    val toastMessage: String? = null,
    val redoInlineFeedback: Map<Int, String> = emptyMap(),
    val exam: ExamUiState = ExamUiState()
) {
    val answeredCount: Int
        get() = progressMap.values.count { it.status == "correct" || it.status == "wrong" }

    val correctCount: Int
        get() = progressMap.values.count { it.status == "correct" }

    val wrongCount: Int
        get() = progressMap.values.count { it.status == "wrong" }

    val savedCount: Int
        get() = progressMap.values.count { it.saved }

    val filteredStudyQuestions: List<PoliticalQuestion>
        get() {
            val qStr = searchQuery.trim().lowercase()
            return questions.filter { q ->
                if (filterPart.startsWith("mod_")) {
                    val m = filterPart.removePrefix("mod_").toIntOrNull()
                    if (q.module != m) return@filter false
                } else if (filterPart.startsWith("pack_")) {
                    val p = filterPart.removePrefix("pack_").toIntOrNull()
                    if (q.pack30 != p) return@filter false
                }

                val st = progressMap[q.id]
                when (filterState) {
                    "unanswered" -> if (st?.status != null) return@filter false
                    "answered" -> if (st?.status == null) return@filter false
                    "wrong" -> if (st?.status != "wrong") return@filter false
                    "saved" -> if (st?.saved != true) return@filter false
                }

                if (qStr.isNotEmpty()) {
                    val matchId = q.id.toString() == qStr
                    val matchText = q.question.lowercase().contains(qStr) ||
                        q.options.any { it.lowercase().contains(qStr) } ||
                        q.keyword.lowercase().contains(qStr)
                    if (!matchId && !matchText) return@filter false
                }
                true
            }
        }

    val currentStudyQuestion: PoliticalQuestion?
        get() {
            val list = filteredStudyQuestions
            if (list.isEmpty()) return null
            val safeIdx = studyIndex.coerceIn(0, list.lastIndex)
            return list[safeIdx]
        }
}

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuizRepository
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var examTimerJob: Job? = null

    init {
        val db = AppDatabase.getInstance(application)
        repository = QuizRepository(application, db.quizDao())

        viewModelScope.launch {
            val loadedQuestions = repository.loadQuestions()
            _uiState.update { it.copy(questions = loadedQuestions, isLoading = false) }
        }

        viewModelScope.launch {
            combine(
                repository.allProgressFlow,
                repository.appPrefFlow
            ) { progressList, pref ->
                Pair(progressList.associateBy { it.questionId }, pref)
            }.collect { (progressMap, pref) ->
                _uiState.update { state ->
                    state.copy(
                        progressMap = progressMap,
                        darkTheme = pref?.darkTheme ?: state.darkTheme,
                        lastQuestionId = pref?.lastQuestionId ?: state.lastQuestionId
                    )
                }
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun toggleDarkTheme() {
        val nextDark = !_uiState.value.darkTheme
        _uiState.update { it.copy(darkTheme = nextDark) }
        viewModelScope.launch {
            repository.updatePref(darkTheme = nextDark)
        }
    }

    fun setShowPaletteSheet(show: Boolean) {
        _uiState.update { it.copy(showPaletteSheet = show) }
    }

    fun setShowInfoDialog(show: Boolean) {
        _uiState.update { it.copy(showInfoDialog = show) }
    }

    fun setShowResetConfirmDialog(show: Boolean) {
        _uiState.update { it.copy(showResetConfirmDialog = show) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    private fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun resumeLastQuestion() {
        val state = _uiState.value
        val targetId = state.lastQuestionId
        val idx = state.questions.indexOfFirst { it.id == targetId }.coerceAtLeast(0)
        _uiState.update {
            it.copy(
                filterPart = "all",
                filterState = "all",
                searchQuery = "",
                studyIndex = idx,
                currentTab = AppTab.STUDY
            )
        }
        viewModelScope.launch {
            repository.updatePref(filterPart = "all", filterState = "all")
        }
    }

    fun openAll300Study() {
        _uiState.update {
            it.copy(
                filterPart = "all",
                filterState = "all",
                searchQuery = "",
                studyIndex = 0,
                currentTab = AppTab.STUDY
            )
        }
        viewModelScope.launch {
            repository.updatePref(filterPart = "all", filterState = "all")
        }
    }

    fun openModuleOrPack(filterPartKey: String) {
        _uiState.update {
            it.copy(
                filterPart = filterPartKey,
                filterState = "all",
                searchQuery = "",
                studyIndex = 0,
                currentTab = AppTab.STUDY
            )
        }
        val firstQ = _uiState.value.currentStudyQuestion
        viewModelScope.launch {
            repository.updatePref(
                filterPart = filterPartKey,
                filterState = "all",
                lastQuestionId = firstQ?.id
            )
        }
    }

    fun jumpToQuestionById(questionId: Int) {
        val idx = _uiState.value.questions.indexOfFirst { it.id == questionId }.coerceAtLeast(0)
        _uiState.update {
            it.copy(
                filterPart = "all",
                filterState = "all",
                searchQuery = "",
                studyIndex = idx,
                currentTab = AppTab.STUDY
            )
        }
        viewModelScope.launch {
            repository.updatePref(
                filterPart = "all",
                filterState = "all",
                lastQuestionId = questionId
            )
        }
    }

    fun setStudyFilterPart(part: String) {
        _uiState.update { it.copy(filterPart = part, studyIndex = 0) }
        val q = _uiState.value.currentStudyQuestion
        viewModelScope.launch {
            repository.updatePref(filterPart = part, lastQuestionId = q?.id)
        }
    }

    fun setStudyFilterState(stateFilter: String) {
        _uiState.update { it.copy(filterState = stateFilter, studyIndex = 0) }
        val q = _uiState.value.currentStudyQuestion
        viewModelScope.launch {
            repository.updatePref(filterState = stateFilter, lastQuestionId = q?.id)
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, studyIndex = 0) }
    }

    fun navigateStudyQuestion(delta: Int) {
        val list = _uiState.value.filteredStudyQuestions
        if (list.isEmpty()) return
        val nextIdx = (_uiState.value.studyIndex + delta + list.size) % list.size
        val nextQ = list[nextIdx]
        _uiState.update { it.copy(studyIndex = nextIdx, lastQuestionId = nextQ.id) }
        viewModelScope.launch {
            repository.updatePref(lastQuestionId = nextQ.id)
        }
    }

    fun selectStudyQuestionIndex(index: Int) {
        val list = _uiState.value.filteredStudyQuestions
        if (index !in list.indices) return
        val q = list[index]
        _uiState.update {
            it.copy(
                studyIndex = index,
                lastQuestionId = q.id,
                showPaletteSheet = false
            )
        }
        viewModelScope.launch {
            repository.updatePref(lastQuestionId = q.id)
        }
    }

    fun submitStudyOption(question: PoliticalQuestion, optionIndex: Int) {
        val isCorrect = optionIndex == question.answer
        viewModelScope.launch {
            repository.answerQuestion(
                questionId = question.id,
                chosenOption = optionIndex,
                isCorrect = isCorrect
            )
            repository.updatePref(lastQuestionId = question.id)
        }
    }

    fun redoCurrentStudyQuestion(questionId: Int) {
        viewModelScope.launch {
            repository.resetSingleQuestionAnswer(questionId)
        }
    }

    fun toggleBookmark(questionId: Int) {
        viewModelScope.launch {
            val savedNow = repository.toggleBookmark(questionId)
            showToast(
                if (savedNow) "⭐ Đã lưu Câu #$questionId vào Ghi nhớ"
                else "Đã bỏ lưu ghi nhớ Câu #$questionId"
            )
        }
    }

    fun setSavedFilterMode(mode: String) {
        _uiState.update { it.copy(savedFilterMode = mode) }
    }

    fun confirmResetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            _uiState.update {
                it.copy(
                    showResetConfirmDialog = false,
                    redoInlineFeedback = emptyMap(),
                    lastQuestionId = 1,
                    studyIndex = 0
                )
            }
            showToast("Đã xóa kết quả cũ để làm lại từ đầu.")
        }
    }

    fun answerInRedoTab(question: PoliticalQuestion, optionIndex: Int) {
        val letters = listOf("A", "B", "C", "D")
        if (optionIndex == question.answer) {
            viewModelScope.launch {
                repository.answerQuestion(question.id, optionIndex, true)
                _uiState.update { state ->
                    state.copy(redoInlineFeedback = state.redoInlineFeedback - question.id)
                }
                showToast("✓ Đã làm đúng Câu #${question.id}!")
            }
        } else {
            val correctLetter = letters.getOrElse(question.answer) { "A" }
            val msg = "✗ Vẫn chưa đúng! Đáp án đúng là $correctLetter (Từ khóa: \"${question.keyword}\")."
            _uiState.update { state ->
                state.copy(redoInlineFeedback = state.redoInlineFeedback + (question.id to msg))
            }
        }
    }

    fun clearAllRedoQuestions() {
        viewModelScope.launch {
            repository.clearRedoList()
            _uiState.update { it.copy(redoInlineFeedback = emptyMap()) }
            showToast("Đã xóa danh sách câu làm lại.")
        }
    }

    // ==================== EXAM LOGIC ====================
    fun setExamCountOption(count: Int) {
        _uiState.update { it.copy(exam = it.exam.copy(countOption = count)) }
    }

    fun setExamPartOption(part: String) {
        _uiState.update { it.copy(exam = it.exam.copy(partOption = part)) }
    }

    fun startExam() {
        val state = _uiState.value
        val count = state.exam.countOption
        val part = state.exam.partOption
        val pool = if (part == "all") {
            state.questions
        } else {
            val m = part.toIntOrNull() ?: 1
            state.questions.filter { it.module == m }
        }
        if (pool.isEmpty()) return

        val chosenQuestions = pool.shuffled().take(count.coerceAtMost(pool.size))
        val items = chosenQuestions.map { q ->
            val shuffledOpts = q.options.mapIndexed { idx, txt ->
                ShuffledOption(text = txt, originalIndex = idx)
            }.shuffled()
            ExamQuestionItem(
                question = q,
                options = shuffledOpts,
                pickedShuffledIndex = null
            )
        }

        val durationMinutes = if (count == 50) 45 else count
        val totalSeconds = durationMinutes * 60

        examTimerJob?.cancel()
        _uiState.update {
            it.copy(
                exam = it.exam.copy(
                    isActive = true,
                    isFinished = false,
                    items = items,
                    currentIndex = 0,
                    remainingSeconds = totalSeconds,
                    correctCount = 0,
                    score10 = 0f
                )
            )
        }

        examTimerJob = viewModelScope.launch {
            while (_uiState.value.exam.isActive && _uiState.value.exam.remainingSeconds > 0) {
                delay(1000L)
                val nextSec = _uiState.value.exam.remainingSeconds - 1
                if (nextSec <= 0) {
                    _uiState.update { st -> st.copy(exam = st.exam.copy(remainingSeconds = 0)) }
                    submitExam()
                    break
                } else {
                    _uiState.update { st -> st.copy(exam = st.exam.copy(remainingSeconds = nextSec)) }
                }
            }
        }
    }

    fun pickExamOption(shuffledIndex: Int) {
        _uiState.update { st ->
            val ex = st.exam
            if (!ex.isActive || ex.currentIndex !in ex.items.indices) return@update st
            val updatedItems = ex.items.toMutableList()
            val currentItem = updatedItems[ex.currentIndex]
            updatedItems[ex.currentIndex] = currentItem.copy(pickedShuffledIndex = shuffledIndex)
            st.copy(exam = ex.copy(items = updatedItems))
        }
    }

    fun navigateExamQuestion(delta: Int) {
        _uiState.update { st ->
            val ex = st.exam
            if (ex.items.isEmpty()) return@update st
            val nextIdx = (ex.currentIndex + delta + ex.items.size) % ex.items.size
            st.copy(exam = ex.copy(currentIndex = nextIdx))
        }
    }

    fun submitExam() {
        examTimerJob?.cancel()
        val ex = _uiState.value.exam
        if (ex.items.isEmpty()) return

        var correct = 0
        val outcomes = ex.items.map { item ->
            val pickedOpt = item.pickedShuffledIndex?.let { item.options.getOrNull(it) }
            val isOk = pickedOpt != null && pickedOpt.originalIndex == item.question.answer
            if (isOk) correct++
            Pair(item.question, pickedOpt?.originalIndex)
        }

        val score = (correct.toFloat() / ex.items.size.toFloat()) * 10f
        _uiState.update { st ->
            st.copy(
                exam = ex.copy(
                    isActive = false,
                    isFinished = true,
                    correctCount = correct,
                    score10 = score
                )
            )
        }

        viewModelScope.launch {
            repository.saveExamOutcomes(outcomes)
        }
    }

    fun quitExamToSetup() {
        examTimerJob?.cancel()
        _uiState.update { st ->
            st.copy(
                exam = st.exam.copy(
                    isActive = false,
                    isFinished = false,
                    items = emptyList()
                )
            )
        }
    }
}
