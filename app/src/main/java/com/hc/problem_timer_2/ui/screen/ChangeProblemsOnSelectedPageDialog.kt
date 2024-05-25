package com.hc.problem_timer_2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hc.problem_timer_2.R
import com.hc.problem_timer_2.ui.view.BasicTextFieldWithHint
import com.hc.problem_timer_2.ui.view.BottomSheetDialog
import com.hc.problem_timer_2.ui.view.TextWithoutPadding
import com.hc.problem_timer_2.ui.viewmodel.ProblemViewModel
import com.hc.problem_timer_2.ui.viewmodel.SelectedBookInfoViewModel
import com.hc.problem_timer_2.util.notosanskr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChangeProblemsOnSelectedPageDialog(
    problemViewModel: ProblemViewModel = viewModel()
) {
    val isChangingProblemsOnSelectedPage by problemViewModel.isChangingProblems.observeAsState()
    var isLastProblemNumberTabVisible by remember { mutableStateOf(false) }
    var isChangeProblemsButtonVisible by remember { mutableStateOf(false) }

    BottomSheetDialog(
        title = "문제 범위 설정",
        content = {
            FirstProblemNumberTab({ isLastProblemNumberTabVisible = true })
            if (isLastProblemNumberTabVisible) {
                LastProblemNumberTab({ isChangeProblemsButtonVisible = true })
            }
            if (isChangeProblemsButtonVisible) {
                Spacer(modifier = Modifier.height(8.dp))
                ChangeProblemsButton()
            }
        },
        isVisible = isChangingProblemsOnSelectedPage!!,
        dismiss = { problemViewModel.unsetBookInfoToChangeProblems() }
    )
}

@Composable
fun FirstProblemNumberTab(
    showLastProblemNumberTab: () -> Unit,
    focusRequester: FocusRequester = remember { FocusRequester() },
    problemViewModel: ProblemViewModel = viewModel()
) {
    val firstProblemNumberInput by problemViewModel.firstProblemNumberInput.observeAsState()
    var isInitialized by remember { mutableStateOf(false) }

    RequestFocusOnInitialize(focusRequester, { isInitialized }, { value: Boolean -> isInitialized = value })

    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextFieldWithHint(
            modifier = Modifier
                .wrapContentSize()
                .focusRequester(focusRequester),
            value = firstProblemNumberInput!!,
            onValueChange = { if (it.length <= 5) problemViewModel.firstProblemNumberInput.value = it },
            hint = "시작 문제 번호",
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = notosanskr,
                color = colorResource(id = R.color.black_900)
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                showLastProblemNumberTab()
            }),
            singleLine = true,
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(8.dp))
        // underline
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = colorResource(id = R.color.black_300))
        )
    }
}

@Composable
fun RequestFocusOnInitialize(
    focusRequester: FocusRequester,
    isInitialized: () -> Boolean,
    setInitialized: (Boolean) -> Unit,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    LaunchedEffect(key1 = isInitialized()) {
        if (!isInitialized()) {
            scope.launch {
                delay(500)
                focusRequester.requestFocus()
            }
            setInitialized(true)
        }
    }
}

@Composable
fun LastProblemNumberTab(
    showChangeProblemsButton: () -> Unit,
    focusRequester: FocusRequester = remember { FocusRequester() },
    focusManager: FocusManager = LocalFocusManager.current,
    problemViewModel: ProblemViewModel = viewModel()
) {
    val lastProblemNumberInput by problemViewModel.lastProblemNumberInput.observeAsState()
    var isInitialized by remember { mutableStateOf(false) }

    RequestFocusOnInitialize(focusRequester, { isInitialized }, { value: Boolean -> isInitialized = value })

    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextFieldWithHint(
            modifier = Modifier
                .wrapContentSize()
                .focusRequester(focusRequester),
            value = lastProblemNumberInput!!,
            onValueChange = { if (it.length <= 5) problemViewModel.lastProblemNumberInput.value = it },
            hint = "마지막 문제 번호",
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = notosanskr,
                color = colorResource(id = R.color.black_900)
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                showChangeProblemsButton()
                focusManager.clearFocus()
            }),
            singleLine = true,
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(8.dp))
        // underline
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = colorResource(id = R.color.black_300))
        )
    }
}

@Composable
fun ChangeProblemsButton(
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel(),
    problemViewModel: ProblemViewModel = viewModel()
) {
    val selectedBookInfo by selectedBookInfoViewModel.selectedBookInfo.observeAsState()
    val isChangingProblemsOnSelectedPage by problemViewModel.isChangingProblems.observeAsState()
    val canChangeProblemsButton by problemViewModel.canChangeProblemsButton.observeAsState()

    Box(
        modifier = Modifier
            .clickable {
                with(problemViewModel) {
                    deleteProblemsOnPage(
                        selectedBookInfo!!.selectedBook!!.id,
                        selectedBookInfo!!.selectedPage!!
                    )
                    .invokeOnCompletion {
                        addProblemsOnSelectedPage()
                    }
                }
            }
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = colorResource(id = if (canChangeProblemsButton!!) R.color.blue_800 else R.color.black_200),
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        TextWithoutPadding(
            text = "설정 완료",
            fontSize = 16.sp,
            fontFamily = notosanskr,
            fontWeight = FontWeight.SemiBold,
            color = if (canChangeProblemsButton!!) Color.White else colorResource(id = R.color.black_500)
        )
    }
}