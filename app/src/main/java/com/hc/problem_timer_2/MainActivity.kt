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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import com.hc.problem_timer_2.MainActivity.Companion.POSITIVE_INTEGER_MATCHER
import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem
import com.hc.problem_timer_2.vo.ProblemRecord
import com.hc.problem_timer_2.ui.theme.BackgroundGrey
import com.hc.problem_timer_2.util.BaseAlertDialog
import com.hc.problem_timer_2.util.BaseDialog
import com.hc.problem_timer_2.util.TextWithoutPadding
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
                    TimerScreen()
                }
            }
        }
        Timber.plant(TimberDebugTree)
    }

    companion object {
        const val PAGE_ITEM_SIZE = 40
        const val POSITIVE_INTEGER_MATCHER = "^([1-9]\\d*)$"
    }
}

@Composable
fun TimerScreen() {
    var isGradeMode by remember { mutableStateOf(false) }
    var isShowingAddBookDialog by remember { mutableStateOf(false) }
    var problemToUpdate by remember { mutableStateOf<Problem?>(null) }
    var problemToEdit by remember { mutableStateOf<Problem?>(null) }
    Column(modifier = Modifier.fillMaxSize()) {
        BookTab { isShowingAddBookDialog = true }
        Divider(thickness = 1.dp, color = Color.LightGray)
//        PageAndGradeTab({ isGradeMode }, { value: Boolean -> isGradeMode = value })
        PageTab()
        Divider(thickness = 1.dp, color = Color.LightGray)
        if (isShowingAddBookDialog) AddBookDialog { isShowingAddBookDialog = false }
        ProblemListTab({ isGradeMode }, { value: Problem -> problemToUpdate = value }, { value: Problem -> problemToEdit == value }, { problemToEdit = null })
        if (problemToUpdate != null) UpdateProblemDialog(problemToUpdate!!, { problemToUpdate = null }, { problemToEdit = problemToUpdate })
    }
}

@Composable
fun BookTab(
    bookListViewModel: BookListViewModel = viewModel(),
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel(),
    showAddBookDialog: () -> Unit
) {
    val books by bookListViewModel.bookList.observeAsState()
    var selectedItemIndex by remember { mutableStateOf<Int?>(null) }
    val selectedBook by remember { derivedStateOf<Book?> {
        if (selectedItemIndex == null) null
        else books!![selectedItemIndex!!]
    } }
    var isShowingDeleteBookBtn by remember { mutableStateOf(false) }
    if (selectedBook != null) { selectedBookInfoViewModel.select(selectedBook!!) }
    BookTabStateless(
        books!!,
        { selectedItemIndex },
        { value: Int -> selectedItemIndex = value },
        showAddBookDialog,
        { isShowingDeleteBookBtn },
        { value: Boolean -> isShowingDeleteBookBtn = value}
    )
}

@Composable
fun BookTabStateless(
    books: List<Book>,
    getSelectedItemIndex: () -> Int?,
    setSelectedItemIndex: (Int) -> Unit,
    showAddBookDialog: () -> Unit,
    isDeleteBookBtnVisible: () -> Boolean,
    setVisibilityOfDeleteButton: (Boolean) -> Unit
) {
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
            color = colorResource(id = R.color.black_200)
        )
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(books.size) { itemIndex ->
                BookButton(
                    books[itemIndex],
                    getSelectedItemIndex() == itemIndex,
                    { setSelectedItemIndex(itemIndex) },
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
            color = colorResource(id = R.color.black_200)
        )
        AddBookButton(showAddBookDialog)
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
    bookListViewModel: BookListViewModel = viewModel(),
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
                    color = if (isSelected) colorResource(R.color.black_100) else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
        ) {
            TextWithoutPadding(
                modifier = Modifier
                    .combinedClickable(
                        onClick = selectBook,
                        onLongClick = toggleVisibilityOfDeleteButton
                    ),
                text = book.name,
                fontFamily = notosanskr,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = colorResource(id = R.color.black_400)
            )
        }
        if (isDeleteBookBtnVisible()) {
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
fun AddBookButton(showAddBookDialog: () -> Unit) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(30.dp)
            .background(color = colorResource(R.color.black_100), shape = RoundedCornerShape(10.dp))
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            .clickable { showAddBookDialog() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextWithoutPadding(
            text = "추가",
            fontSize = 12.sp,
            fontFamily = notosanskr,
            fontWeight = FontWeight.Normal,
            color = colorResource(id = R.color.black_600)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookDialog(
    bookListViewModel: BookListViewModel = viewModel(),
    hideDialog: () -> Unit
) {
    var bookName by remember { mutableStateOf("") }
    BaseAlertDialog(
        title = "추가할 교재를 입력해주세요",
        confirmText = "교재 추가",
        dismissText = "취소",
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
        onConfirm = { bookListViewModel.addBook(Book(name = bookName)) },
        hideDialog = hideDialog
    )
}

@Composable
fun GradeTab(
    isGradeMode: () -> Boolean,
    setGradeMode: (Boolean) -> Unit,
    context: Context = LocalContext.current,
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel()
) {
    val bookInfo by selectedBookInfoViewModel.selectedBookInfo.observeAsState()
    var text by remember { mutableStateOf(context.getString(R.string.view_problems)) }
    val progress by animateFloatAsState(
        targetValue = if (isGradeMode()) 1f else 0f,
        label = "",
        finishedListener = { text = if (isGradeMode()) context.getString(R.string.finish_grade) else context.getString(R.string.view_problems) }
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
            checked = isGradeMode(),
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

@Composable
fun PageTab(
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel(),
    problemListViewModel: ProblemListViewModel = viewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    listState: LazyListState = rememberLazyListState()
) {
    val bookInfo by selectedBookInfoViewModel.selectedBookInfo.observeAsState()
    val problems by problemListViewModel.problems.observeAsState()
    if (!bookInfo!!.isBookSelected()) return
    val pages = problems!!
        .filter { it.bookId == bookInfo!!.selectedBook!!.id }
        .map { it.page }
        .distinct()
    if (pages.isEmpty()) problemListViewModel.addDefaultProblems(bookInfo!!.selectedBook!!.id)
    val selectedPage = bookInfo!!.selectedPage

    LaunchedEffect(key1 = selectedPage) {
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
            fontWeight = FontWeight.Medium,
            fontFamily = notosanskr
        )
        Divider(
            modifier = Modifier
                .padding(start = 5.dp, end = 15.dp, top = 5.dp, bottom = 5.dp)
                .width(1.dp)
                .height(20.dp),
            color = colorResource(id = R.color.black_200)
        )
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            state = listState
        ) {
            items(pages) { page ->
                MyPageButton(
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
            color = colorResource(id = R.color.black_200)
        )
        AddPageButton {}
    }
}

@Composable
fun MyPageButton(
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
                    color = if (isSelected) colorResource(R.color.black_100) else Color.Transparent,
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
                color = colorResource(id = R.color.black_400)
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
            .background(color = colorResource(R.color.black_100), shape = RoundedCornerShape(10.dp))
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            .clickable { addPages() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextWithoutPadding(
            text = "추가",
            fontSize = 12.sp,
            fontFamily = notosanskr,
            fontWeight = FontWeight.Normal,
            color = colorResource(id = R.color.black_600)
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
fun ColumnScope.ProblemListTab(
    isGradeMode: () -> Boolean,
    setProblemToUpdate: (Problem) -> Unit,
    isProblemEditing: (Problem) -> Boolean,
    finishEditingProblem: () -> Unit,
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel(),
    problemListViewModel: ProblemListViewModel = viewModel(),
    problemRecordListViewModel: ProblemRecordListViewModel = viewModel()
) {
    val problems by problemListViewModel.problems.observeAsState()
    val problemRecordList by problemRecordListViewModel.problemRecordListOnSelectedPage.observeAsState()
    val bookInfo by selectedBookInfoViewModel.selectedBookInfo.observeAsState()
    val problemsOnSelectedPage = problems!!
        .onBook(bookInfo!!.selectedBook?.id)
        .onPage(bookInfo!!.selectedPage)
        .sorted()
    val problemRecordListMapOnSelectedPage = problemRecordList!!
        .onBook(bookInfo!!.selectedBook)
        .onPage(bookInfo!!.selectedPage)
        .toProblemRecordListMap()
    ProblemListTabStateless(problemsOnSelectedPage, problemRecordListMapOnSelectedPage, setProblemToUpdate, isProblemEditing, finishEditingProblem, isGradeMode)
}

@Composable
fun ColumnScope.ProblemListTabStateless(
    problemsOnSelectedPage: List<Problem>,
    problemRecordListMapOnSelectedPage: Map<String, List<ProblemRecord>>,
    setProblemToUpdate: (Problem) -> Unit,
    isProblemEditing: (Problem) -> Boolean,
    finishEditingProblem: () -> Unit,
    isGradeMode: () -> Boolean,
    context: Context = LocalContext.current,
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel(),
    problemListViewModel: ProblemListViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        if (!selectedBookInfoViewModel.selectedBookInfo.value!!.isBookSelected()) {
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
                items(items = problemsOnSelectedPage) { problem ->
                    val problemRecords =
                        problemRecordListMapOnSelectedPage[problem.number] ?: emptyList()
                    var color by remember { mutableStateOf(BackgroundGrey) }
                    var isShowingProblemRecords by remember { mutableStateOf(false) }

                    ProblemAndProblemRecordTabStateless(
                        problem,
                        problemRecords,
                        { setProblemToUpdate(problem) },
                        { isProblemEditing(problem) },
                        finishEditingProblem,
                        isGradeMode,
                        color,
                        { value: Color -> color = value },
                        isShowingProblemRecords,
                        { value: Boolean -> isShowingProblemRecords = value }
                    )
                    if (problemListViewModel.isLastProblem(problem)) {
                        Button(
                            modifier = Modifier.padding(10.dp),
                            onClick = { problemListViewModel.addDefaultProblems(problem.bookId) },
                            colors = ButtonDefaults.buttonColors(contentColor = Color.White)
                        ) {
                            Text("문제 추가하기")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProblemAndProblemRecordTabStateless(
    problem: Problem,
    problemRecords: List<ProblemRecord>,
    updateProblem: () -> Unit,
    isProblemEditing: () -> Boolean,
    finishEditingProblem: () -> Unit,
    isGradeMode: () -> Boolean,
    color: Color,
    setColor: (Color) -> Unit,
    isShowingProblemRecords: Boolean,
    setShowingProblemRecords: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = if (!isGradeMode()) BackgroundGrey else color
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
        ) {
            LaunchedEffect(key1 = isGradeMode()) { setShowingProblemRecords(false) }

            ProblemTab(
                problem,
                problemRecords,
                updateProblem,
                isProblemEditing,
                finishEditingProblem,
                isGradeMode,
                isShowingProblemRecords,
                { setShowingProblemRecords(!isShowingProblemRecords) },
                setColor
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

@Composable
fun ProblemTab(
    problem: Problem,
    problemRecords: List<ProblemRecord>,
    updateProblem: () -> Unit,
    isProblemEditing: () -> Boolean,
    finishEditingProblem: () -> Unit,
    isGradeMode: () -> Boolean,
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
        ProblemNumberTab(problem, problemRecords, updateProblem, isProblemEditing, finishEditingProblem, isGradeMode)
        ProblemTimerTab(problem, problemRecords, isGradeMode, setColor)
        ViewMoreButton(isVisible, toggleVisibility)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProblemNumberTab(
    problem: Problem,
    problemRecords: List<ProblemRecord>,
    updateProblem: () -> Unit,
    isProblemEditing: () -> Boolean,
    finishEditingProblem: () -> Unit,
    isGradeMode: () -> Boolean,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ProblemNumberHeaderTab(problemRecords)
        ProblemNumberBodyTab(problem, updateProblem, isProblemEditing, finishEditingProblem, isGradeMode)
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ColumnScope.ProblemNumberHeaderTab(problemRecords: List<ProblemRecord>) {
    Box(
        modifier = Modifier
            .width(50.dp)
            .weight(1f),
        contentAlignment = Alignment.BottomCenter
    ) {
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
fun RowScope.ProblemTimerTab(
    problem: Problem,
    problemRecords: List<ProblemRecord>,
    isGradeMode: () -> Boolean,
    setColor: (Color) -> Unit,
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel(),
    problemRecordListViewModel: ProblemRecordListViewModel = viewModel()
) {
    var isTimerRunning by remember { mutableStateOf(false) }
    var currentTimeRecord by remember { mutableIntStateOf(0) }
    var currentGrade: Grade by remember { mutableStateOf(Unranked) }
    val recentProblemRecord = if (problemRecords.isEmpty()) null else problemRecords.first()

    LaunchedEffect(key1 = isTimerRunning) {
        while (isTimerRunning) {
            delay(100)
            currentTimeRecord += 100
        }
    }
    LaunchedEffect(key1 = isGradeMode()) {
        isTimerRunning = false
        // 채점 완료했을 때 문제 기록을 저장한다
        if (!isGradeMode() && !shouldWaitToRecordAgain(recentProblemRecord) && currentGrade != Unranked) {
            problemRecordListViewModel.addProblemRecord(
                ProblemRecord(
                    bookId = selectedBookInfoViewModel.selectedBookInfo.value!!.selectedBook!!.id,
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
        if (isGradeMode()) setColor(currentGrade.color)
    }

    ProblemTimerTabStateless(
        problem,
        recentProblemRecord,
        isGradeMode,
        { isTimerRunning = !isTimerRunning },
        { currentTimeRecord },
        { currentGrade },
        { currentGrade = currentGrade.next() }
    )
}

@Composable
fun RowScope.ProblemTimerTabStateless(
    problem: Problem,
    recentProblemRecord: ProblemRecord?,
    isGradeMode: () -> Boolean,
    toggleTimerRunning: () -> Unit,
    getCurrentTimeRecord: () -> Int,
    getCurrentGrade: () -> Grade,
    setNextCurrentGrade: () -> Unit,
    context: Context = LocalContext.current,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable {
                if (shouldWaitToRecordAgain(recentProblemRecord)) {
                    if (!isGradeMode()) customToast(
                        "${problem.number}번 문제는 내일 다시 풀 수 있습니다",
                        context
                    )
                    else customToast("${problem.number}번 문제는 내일 다시 채점 수 있습니다", context)
                } else {
                    if (!isGradeMode()) toggleTimerRunning()
                    else {
                        if (getCurrentTimeRecord() == 0) customToast("아직 시간이 기록되지 않았습니다", context)
                        else setNextCurrentGrade()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (!isGradeMode()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = if (getCurrentTimeRecord() > 0) toTimeFormat(getCurrentTimeRecord()) else "탭에서 시간 재기",
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = if (getCurrentGrade() == Unranked) "탭해서 채점하기" else getCurrentGrade().text,
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
fun ProblemRecordListTab(problemRecords: List<ProblemRecord>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = SecondPrimary, shape = RoundedCornerShape(10.dp))
    ) {
        if (problemRecords.isEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                text = stringResource(R.string.no_problem_record),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
        } else {
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
                        text = with(problemRecord.solvedAt) { "$monthNumber/$dayOfMonth" },
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

fun isPositiveInteger(s: String) = s.matches(POSITIVE_INTEGER_MATCHER.toRegex())

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