package com.hc.problem_timer_2

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hc.problem_timer_2.ui.theme.Primary
import com.hc.problem_timer_2.ui.theme.ProblemTimer2Theme
import com.hc.problem_timer_2.util.TimberDebugTree
import com.hc.problem_timer_2.util.added
import com.hc.problem_timer_2.viewmodel.BookListViewModel
import com.hc.problem_timer_2.viewmodel.ProblemRecordListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import androidx.compose.ui.text.font.lerp
import androidx.compose.ui.text.input.ImeAction
import com.hc.problem_timer_2.MainActivity.Companion.POSITIVE_INTEGER_MATCHER
import com.hc.problem_timer_2.ui.screen.AddBookScreen
import com.hc.problem_timer_2.ui.screen.TimerScreen
import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem
import com.hc.problem_timer_2.vo.ProblemRecord
import com.hc.problem_timer_2.util.BaseDialog
import com.hc.problem_timer_2.util.TextWithoutPadding
import com.hc.problem_timer_2.util.applesdgothicneo
import com.hc.problem_timer_2.vo.Grade
import com.hc.problem_timer_2.vo.Grade.*
import com.hc.problem_timer_2.util.customToast
import com.hc.problem_timer_2.util.getNow
import com.hc.problem_timer_2.util.notosanskr
import com.hc.problem_timer_2.viewmodel.SelectedBookInfoViewModel
import com.hc.problem_timer_2.viewmodel.ProblemListViewModel
import com.hc.problem_timer_2.vo.onBook
import com.hc.problem_timer_2.vo.onPage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getDataFromLocalDB()
        setContent {
            ProblemTimer2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TimerApp()
                }
            }
        }
        Timber.plant(TimberDebugTree)
    }

    companion object {
        const val POSITIVE_INTEGER_MATCHER = "^([1-9]\\d*)$"
        const val BOOK_NAME_LENGTH_MAX = 15
    }
}

@Composable
fun TimerApp() {
    var isShowingAddBookScreen by remember { mutableStateOf(false) }


    AnimatedVisibility(
        visible = !isShowingAddBookScreen,
        enter = slideInHorizontally() + fadeIn(),
        exit = slideOutHorizontally() + fadeOut()
    ) {
        TimerScreen({ isShowingAddBookScreen = true })
    }
    AnimatedVisibility(
        visible = isShowingAddBookScreen,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it }
    ) {
        AddBookScreen( { isShowingAddBookScreen = false } )
    }
}

@Composable
fun ProblemNumberBodyTab(
    problem: Problem,
    updateProblem: () -> Unit,
    isProblemEditing: () -> Boolean,
    finishEditingProblem: () -> Unit,
    isGradeMode: () -> Boolean,
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
    focusRequester: FocusRequester = remember { FocusRequester() },
    windowInfo: WindowInfo = LocalWindowInfo.current,
    problemListViewModel: ProblemListViewModel = viewModel()
) {
    var problemNumberInput by remember { mutableStateOf("") }

    LaunchedEffect(key1 = isProblemEditing()) {
        if (isProblemEditing()) {
            snapshotFlow { windowInfo.isWindowFocused }
                .collect { isWindowFocused ->
                    if (isWindowFocused) {
                        focusRequester.requestFocus()
                    }
                }
        }
    }

    Box(modifier = Modifier.clickable { if (!isProblemEditing()) updateProblem() }) {
        BasicTextField(
            modifier = Modifier
                .width(60.dp)
                .focusRequester(focusRequester),
            enabled = !isGradeMode() && isProblemEditing(),
            value = problemNumberInput,
            onValueChange = { problemNumberInput = it },
            textStyle = TextStyle(
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                if (!isPositiveInteger(problemNumberInput)) {
                    customToast(context.getString(R.string.invalid_number_input), context)
                    problemNumberInput = problem.number
                } else if (problemListViewModel.isProblemNumberDuplicated(problem, problemNumberInput)) {
                    customToast(context.getString(R.string.duplicated_number_input), context)
                    problemNumberInput = problem.number
                } else {
                    problemListViewModel.updateProblemNumber(problem, problemNumberInput)
                }
                focusManager.clearFocus()
                finishEditingProblem()
                problemNumberInput = ""
            }),
            singleLine = true,
            maxLines = 1,
            decorationBox = { innerTextField ->
                if (!isProblemEditing()) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = problem.number
                    )
                }
                innerTextField()
            }
        )
    }
}


@Composable
fun UpdateProblemDialog(problem: Problem, hideDialog: () -> Unit, editProblem: () -> Unit, problemListViewModel: ProblemListViewModel = viewModel()) {
    val problemWithoutId = problem.copy(id = 0L)
    val nextSubProblem = problemWithoutId.copy(subNumber = if (problem.isMainProblem()) "1" else "${problem.subNumber!!.toInt() + 1}")
    val nextMainProblem = problemWithoutId.copy(mainNumber = "${problem.mainNumber.toInt() + 1}").copy(subNumber = null)

    BaseDialog(
        text = {
            Column {
                if (!problemListViewModel.isProblemNumberDuplicated(problem, nextSubProblem.number)) {
                    DialogButton(
                        onClick = {
                            problemListViewModel.addProblem(nextSubProblem)
                            hideDialog()
                        },
                        text = "${nextSubProblem.number}번 문제 추가하기"
                    )
                }
                if (!problemListViewModel.isProblemNumberDuplicated(problem, nextMainProblem.number)) {
                    DialogButton(
                        onClick = {
                            problemListViewModel.addProblem(nextMainProblem)
                            hideDialog()
                        },
                        text = "${nextMainProblem.number}번 문제 추가하기"
                    )
                }
                DialogButton(
                    onClick = {
                        editProblem()
                        hideDialog()
                    },
                    text = "${problem.number}번 문제 수정하기"
                )
                DialogButton(
                    onClick = {
                        problemListViewModel.deleteProblem(problem)
                        hideDialog()
                    },
                    text = "${problem.number}번 문제 삭제하기"
                )
            }
        },
        hideDialog = hideDialog
    )
}

@Composable
fun DialogButton(onClick: () -> Unit, text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                color = Color.Black,
                shape = CircleShape
            )
            .padding(horizontal = 15.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White
        )
    }
}

fun ComponentActivity.getDataFromLocalDB() {
    val bookListViewModel: BookListViewModel by viewModels()
    bookListViewModel.getBookList()

    val problemListViewModel: ProblemListViewModel by viewModels()
    problemListViewModel.getProblems()

    val problemRecordListViewModel: ProblemRecordListViewModel by viewModels()
    problemRecordListViewModel.getProblemRecords()
}

fun isPositiveInteger(s: String) = s.matches(POSITIVE_INTEGER_MATCHER.toRegex())