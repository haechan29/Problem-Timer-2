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
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hc.problem_timer_2.MainActivity.Companion.PAGE_ITEM_SIZE
import com.hc.problem_timer_2.ui.theme.Primary
import com.hc.problem_timer_2.ui.theme.ProblemTimer2Theme
import com.hc.problem_timer_2.ui.theme.SecondPrimary
import com.hc.problem_timer_2.util.FlagController.invokeAndBlock
import com.hc.problem_timer_2.util.Flag.*
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
import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem
import com.hc.problem_timer_2.vo.ProblemRecord
import com.hc.problem_timer_2.ui.theme.BackgroundGrey
import com.hc.problem_timer_2.vo.Grade
import com.hc.problem_timer_2.vo.Grade.*
import com.hc.problem_timer_2.util.customToast
import com.hc.problem_timer_2.util.getNow
import com.hc.problem_timer_2.viewmodel.BookInfoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getDataFromViewModels()
        setContent {
            ProblemTimer2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TimerScreen()
                }
            }
        }
        Timber.plant(TimberDebugTree)
    }

    companion object {
        const val PAGE_ITEM_SIZE = 40
    }
}

@Composable
fun TimerScreen() {
    var isGradeMode by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        var isShowingAddBookDialog by remember { mutableStateOf(false) }
        BookTab { isShowingAddBookDialog = true }
        Divider(thickness = 1.dp, color = Color.LightGray)
        PageAndGradeTab(isGradeMode, { value: Boolean -> isGradeMode = value })
        Divider(thickness = 1.dp, color = Color.LightGray)
        if (isShowingAddBookDialog) {
            AddBookDialog { isShowingAddBookDialog = false }
        }
        ProblemListTab(isGradeMode)
    }
}

@Composable
fun BookTab(
    bookListViewModel: BookListViewModel = viewModel(),
    bookInfoViewModel: BookInfoViewModel = viewModel(),
    isShowingAddBookDialog: () -> Unit
) {
    val books by bookListViewModel.bookList.observeAsState()
    var selectedItemId by remember { mutableStateOf<Long?>(null) }
    var isShowingDeleteBookBtn by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = selectedItemId) {
        if (selectedItemId == null) return@LaunchedEffect
        val book = books!!.find { it.id == selectedItemId }!!
        bookInfoViewModel.setBook(book)
    }

    LazyRow(
        modifier = Modifier
            .padding(all = 10.dp)
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(books!!.size + 1) { itemIndex ->
            if (itemIndex in books!!.indices) {
                val book = books!![itemIndex]
                BookButton(
                    book,
                    selectedItemId == book.id,
                    { id: Long -> selectedItemId = id },
                    isShowingDeleteBookBtn,
                    { isShowingDeleteBookBtn = !isShowingDeleteBookBtn }
                )
            } else {
                IconButton(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(all = 5.dp)
                        .background(color = Primary, shape = CircleShape),
                    onClick = { isShowingAddBookDialog() },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "add book",
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookButton(
    book: Book,
    isSelected: Boolean,
    setSelectedItemIndex: (Long) -> Unit,
    isShowingDeleteBookBtn: Boolean,
    toggleVisibilityOfDeleteButton: () -> Unit,
    bookListViewModel: BookListViewModel = viewModel(),
) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.TopEnd
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight()
                .background(
                    color = if (isSelected) Color.Black else Color.LightGray,
                    shape = CircleShape
                )
                .padding(horizontal = 15.dp)
                .combinedClickable(
                    onClick = { setSelectedItemIndex(book.id) },
                    onLongClick = toggleVisibilityOfDeleteButton
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = book.name,
                color = if (isSelected) Color.White else Color.Black
            )
        }
        if (isShowingDeleteBookBtn) {
            Box(
                modifier = Modifier
                    .background(color = Color.Red, shape = CircleShape)
                    .align(Alignment.TopEnd)
                    .clickable {
                        bookListViewModel.deleteBook(book.id)
                        toggleVisibilityOfDeleteButton()
                    }
            ) {
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(3.dp),
                    imageVector = Icons.Default.Clear,
                    contentDescription = "delete book",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun PageAndGradeTab(isGradeMode: Boolean, setGradeMode: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .padding(start = 5.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PageTab()
        Spacer(modifier = Modifier.weight(1f))
        GradeTab(isGradeMode, setGradeMode)
    }
}

@Composable
fun PageTab(
    bookInfoViewModel: BookInfoViewModel = viewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    listState: LazyListState = rememberLazyListState()
) {
    val bookInfo by bookInfoViewModel.bookInfo.observeAsState()
    val pages = bookInfo?.selectedBook?.getPages()
    val selectedPage = bookInfo?.selectedPage

    LaunchedEffect(key1 = selectedPage) {
        if (selectedPage == null) return@LaunchedEffect
        val index = pages!!.indexOf(selectedPage)
        scope.launch { listState.animateScrollToItem(index) }
    }

    Row(
        modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PageButton(true) {
            if (bookInfo == null) {
                customToast(context.getString(R.string.select_book), context)
                return@PageButton
            }
            val index = pages!!.indexOf(selectedPage)
            if (index < 0) return@PageButton
            setPage((selectedPage!! - 1).toString(), bookInfoViewModel, pages) {
                notifyPageOutOfRange(context, pages)
            }
        }
        PageBox(pages, listState, bookInfo == null )
        PageButton(false) {
            if (bookInfo == null) {
                customToast(context.getString(R.string.select_book), context)
                return@PageButton
            }
            val index = pages!!.indexOf(selectedPage)
            if (index < 0) return@PageButton
            setPage((selectedPage!! + 1).toString(), bookInfoViewModel, pages) {
                notifyPageOutOfRange(context, pages)
            }
        }
    }
}

@Composable
fun PageButton(isBeforeButton: Boolean, onClick: () -> Unit) {
    IconButton(
        modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight(),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(
                if (isBeforeButton) R.drawable.navigate_before_24px
                else R.drawable.navigate_next_24px
            ),
            contentDescription = null
        )
    }
}

@Composable
fun PageBox(
    pages: List<Int>?,
    listState: LazyListState,
    isBookSelected: Boolean,
    bookInfoViewModel: BookInfoViewModel = viewModel(),
    focusManager: FocusManager = LocalFocusManager.current,
    context: Context = LocalContext.current
) {
    var pageInput by remember { mutableStateOf("") }
    var isPageBoxFocused by remember { mutableStateOf(false) }

    LazyRow(
        modifier = Modifier
            .width(PAGE_ITEM_SIZE.dp * 1.5f)
            .fillMaxHeight()
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(5.dp))
            .clickable {
                if (!isBookSelected) customToast(
                    context.getString(R.string.select_book),
                    context
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        state = listState,
        contentPadding = PaddingValues(horizontal = 10.dp),
        userScrollEnabled = false
    ) {
        if (pages == null) return@LazyRow
        items(pages.size) { itemIdx ->
            BasicTextField(
                modifier = Modifier
                    .width(PAGE_ITEM_SIZE.dp)
                    .fillMaxHeight()
                    .wrapContentHeight()
                    .onFocusChanged { isPageBoxFocused = it.isFocused },
                value = pageInput,
                onValueChange = { pageInput = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    setPage(pageInput, bookInfoViewModel, pages) { notifyPageOutOfRange(context, pages) }
                    pageInput = ""
                }),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                singleLine = true,
                maxLines = 1,
                decorationBox = { innerTextField ->
                    if (!isPageBoxFocused) {
                        Text(
                            text = "${pages[itemIdx]}",
                            textAlign = TextAlign.Center
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
fun GradeTab(
    isGradeMode: Boolean,
    setGradeMode: (Boolean) -> Unit,
    context: Context = LocalContext.current,
    bookInfoViewModel: BookInfoViewModel = viewModel()
) {
    val bookInfo by bookInfoViewModel.bookInfo.observeAsState()
    var text by remember { mutableStateOf(context.getString(R.string.view_problems)) }
    val progress by animateFloatAsState(
        targetValue = if (isGradeMode) 1f else 0f,
        label = "",
        finishedListener = { text = if (isGradeMode) context.getString(R.string.finish_grade) else context.getString(R.string.view_problems) }
    )
    val focusedWeight = lerp(FontWeight.ExtraBold, FontWeight.Normal, progress)
    val unfocusedWeight = lerp(FontWeight.ExtraBold, FontWeight.Normal, 1 - progress)

    Row(
        modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            lineHeight = 14.sp,
            fontSize = 12.sp,
            fontWeight = focusedWeight
        )
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight(),
            checked = isGradeMode,
            onCheckedChange = { checked ->
                if (bookInfo != null) setGradeMode(checked)
                else customToast(context.getString(R.string.select_book), context)
            },
            colors = SwitchDefaults.colors(
                checkedTrackColor = Primary,
                uncheckedBorderColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.grade),
            lineHeight = 14.sp,
            fontSize = 12.sp,
            fontWeight = unfocusedWeight
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookDialog(
    bookListViewModel: BookListViewModel = viewModel(),
    hideDialog: () -> Unit
) {
    var bookName by remember { mutableStateOf("") }
    AlertDialog(
        title = { Text(text = "추가할 교재를 입력해주세요", fontSize = 16.sp) },
        text = {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                value = bookName,
                onValueChange = { bookName = it },
                textStyle = TextStyle(fontSize = 14.sp),
                label = {
                    Text(
                        text = "교재 이름",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    bookListViewModel.addBook(Book(name = bookName))
                    hideDialog()
                }),
            )
        },
        containerColor = Color.White,
        onDismissRequest = hideDialog,
        confirmButton = {
            TextButton(
                onClick = {
                    bookListViewModel.addBook(Book(name = bookName))
                    hideDialog()
                }
            ) {
                Text(
                    text = "교재 추가",
                    fontSize = 14.sp,
                    color = Primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = hideDialog) {
                Text(
                    text = "취소",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    )
}

@Composable
fun ColumnScope.ProblemListTab(
    isGradeMode: Boolean,
    bookInfoViewModel: BookInfoViewModel = viewModel(),
    problemRecordListViewModel: ProblemRecordListViewModel = viewModel(),
    context: Context = LocalContext.current
) {
    val bookInfo by bookInfoViewModel.bookInfo.observeAsState()
    val problems = bookInfo?.getProblemsOnSelectedPage()

    if (bookInfo != null) {
        with(bookInfo!!) { problemRecordListViewModel.getProblemRecords(selectedBook.id, selectedPage) }
    }

    val problemRecordList by problemRecordListViewModel.problemRecordListOnSelectedPage.observeAsState()
    val problemRecordListMap = problemRecordList!!.toProblemRecordListMap()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        if (problems.isNullOrEmpty()) {
            Text(
                text = context.getString(R.string.select_book),
                fontSize = 12.sp
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(all = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(items = problems) { problem ->
                    val problemRecords = problemRecordListMap[problem.number]
                    var color by remember { mutableStateOf(BackgroundGrey) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!isGradeMode) BackgroundGrey else color
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(10.dp)
                        ) {
                            var isShowingProblemRecords by remember { mutableStateOf(false) }
                            LaunchedEffect(key1 = isGradeMode) {
                                isShowingProblemRecords = false
                            }

                            ProblemTab(
                                problem,
                                problemRecords,
                                isGradeMode,
                                isShowingProblemRecords,
                                { isShowingProblemRecords = !isShowingProblemRecords },
                                { value: Color -> color = value }
                            )
                            AnimatedVisibility(
                                visible = isShowingProblemRecords,
                                enter = expandVertically(expandFrom = Alignment.Top),
                                exit = shrinkVertically(shrinkTowards = Alignment.Top)
                            ) {
                                Spacer(modifier = Modifier.height(10.dp))
                                ProblemRecordListTab(problemRecords)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProblemTab(
    problem: Problem,
    problemRecords: List<ProblemRecord>?,
    isGradeMode: Boolean,
    isVisible: Boolean,
    toggleVisibility: () -> Unit,
    setColor: (Color) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProblemNumberTab(problem, problemRecords, isGradeMode)
        ProblemTimberTab(problem, problemRecords, isGradeMode, setColor)
        ViewMoreButton(isVisible, toggleVisibility)
    }
}

@Composable
fun ProblemNumberTab(
    problem: Problem,
    problemRecords: List<ProblemRecord>?,
    isGradeMode: Boolean,
    focusManager: FocusManager = LocalFocusManager.current,
    context: Context = LocalContext.current,
    bookInfoViewModel: BookInfoViewModel = viewModel(),
    bookListViewModel: BookListViewModel = viewModel()
) {
    var isProblemNumberFocused by remember { mutableStateOf(false) }
    var problemNumberInput by remember { mutableStateOf("") }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .weight(1f),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (problemRecords == null) return@Box
            Row(modifier = Modifier.wrapContentSize()) {
                problemRecords.reversed().forEach { problemRecord ->
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = problemRecord.grade.text,
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp
                    )
                }
            }
        }
        BasicTextField(
            modifier = Modifier
                .width(60.dp)
                .onFocusChanged { isProblemNumberFocused = it.isFocused },
            enabled = !isGradeMode,
            value = problemNumberInput,
            onValueChange = { problemNumberInput = it },
            textStyle = TextStyle(
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (bookInfoViewModel.isProblemNumberDuplicated(problemNumberInput)) {
                    customToast("문제 번호가 중복됩니다", context)
                    problemNumberInput = problem.number
                } else {
                    bookInfoViewModel.updateProblemNumber(problem, problemNumberInput)
                    val book = bookInfoViewModel.bookInfo.value!!.selectedBook
                    bookListViewModel.updateBook(book)
                }
                focusManager.clearFocus()
                problemNumberInput = ""
            }),
            singleLine = true,
            maxLines = 1,
            decorationBox = { innerTextField ->
                if (!isProblemNumberFocused) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = problem.number
                    )
                }
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun RowScope.ProblemTimberTab(
    problem: Problem,
    problemRecords: List<ProblemRecord>?,
    isGradeMode: Boolean,
    setColor: (Color) -> Unit,
    context: Context = LocalContext.current,
    bookInfoViewModel: BookInfoViewModel = viewModel(),
    problemRecordListViewModel: ProblemRecordListViewModel = viewModel()
) {
    var isTimerRunning by remember { mutableStateOf(false) }
    var currentTimeRecord by remember { mutableIntStateOf(0) }
    var currentGrade: Grade by remember { mutableStateOf(Unranked) }
    val recentProblemRecord = problemRecords?.first()

    LaunchedEffect(key1 = isTimerRunning) {
        while (isTimerRunning) {
            delay(100)
            currentTimeRecord += 100
        }
    }
    LaunchedEffect(key1 = isGradeMode) {
        isTimerRunning = false
        // 채점 완료했을 때 문제 기록을 저장한다
        if (!isGradeMode && !shouldWaitToRecordAgain(recentProblemRecord) && currentGrade != Unranked) {
            problemRecordListViewModel.addProblemRecord(
                ProblemRecord(
                    bookId = bookInfoViewModel.bookInfo.value!!.selectedBook.id,
                    page = problem.page,
                    number = problem.number,
                    timeRecord = currentTimeRecord,
                    grade = currentGrade,
                    solvedAt = getNow()
                )
            )
        }
    }
    LaunchedEffect(key1 = currentGrade) {
        if (isGradeMode) {
            setColor(currentGrade.color)
        }
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable {
                if (shouldWaitToRecordAgain(recentProblemRecord)) {
                    if (!isGradeMode) customToast("${problem.number}번 문제는 내일 다시 풀 수 있습니다", context)
                    else customToast("${problem.number}번 문제는 내일 다시 채점 수 있습니다", context)
                } else {
                    if (!isGradeMode) isTimerRunning = !isTimerRunning
                    else {
                        if (currentTimeRecord == 0) customToast("아직 시간이 기록되지 않았습니다", context)
                        else currentGrade = currentGrade.next()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (!isGradeMode) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = if (currentTimeRecord > 0) toTimeFormat(currentTimeRecord) else "탭에서 시간 재기",
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = if (currentGrade == Unranked) "탭해서 채점하기" else currentGrade.text,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ViewMoreButton(isVisible: Boolean, toggleVisibility: () -> Unit) {
    val viewMoreIconRotationZ by animateFloatAsState(
        targetValue = if (!isVisible) 0f else 180f,
        animationSpec = tween(200),
        label = "rotationZ for view more icon"
    )

    IconButton(onClick = toggleVisibility) {
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "show problem records",
            modifier = Modifier.graphicsLayer {
                rotationZ = viewMoreIconRotationZ
            }
        )
    }
}

@Composable
fun ProblemRecordListTab(problemRecords: List<ProblemRecord>?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = SecondPrimary, shape = RoundedCornerShape(10.dp))
    ) {
        if (problemRecords == null) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                text = "아직 문제를 푼 기록이 없습니다",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
            return
        }
        problemRecords.map { problemRecord ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    text = with (problemRecord.solvedAt) { "$monthNumber/$dayOfMonth" },
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier
                        .weight(2f)
                        .wrapContentHeight(),
                    text = toSimpleTimeFormat(problemRecord.timeRecord),
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    text = problemRecord.grade.text,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun ComponentActivity.getDataFromViewModels() {
    val bookListViewModel: BookListViewModel by viewModels()
    val problemRecordListViewModel: ProblemRecordListViewModel by viewModels()

    bookListViewModel.getBookListFromLocalDB()
    problemRecordListViewModel.getProblemRecordsFromLocalDB()
}

fun setPage(pageString: String, bookInfoViewModel: BookInfoViewModel, pages: List<Int>, alert: () -> Unit) =
    try {
        val page = pageString.toInt()
        invokeAndBlock(SET_PAGE, 500) {
            if (page !in pages) throw IndexOutOfBoundsException("page out of pages // " +
                    "page: $page, pages: ${pages.first()} - ${pages.last()}")
            bookInfoViewModel.setCurrentPage(page)
        }
    } catch(e: Exception) {
        alert()
    }

fun notifyPageOutOfRange(context: Context, pages: List<Int>)
        = customToast("${pages.first()}과 ${pages.last()} 사이의 값을 입력해주세요", context)

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

fun toSimpleTimeFormat(timeRecord: Int): String {
    val s = (timeRecord / 1_000) % 60
    val m = (timeRecord / 100_000) % 60

    val sb = StringBuilder()
    if (m > 0) sb.append("${m}분 ")
    sb.append("${s}초")
    return sb.toString()
}

fun shouldWaitToRecordAgain(recentProblemRecord: ProblemRecord?) =
    recentProblemRecord != null && recentProblemRecord.solvedAt.date == getNow().date