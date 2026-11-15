package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppInfoDialog
import com.example.ui.AppTab
import com.example.ui.ExamScreen
import com.example.ui.PartsHomeScreen
import com.example.ui.QuizViewModel
import com.example.ui.RedoWrongScreen
import com.example.ui.ResetProgressConfirmDialog
import com.example.ui.SavedNotesScreen
import com.example.ui.Study300Screen
import com.example.ui.TopPoliticalAppBar
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoseError
import com.example.ui.theme.Slate950

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: QuizViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = uiState.darkTheme) {
                ChinhTri300App(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ChinhTri300App(viewModel: QuizViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle Back navigation on secondary tabs
    BackHandler(enabled = uiState.currentTab != AppTab.PARTS) {
        if (uiState.currentTab == AppTab.EXAM && uiState.exam.isActive) {
            viewModel.quitExamToSetup()
        } else {
            viewModel.selectTab(AppTab.PARTS)
        }
    }

    LaunchedEffect(uiState.toastMessage) {
        val msg = uiState.toastMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopPoliticalAppBar(
                darkTheme = uiState.darkTheme,
                onToggleTheme = viewModel::toggleDarkTheme,
                onOpenInfo = { viewModel.setShowInfoDialog(true) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                AppTab.entries.forEach { tab ->
                    val selected = uiState.currentTab == tab
                    val icon = when (tab) {
                        AppTab.PARTS -> if (selected) Icons.Filled.LibraryBooks else Icons.Outlined.LibraryBooks
                        AppTab.STUDY -> if (selected) Icons.Filled.MenuBook else Icons.Outlined.MenuBook
                        AppTab.SAVED -> if (selected) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
                        AppTab.REDO -> if (selected) Icons.Filled.Autorenew else Icons.Outlined.Autorenew
                        AppTab.EXAM -> if (selected) Icons.Filled.Timer else Icons.Outlined.Timer
                    }

                    val badgeCount = when (tab) {
                        AppTab.SAVED -> uiState.savedCount
                        AppTab.REDO -> uiState.wrongCount
                        else -> 0
                    }

                    NavigationBarItem(
                        selected = selected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (badgeCount > 0) {
                                        Badge(
                                            containerColor = if (tab == AppTab.SAVED) GoldAccent else RoseError,
                                            contentColor = if (tab == AppTab.SAVED) Slate950 else Color.White
                                        ) {
                                            Text(
                                                text = badgeCount.toString(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(imageVector = icon, contentDescription = tab.label)
                            }
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (uiState.darkTheme) GoldAccent else CrimsonRed,
                            selectedTextColor = if (uiState.darkTheme) GoldAccent else CrimsonRed,
                            indicatorColor = if (uiState.darkTheme) {
                                Color(0xFF334155)
                            } else {
                                Color(0xFFFEF2F2)
                            }
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = CrimsonRed,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                when (uiState.currentTab) {
                    AppTab.PARTS -> PartsHomeScreen(
                        uiState = uiState,
                        onResumeClick = viewModel::resumeLastQuestion,
                        onStudyAll300Click = viewModel::openAll300Study,
                        onOpenModuleOrPack = viewModel::openModuleOrPack,
                        onRequestReset = { viewModel.setShowResetConfirmDialog(true) },
                        onNavigateTab = viewModel::selectTab
                    )

                    AppTab.STUDY -> Study300Screen(
                        uiState = uiState,
                        onSelectPartFilter = viewModel::setStudyFilterPart,
                        onSelectStateFilter = viewModel::setStudyFilterState,
                        onSearchQueryChange = viewModel::setSearchQuery,
                        onOpenPalette = { viewModel.setShowPaletteSheet(true) },
                        onClosePalette = { viewModel.setShowPaletteSheet(false) },
                        onPickPaletteIndex = viewModel::selectStudyQuestionIndex,
                        onSelectOption = viewModel::submitStudyOption,
                        onToggleBookmark = viewModel::toggleBookmark,
                        onRedoSingleQuestion = viewModel::redoCurrentStudyQuestion,
                        onNavigateQuestion = viewModel::navigateStudyQuestion
                    )

                    AppTab.SAVED -> SavedNotesScreen(
                        uiState = uiState,
                        onSelectSavedFilter = viewModel::setSavedFilterMode,
                        onJumpToQuestion = viewModel::jumpToQuestionById
                    )

                    AppTab.REDO -> RedoWrongScreen(
                        uiState = uiState,
                        onAnswerRedoQuestion = viewModel::answerInRedoTab,
                        onClearAllRedo = viewModel::clearAllRedoQuestions
                    )

                    AppTab.EXAM -> ExamScreen(
                        uiState = uiState,
                        onSelectExamCount = viewModel::setExamCountOption,
                        onSelectExamPart = viewModel::setExamPartOption,
                        onStartExam = viewModel::startExam,
                        onPickExamOption = viewModel::pickExamOption,
                        onNavigateExamQuestion = viewModel::navigateExamQuestion,
                        onSubmitExam = viewModel::submitExam,
                        onQuitExam = viewModel::quitExamToSetup,
                        onGoRedoTab = { viewModel.selectTab(AppTab.REDO) }
                    )
                }
            }
        }
    }

    if (uiState.showInfoDialog) {
        AppInfoDialog(onDismiss = { viewModel.setShowInfoDialog(false) })
    }

    if (uiState.showResetConfirmDialog) {
        ResetProgressConfirmDialog(
            onConfirm = viewModel::confirmResetAllProgress,
            onDismiss = { viewModel.setShowResetConfirmDialog(false) }
        )
    }
}
