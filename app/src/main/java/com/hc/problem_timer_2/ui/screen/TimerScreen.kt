package com.hc.problem_timer_2.ui.screen

import android.content.Context
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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hc.problem_timer_2.R
import com.hc.problem_timer_2.ui.view.TextWithoutPadding
import com.hc.problem_timer_2.util.added
import com.hc.problem_timer_2.util.applesdgothicneo
import com.hc.problem_timer_2.util.getNow
import com.hc.problem_timer_2.util.notosanskr
import com.hc.problem_timer_2.ui.viewmodel.BookViewModel
import com.hc.problem_timer_2.ui.viewmodel.ProblemViewModel
import com.hc.problem_timer_2.ui.viewmodel.ProblemRecordViewModel
import com.hc.problem_timer_2.ui.viewmodel.SelectedBookInfoViewModel
import com.hc.problem_timer_2.data.vo.Book
import com.hc.problem_timer_2.data.vo.Grade
import com.hc.problem_timer_2.data.vo.Problem
import com.hc.problem_timer_2.data.vo.ProblemRecord
import com.hc.problem_timer_2.data.vo.onBook
import com.hc.problem_timer_2.data.vo.onPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun TimerScreen(
    showAddBookScreen: () -> Unit,
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel()
) {
    val selectedBookInfo by selectedBookInfoViewModel.selectedBookInfo.observeAsState()

    Column(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
    ) {
        BookTab(showAddBookScreen)
        Divider(thickness = 1.dp, color = Color.LightGray)
        AnimatedVisibility(
            visible = selectedBookInfo!!.isBookSelected(),
            enter = slideInHorizontally()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                PageTab()
                Divider(thickness = 1.dp, color = Color.LightGray)
            }
        }
        ProblemTab()
    }
}

@Composable
fun BookTab(
    showAddBookScreen: () -> Unit,
    bookViewModel: BookViewModel = viewModel(),
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel()
) {
    val books by bookViewModel.bookList.observeAsState()
    val selectedBookInfo by selectedBookInfoViewModel.selectedBookInfo.observeAsState()
    var isDeleteBookBtnVisible by remember { mutableStateOf(false) }

    BookTabStateless(
        books!!,
        { book: Book -> selectedBookInfo!!.selectedBook == book },
        { book: Book -> selectedBookInfoViewModel.select(book) },
        showAddBookScreen,
        { isDeleteBookBtnVisible },
        { value: Boolean -> isDeleteBookBtnVisible = value}
    )
}

@Composable
fun BookTabStateless(
    books: List<Book>,
    isSelected: (Book) -> Boolean,
    selectBook: (Book) -> Unit,
    showAddBookScreen: () -> Unit,
    isDeleteBookBtnVisible: () -> Boolean,
    setVisibilityOfDeleteButton: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .height(50.dp)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextWithoutPadding(
            modifier = Modifier
                .width(60.dp)
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            text = "교재",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = notosanskr
        )
        Divider(
            modifier = Modifier
                .padding(start = 5.dp, end = 15.dp, top = 5.dp, bottom = 5.dp)
                .width(1.dp)
                .height(20.dp),
            color = colorResource(id = R.color.black_300)
        )
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(books) { book ->
                BookButton(
                    book,
                    isSelected(book),
                    { selectBook(book) },
                    isDeleteBookBtnVisible,
                    { setVisibilityOfDeleteButton(!isDeleteBookBtnVisible()) }
                )
            }
        }
        Divider(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                .width(1.dp)
                .height(20.dp),
            color = colorResource(id = R.color.black_300)
        )
        AddBookButton(showAddBookScreen)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookButton(
    book: Book,
    isSelected: Boolean,
    selectBook: () -> Unit,
    isDeleteBookBtnVisible: () -> Boolean,
    toggleVisibilityOfDeleteButton: () -> Unit,
    bookViewModel: BookViewModel = viewModel(),
    problemViewModel: ProblemViewModel = viewModel(),
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel()
) {
    val paddingTopForDeleteBookBtn = 3.dp
    val paddingEndForDeleteBookBtn = 3.dp

    Box(
        modifier = Modifier.wrapContentSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        Box(
            modifier = Modifier
                .padding(
                    top = paddingTopForDeleteBookBtn,
                    bottom = paddingTopForDeleteBookBtn,
                    end = paddingEndForDeleteBookBtn
                )
                .height(30.dp)
                .background(
                    color = if (isSelected) colorResource(R.color.black_200) else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
        ) {
            TextWithoutPadding(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            selectBook()
                            if (isDeleteBookBtnVisible()) {
                                toggleVisibilityOfDeleteButton()
                            }
                        },
                        onLongClick = toggleVisibilityOfDeleteButton
                    ),
                text = book.name,
                fontFamily = notosanskr,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = colorResource(id = R.color.black_500)
            )
        }
        if (isDeleteBookBtnVisible()) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(color = Color.Black, shape = CircleShape)
                    .clickable {
                        selectedBookInfoViewModel.unselect()
                        bookViewModel.deleteBook(book.id)
                        problemViewModel.deleteProblemsOnBook(book.id)
                        toggleVisibilityOfDeleteButton()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(8.dp),
                    imageVector = Icons.Default.Clear,
                    contentDescription = "delete book",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun AddBookButton(showAddBookScreen: () -> Unit) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(30.dp)
            .background(color = colorResource(R.color.black_200), shape = RoundedCornerShape(10.dp))
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            .clickable {
                showAddBookScreen()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextWithoutPadding(
            text = "추가",
            fontSize = 12.sp,
            fontFamily = notosanskr,
            fontWeight = FontWeight.Normal,
            color = colorResource(id = R.color.black_700)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            modifier = Modifier.size(12.dp),
            tint = Color.Black,
            imageVector = Icons.Default.Add,
            contentDescription = "add book",
        )
    }
}


@Composable
fun PageTab(
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel(),
    problemViewModel: ProblemViewModel = viewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    listState: LazyListState = rememberLazyListState()
) {
    val bookInfo by selectedBookInfoViewModel.selectedBookInfo.observeAsState()
    val problems by problemViewModel.problems.observeAsState()
    if (!bookInfo!!.isBookSelected()) return
    val pages = problems!!
        .filter { it.bookId == bookInfo!!.selectedBook!!.id }
        .map { it.page }
        .distinct()
        .sorted()
    if (pages.isEmpty()) {
        problemViewModel.addDefaultProblems(bookInfo!!.selectedBook!!.id)
    } else {
        if (!bookInfo!!.isPageSelected()) {
            selectedBookInfoViewModel.select(pages.first())
        }
    }
    val selectedPage = bookInfo!!.selectedPage

    // 사용자가 페이지를 추가하면 pages가 변경된다
    LaunchedEffect(key1 = selectedPage, key2 = pages) {
        if (!pages.contains(selectedPage)) return@LaunchedEffect
        val index = pages.indexOf(selectedPage)
        scope.launch { listState.animateScrollToItem(index) }
    }

    Row(
        modifier = Modifier
            .wrapContentHeight()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextWithoutPadding(
            modifier = Modifier
                .width(60.dp)
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            text = "페이지",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium, fontFamily = notosanskr
        )
        Divider(
            modifier = Modifier
                .padding(start = 5.dp, end = 15.dp, top = 5.dp, bottom = 5.dp)
                .width(1.dp)
                .height(20.dp),
            color = colorResource(id = R.color.black_300)
        )
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            state = listState
        ) {
            items(pages) { page ->
                PageButton(
                    selectedPage == page,
                    { selectedBookInfoViewModel.select(page) },
                    page
                )
            }
        }
        Divider(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                .width(1.dp)
                .height(20.dp),
            color = colorResource(id = R.color.black_300)
        )
        AddPageButton {
            val bookId = bookInfo!!.selectedBook!!.id
            problemViewModel.addDefaultProblems(bookId)
        }
    }
}

@Composable
fun PageButton(
    isSelected: Boolean,
    selectPage: () -> Unit,
    page: Int
) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .height(30.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isSelected) colorResource(R.color.black_200) else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
        ) {
            TextWithoutPadding(
                modifier = Modifier.clickable { selectPage() },
                text = "$page",
                fontFamily = notosanskr,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = colorResource(id = R.color.black_500)
            )
        }
    }
}

@Composable
fun AddPageButton(addPages: () -> Unit) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(30.dp)
            .background(color = colorResource(R.color.black_200), shape = RoundedCornerShape(10.dp))
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            .clickable { addPages() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextWithoutPadding(
            text = "추가",
            fontSize = 12.sp,
            fontFamily = notosanskr,
            fontWeight = FontWeight.Normal,
            color = colorResource(id = R.color.black_700)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            modifier = Modifier.size(12.dp),
            tint = Color.Black,
            imageVector = Icons.Default.Add,
            contentDescription = "add pages",
        )
    }
}

@Composable
fun ColumnScope.ProblemTab(
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel(),
    problemViewModel: ProblemViewModel = viewModel(),
    problemRecordViewModel: ProblemRecordViewModel = viewModel()
) {
    val problems by problemViewModel.problems.observeAsState()
    val problemRecordList by problemRecordViewModel.problemRecordListOnSelectedPage.observeAsState()
    val selectedBookInfo by selectedBookInfoViewModel.selectedBookInfo.observeAsState()
    val problemsOnSelectedPage = problems!!
        .onBook(selectedBookInfo!!.selectedBook?.id)
        .onPage(selectedBookInfo!!.selectedPage)
        .sorted()
    val problemRecordsMapOnSelectedPage = problemRecordList!!
        .onBook(selectedBookInfo!!.selectedBook)
        .onPage(selectedBookInfo!!.selectedPage)
        .toProblemRecordListMap()
    var isGradeMode by remember { mutableStateOf(false) }

    ProblemTabStateless(
        selectedBookInfo!!.selectedPage,
        selectedBookInfo!!.isBookSelected(),
        selectedBookInfo!!.isPageSelected(),
        problemsOnSelectedPage,
        problemRecordsMapOnSelectedPage,
        { isGradeMode },
        { isGradeMode = !isGradeMode }
    )
}

@Composable
fun ColumnScope.ProblemTabStateless(
    selectedPage: Int?,
    isBookSelected: Boolean,
    isPageSelected: Boolean,
    problemsOnSelectedPage: List<Problem>,
    problemRecordsMapOnSelectedPage: Map<String, List<ProblemRecord>>,
    isGradeMode: () -> Boolean,
    toggleGradeMode: () -> Unit,
    context: Context = LocalContext.current
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(vertical = 15.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!isBookSelected || !isPageSelected) {
            Text(
                text = if (!isBookSelected) context.getString(R.string.select_book) else context.getString(
                    R.string.select_page),
                fontSize = 12.sp
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                ProblemHeaderTab(isGradeMode, toggleGradeMode)
                ProblemBodyTab(
                    selectedPage,
                    problemsOnSelectedPage,
                    problemRecordsMapOnSelectedPage,
                    isGradeMode
                )
                if (isGradeMode()) {
                    GradeButtonTab(toggleGradeMode)
                }
            }
        }
    }
}

@Composable
fun ColumnScope.ProblemHeaderTab(isGradeMode: () -> Boolean, toggleGradeMode: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        this@ProblemHeaderTab.AnimatedVisibility(
            visible = !isGradeMode(),
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut()
        ) {
            ProblemHeaderInNormalModeTab(toggleGradeMode)
        }
        this@ProblemHeaderTab.AnimatedVisibility(
            visible = isGradeMode(),
            enter = slideInHorizontally(),
            exit = slideOutHorizontally(animationSpec = tween(500))
        ) {
            ProblemHeaderInGradeModeTab()
        }
    }
}

@Composable
fun ProblemHeaderInNormalModeTab(toggleGradeMode: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextWithoutPadding(
            modifier = Modifier.wrapContentSize(),
            textAlign = TextAlign.Center,
            text = "문제 풀기",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = notosanskr
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .wrapContentSize()
                .clickable { toggleGradeMode() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextWithoutPadding(
                modifier = Modifier.wrapContentSize(),
                textAlign = TextAlign.Center,
                text = "채점하기",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = notosanskr
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "grade",
            )
        }
    }
}

@Composable
fun ProblemHeaderInGradeModeTab() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterStart
    ) {
        TextWithoutPadding(
            modifier = Modifier.wrapContentSize(),
            textAlign = TextAlign.Center,
            text = "채점하기",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = notosanskr
        )
    }
}

@Composable
fun ColumnScope.ProblemBodyTab(
    selectedPage: Int?,
    problemsOnSelectedPage: List<Problem>,
    problemRecordsMapOnSelectedPage: Map<String, List<ProblemRecord>>,
    isGradeMode: () -> Boolean,
    problemRecordViewModel: ProblemRecordViewModel = viewModel()
) {
    LazyColumn(
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items = problemsOnSelectedPage) { problem ->
            val problemRecords =
                problemRecordsMapOnSelectedPage[problem.number] ?: emptyList()
            val recentProblemRecord = problemRecords.firstOrNull()
            var currentTimeRecord by remember { mutableIntStateOf( recentProblemRecord?.timeRecord ?: 0) }
            var currentGrade by remember { mutableStateOf( recentProblemRecord?.grade ?: Grade.Unranked) }
            var isBeforeRecording by remember { mutableStateOf(problemRecords.ifEmpty { null }?.first()?.isGraded() ?: true) }
            var isProblemRecordsVisible by remember { mutableStateOf(false) }

            LaunchedEffect(key1 = recentProblemRecord) {
                currentTimeRecord = recentProblemRecord?.timeRecord ?: 0
                currentGrade = recentProblemRecord?.grade ?: Grade.Unranked
            }

            LaunchedEffect(key1 = selectedPage) {
                isBeforeRecording = problemRecords.ifEmpty { null }?.first()?.isGraded() ?: true
                isProblemRecordsVisible = false
            }

            ProblemAndProblemRecordTabStateless(
                selectedPage,
                problem,
                problemRecords,
                isGradeMode,
                { currentTimeRecord },
                { value: Int -> currentTimeRecord += value },
                { isProblemRecordsVisible },
                { value: Boolean -> isProblemRecordsVisible = value },
                { currentGrade },
                { currentGrade = currentGrade.next() },
                {
                    if (!isGradeMode() && isBeforeRecording) {
                        problemRecordViewModel.addProblemRecord(problem)
                    } else {
                        problemRecordViewModel.updateProblemRecord(recentProblemRecord!!, currentTimeRecord, currentGrade)
                    }
                },
                { isBeforeRecording },
                { isBeforeRecording = false },
                { isBeforeRecording = true }
            )
        }
    }
}

@Composable
fun ProblemAndProblemRecordTabStateless(
    selectedPage: Int?,
    problem: Problem,
    problemRecords: List<ProblemRecord>,
    isGradeMode: () -> Boolean,
    getCurrentTimeRecord: () -> Int,
    increaseCurrentTimeRecord: (Int) -> Unit,
    isProblemRecordsVisible: () -> Boolean,
    setShowingProblemRecords: (Boolean) -> Unit,
    getCurrentGrade: () -> Grade,
    setNextGrade: () -> Unit,
    addOrUpdateProblemRecord: () -> Unit,
    isBeforeRecording: () -> Boolean,
    startRecording: () -> Unit,
    finishGrading: () -> Unit
) {
    LaunchedEffect(key1 = selectedPage, key2 = isGradeMode()) {
        setShowingProblemRecords(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp,
        )
    ) {
        if (isGradeMode()) {
            ProblemAndProblemRecordInGradeModeTabStateless(
                problem,
                getCurrentGrade,
                isBeforeRecording,
                setNextGrade,
                addOrUpdateProblemRecord,
                finishGrading
            )
        } else {
            ProblemAndProblemRecordInNormalModeTabStateless(
                selectedPage,
                problem,
                getCurrentTimeRecord,
                increaseCurrentTimeRecord,
                getCurrentGrade,
                isProblemRecordsVisible,
                problemRecords,
                { setShowingProblemRecords(!isProblemRecordsVisible()) },
                addOrUpdateProblemRecord,
                startRecording
            )
        }
    }
}

@Composable
fun ProblemAndProblemRecordInGradeModeTabStateless(
    problem: Problem,
    getCurrentGrade: () -> Grade,
    isBeforeRecording: () -> Boolean,
    setNextGrade: () -> Unit,
    addOrUpdateProblemRecord: () -> Unit,
    finishGrading: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(color = Color.White)
            .padding(all = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProblemNumberInGradeModeTab(problem, getCurrentGrade)
        Spacer(Modifier.width(24.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clickable {
                    if (isBeforeRecording()) {
                        // Todo: give some notification
                    } else {
                        setNextGrade()
                        addOrUpdateProblemRecord()
                        finishGrading()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            TextWithoutPadding(
                modifier = Modifier.wrapContentWidth(),
                textAlign = TextAlign.Center,
                text = getCurrentGrade().textLong,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = notosanskr,
                color = getCurrentGrade().color
            )
        }
    }
}

@Composable
fun ProblemNumberInGradeModeTab(
    problem: Problem,
    getCurrentGrade: () -> Grade
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color = getCurrentGrade().color, shape = RoundedCornerShape(10.dp))
            .padding(vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        TextWithoutPadding(
            modifier = Modifier.wrapContentSize(),
            textAlign = TextAlign.Center,
            text = problem.number,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = notosanskr,
            color = Color.White,
            maxLines = 1
        )
    }
}

@Composable
fun ProblemAndProblemRecordInNormalModeTabStateless(
    selectedPage: Int?,
    problem: Problem,
    getCurrentTimeRecord: () -> Int,
    increaseCurrentTimeRecord: (Int) -> Unit,
    getCurrentGrade: () -> Grade,
    isProblemRecordsVisible: () -> Boolean,
    problemRecords: List<ProblemRecord>,
    toggleVisibilityOfProblemRecords: () -> Unit,
    addOrUpdateProblemRecord: () -> Unit,
    startRecording: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = Color.White)
            .padding(all = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        ProblemContentTab(
            selectedPage,
            problem,
            getCurrentTimeRecord,
            increaseCurrentTimeRecord,
            getCurrentGrade,
            addOrUpdateProblemRecord,
            startRecording
        )
        AnimatedVisibility(
            visible = isProblemRecordsVisible(),
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                ProblemRecordTab(problemRecords)
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Divider(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(color = colorResource(id = R.color.black_300))
        )
        Spacer(modifier = Modifier.height(15.dp))
        ProblemRecordViewMoreTab(
            isProblemRecordsVisible,
            toggleVisibilityOfProblemRecords
        )
    }
}

@Composable
fun ProblemContentTab(
    selectedPage: Int?,
    problem: Problem,
    getCurrentTimeRecord: () -> Int,
    increaseCurrentTimeRecord: (Int) -> Unit,
    getCurrentGrade: () -> Grade,
    addOrUpdateProblemRecord: () -> Unit,
    startRecording: () -> Unit
) {
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = selectedPage) {
        isTimerRunning = false
    }

    LaunchedEffect(key1 = isTimerRunning) {
        while (isTimerRunning) {
            delay(10)
            increaseCurrentTimeRecord(10)
        }
    }

    Row(
        modifier = Modifier
            .background(color = Color.White)
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProblemNumberInNormalModeTab(problem, getCurrentGrade)
        Spacer(Modifier.width(24.dp))
        ProblemTimerTab(getCurrentTimeRecord)
        Spacer(Modifier.weight(1f))
        ProblemTimerButton(
            isTimerRunning,
            { isTimerRunning = !isTimerRunning },
            addOrUpdateProblemRecord,
            startRecording
        )
    }
}

@Composable
fun ProblemNumberInNormalModeTab(
    problem: Problem,
    getCurrentGrade: () -> Grade,
    problemViewModel: ProblemViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color = getCurrentGrade().color, shape = RoundedCornerShape(10.dp))
            .clickable { problemViewModel.setProblemToEdit(problem) }
            .padding(vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        TextWithoutPadding(
            modifier = Modifier.wrapContentSize(),
            textAlign = TextAlign.Center,
            text = problem.number,
            fontSize =
                when (problem.number.length) {
                    in 0 .. 3 -> 18.sp
                    in 4 .. 5 -> 14.sp
                    else -> 12.sp
                },
            fontWeight = FontWeight.Bold,
            fontFamily = notosanskr,
            color = Color.White,
            maxLines = 1
        )
    }
}

@Composable
fun ProblemTimerTab(getCurrentTimeRecord: () -> Int) {
    TextWithoutPadding(
        modifier = Modifier.wrapContentSize(),
        textAlign = TextAlign.Center,
        text = toTimeFormat(getCurrentTimeRecord()),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = applesdgothicneo,
        maxLines = 1
    )
}

@Composable
fun ProblemTimerButton(
    isTimerRunning: Boolean,
    toggleTimerRunning: () -> Unit,
    addOrUpdateProblemRecord: () -> Unit,
    startRecording: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = colorResource(id = if (isTimerRunning) R.color.blue_700 else R.color.black_200),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                addOrUpdateProblemRecord()
                toggleTimerRunning()
                startRecording()
            }
            .padding(horizontal = 15.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextWithoutPadding(
            modifier = Modifier.wrapContentSize(),
            textAlign = TextAlign.Center,
            text = if (isTimerRunning) "타이머 정지" else "타이머 시작",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = notosanskr,
            maxLines = 1,
            color = if (isTimerRunning) Color.White else colorResource(id = R.color.black_500)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.clock_24px),
            contentDescription = "record time",
            tint = if (isTimerRunning) Color.White else colorResource(id = R.color.black_500)
        )
    }
}

@Composable
fun ProblemRecordTab(problemRecords: List<ProblemRecord>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = colorResource(id = R.color.black_100),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 30.dp, vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (problemRecords.isEmpty()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.no_problem_record),
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        } else {
            problemRecords.map { problemRecord ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextWithoutPadding(
                        modifier = Modifier
                            .width(40.dp)
                            .wrapContentHeight(),
                        textAlign = TextAlign.Center,
                        text = with (problemRecord.solvedAt) { "$monthNumber/$dayOfMonth" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = notosanskr,
                        color = colorResource(id = R.color.black_400)
                    )
                    TextWithoutPadding(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(),
                        textAlign = TextAlign.Center,
                        text = toTimeFormat(problemRecord.timeRecord),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = notosanskr
                    )
                    TextWithoutPadding(
                        modifier = Modifier
                            .width(60.dp)
                            .wrapContentHeight(),
                        textAlign = TextAlign.Center,
                        text = problemRecord.grade.textShort,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = notosanskr,
                        color = problemRecord.grade.color
                    )
                }
            }
        }
    }
}

@Composable
fun ProblemRecordViewMoreTab(
    isProblemRecordsVisible: () -> Boolean,
    toggleVisibilityOfProblemRecords: () -> Unit
) {
    val viewMoreIconRotationZ by animateFloatAsState(
        targetValue = if (!isProblemRecordsVisible()) 0f else 180f,
        animationSpec = tween(200),
        label = "rotationZ for view more icon"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { toggleVisibilityOfProblemRecords() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextWithoutPadding(
            modifier = Modifier.wrapContentSize(),
            textAlign = TextAlign.Center,
            text = if (isProblemRecordsVisible()) "접기" else "내 기록 보기",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = notosanskr
        )
        Spacer(modifier = Modifier.width(5.dp))
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "show problem records",
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer {
                    rotationZ = viewMoreIconRotationZ
                }
        )
    }
}

@Composable
fun GradeButtonTab(toggleGradeMode: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable { toggleGradeMode() }
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = colorResource(id = R.color.blue_800),
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        TextWithoutPadding(
            text = "채점 완료",
            fontSize = 16.sp,
            fontFamily = notosanskr,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

fun List<ProblemRecord>.toProblemRecordListMap() =
    fold(mutableMapOf<String, List<ProblemRecord>>()) { map, problemRecord ->
        val problemRecordsOfTheNumber = map[problemRecord.number] ?: mutableListOf()
        map[problemRecord.number] =
            problemRecordsOfTheNumber
                .added(problemRecord)
                .sortedByDescending { it.solvedAt }
                .take(3)
                .toMutableList()
        map
    }

fun toTimeFormat(timeRecord: Int): String {
    val ms = (timeRecord / 100) % 10
    val s = (timeRecord / 1_000) % 60
    val m = (timeRecord / 100_000) % 60
    return String.format("%02d:%02d:%01d", m, s, ms)
}

fun shouldWaitToRecordAgain(recentProblemRecord: ProblemRecord?) =
    recentProblemRecord != null && recentProblemRecord.solvedAt.date == getNow().date