package com.hc.problem_timer_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.hc.problem_timer_2.ui.theme.ProblemTimer2Theme
import com.hc.problem_timer_2.util.TimberDebugTree
import com.hc.problem_timer_2.ui.viewmodel.BookViewModel
import com.hc.problem_timer_2.ui.viewmodel.ProblemRecordViewModel
import timber.log.Timber
import com.hc.problem_timer_2.ui.screen.AddBookScreen
import com.hc.problem_timer_2.ui.screen.TimerScreen
import com.hc.problem_timer_2.ui.screen.ChangeProblemsOnSelectedPageDialog
import com.hc.problem_timer_2.ui.screen.EditProblemDialog
import com.hc.problem_timer_2.ui.viewmodel.ProblemViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        getDataFromLocalDB()
        setContent {
            ProblemTimer2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = Color.White
                ) {
                    TimerApp()
                }
            }
        }
        Timber.plant(TimberDebugTree)
    }

    companion object {
        const val BOOK_NAME_LENGTH_LIMIT = 15
    }
}

@Composable
fun TimerApp() {
    var isAddBookScreenVisible by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = !isAddBookScreenVisible,
        enter = slideInHorizontally() + fadeIn(),
        exit = slideOutHorizontally() + fadeOut()
    ) {
        TimerScreen({ isAddBookScreenVisible = true })
    }
    EditProblemDialog()
    ChangeProblemsOnSelectedPageDialog()
    AnimatedVisibility(
        visible = isAddBookScreenVisible,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it }
    ) {
        AddBookScreen( { isAddBookScreenVisible = false } )
    }
}

fun ComponentActivity.getDataFromLocalDB() {
    val bookViewModel: BookViewModel by viewModels()
    bookViewModel.getBookList()

    val problemViewModel: ProblemViewModel by viewModels()
    problemViewModel.getProblems()

    val problemRecordViewModel: ProblemRecordViewModel by viewModels()
    problemRecordViewModel.getProblemRecords()
}