package com.hc.problem_timer_2

import android.content.Context
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hc.problem_timer_2.ui.theme.ProblemTimer2Theme
import com.hc.problem_timer_2.util.TimberDebugTree
import com.hc.problem_timer_2.ui.viewmodel.BookViewModel
import com.hc.problem_timer_2.ui.viewmodel.ProblemRecordViewModel
import timber.log.Timber
import androidx.compose.ui.text.input.ImeAction
import com.hc.problem_timer_2.MainActivity.Companion.POSITIVE_INTEGER_MATCHER
import com.hc.problem_timer_2.ui.screen.AddBookScreen
import com.hc.problem_timer_2.ui.screen.TimerScreen
import com.hc.problem_timer_2.data.vo.Problem
import com.hc.problem_timer_2.ui.screen.EditProblemDialog
import com.hc.problem_timer_2.ui.view.BaseDialog
import com.hc.problem_timer_2.ui.view.customToast
import com.hc.problem_timer_2.ui.viewmodel.ProblemViewModel
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
fun TimerApp(
    problemViewModel: ProblemViewModel = viewModel()
) {
    var isAddBookScreenVisible by remember { mutableStateOf(false) }
    val isEditingProblem by problemViewModel.isEditingProblem.observeAsState()

    AnimatedVisibility(
        visible = !isAddBookScreenVisible,
        enter = slideInHorizontally() + fadeIn(),
        exit = slideOutHorizontally() + fadeOut()
    ) {
        TimerScreen({ isAddBookScreenVisible = true })
    }
    EditProblemDialog(

    )
    AnimatedVisibility(
        visible = isAddBookScreenVisible,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it }
    ) {
        AddBookScreen( { isAddBookScreenVisible = false } )
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
    problemViewModel: ProblemViewModel = viewModel()
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
                } else if (problemViewModel.isProblemNumberDuplicated(problem, problemNumberInput)) {
                    customToast(context.getString(R.string.duplicated_number_input), context)
                    problemNumberInput = problem.number
                } else {
                    problemViewModel.updateProblemNumber(problem, problemNumberInput)
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
fun UpdateProblemDialog(problem: Problem, hideDialog: () -> Unit, editProblem: () -> Unit, problemViewModel: ProblemViewModel = viewModel()) {
    val problemWithoutId = problem.copy(id = 0L)
    val nextSubProblem = problemWithoutId.copy(subNumber = if (problem.isMainProblem()) "1" else "${problem.subNumber!!.toInt() + 1}")
    val nextMainProblem = problemWithoutId.copy(mainNumber = "${problem.mainNumber.toInt() + 1}").copy(subNumber = null)

    BaseDialog(
        text = {
            Column {
                if (!problemViewModel.isProblemNumberDuplicated(problem, nextSubProblem.number)) {
                    DialogButton(
                        onClick = {
                            problemViewModel.addProblem(nextSubProblem)
                            hideDialog()
                        },
                        text = "${nextSubProblem.number}번 문제 추가하기"
                    )
                }
                if (!problemViewModel.isProblemNumberDuplicated(problem, nextMainProblem.number)) {
                    DialogButton(
                        onClick = {
                            problemViewModel.addProblem(nextMainProblem)
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
                        problemViewModel.deleteProblem(problem)
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
    val bookViewModel: BookViewModel by viewModels()
    bookViewModel.getBookList()

    val problemViewModel: ProblemViewModel by viewModels()
    problemViewModel.getProblems()

    val problemRecordViewModel: ProblemRecordViewModel by viewModels()
    problemRecordViewModel.getProblemRecords()
}

fun isPositiveInteger(s: String) = s.matches(POSITIVE_INTEGER_MATCHER.toRegex())