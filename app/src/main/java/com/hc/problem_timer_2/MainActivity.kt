package com.hc.problem_timer_2

import android.content.Context
import android.hardware.camera2.params.BlackLevelPattern
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.PlatformTextStyle
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
import com.hc.problem_timer_2.MainActivity.Companion.BOOK_NAME_LENGTH_MAX
import com.hc.problem_timer_2.MainActivity.Companion.POSITIVE_INTEGER_MATCHER
import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem
import com.hc.problem_timer_2.vo.ProblemRecord
import com.hc.problem_timer_2.util.BaseAlertDialog
import com.hc.problem_timer_2.util.BaseDialog
import com.hc.problem_timer_2.util.BasicTextFieldWithHint
import com.hc.problem_timer_2.util.BasicTextFieldWithoutPadding
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
                    TimerScreen()
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
fun TimerScreen(
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel()
) {
    val selectedBookInfo by selectedBookInfoViewModel.selectedBookInfo.observeAsState()
    var isShowingAddBookDialog by remember { mutableStateOf(false) }
    var problemToUpdate by remember { mutableStateOf<Problem?>(null) }
    var problemToEdit by remember { mutableStateOf<Problem?>(null) }

    if (!isShowingAddBookDialog) {
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize()
        ) {
            BookTab({ isShowingAddBookDialog = true })
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
            ProblemListTab(
                { value: Problem -> problemToUpdate = value },
                { value: Problem -> problemToEdit == value },
                { problemToEdit = null })
//        if (isShowingAddBookDialog) AddBookDialog { isShowingAddBookDialog = false }
            if (problemToUpdate != null) UpdateProblemDialog(
                problemToUpdate!!,
                { problemToUpdate = null },
                { problemToEdit = problemToUpdate })
        }
    }
    if (isShowingAddBookDialog) {
        AddBookScreen( { isShowingAddBookDialog = false } )
    }
}

@Composable
fun BookTab(
    showAddBookDialog: () -> Unit,
    bookListViewModel: BookListViewModel = viewModel(),
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel()
) {
    val books by bookListViewModel.bookList.observeAsState()
    val selectedBookInfo by selectedBookInfoViewModel.selectedBookInfo.observeAsState()
    var isShowingDeleteBookBtn by remember { mutableStateOf(false) }

    BookTabStateless(
        books!!,
        { book: Book -> selectedBookInfo!!.selectedBook == book },
        { book: Book -> selectedBookInfoViewModel.select(book) },
        showAddBookDialog,
        { isShowingDeleteBookBtn },
        { value: Boolean -> isShowingDeleteBookBtn = value}
    )
}

@Composable
fun BookTabStateless(
    books: List<Book>,
    isSelected: (Book) -> Boolean,
    selectBook: (Book) -> Unit,
    showAddBookDialog: () -> Unit,
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
    problemListViewModel: ProblemListViewModel = viewModel(),
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
                        bookListViewModel.deleteBook(book.id)
                        problemListViewModel.deleteProblemsOnBook(book.id)
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
fun AddBookButton(showAddBookDialog: () -> Unit) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(30.dp)
            .background(color = colorResource(R.color.black_200), shape = RoundedCornerShape(10.dp))
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            .clickable {
                showAddBookDialog()
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
fun AddBookScreen(hideAddBookScreen: () -> Unit) {
    Column(modifier = Modifier.background(Color.White)) {
        SearchBarTab(hideAddBookScreen)
        SearchResultTab()
    }
}

@Composable
fun SearchBarTab(
    hideAddBookScreen: () -> Unit,
    bookListViewModel: BookListViewModel = viewModel(),
    scope: CoroutineScope = rememberCoroutineScope()
) {
    var bookNameInput by remember { mutableStateOf("") }
    Row(
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(32.dp)
                .clickable { hideAddBookScreen() },
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "back to timer screen"
        )
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .background(
                    color = colorResource(id = R.color.black_200),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = colorResource(id = R.color.black_500)
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextFieldWithHint(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .padding(vertical = 1.dp),
                value = bookNameInput,
                onValueChange = { if (it.length <= BOOK_NAME_LENGTH_MAX) bookNameInput = it },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = notosanskr,
                    color = colorResource(id = R.color.black_500)
                ),
                hint = "추가할 교재 이름을 입력해주세요",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (bookNameInput.isNotEmpty()) {
                        bookListViewModel.addBook(Book(name = bookNameInput))
                        hideAddBookScreen()
                    }
                    bookNameInput = ""
                }),
                singleLine = true,
                maxLines = 1,
                decorationBox = { innerTextField ->
                    innerTextField()
                }
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(color = colorResource(id = R.color.black_500), shape = CircleShape)
                    .clickable {
                        scope.launch {
                            while (bookNameInput.isNotEmpty()) {
                                delay(50)
                                bookNameInput = bookNameInput.slice(0 until bookNameInput.lastIndex)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(10.dp),
                    imageVector = Icons.Default.Clear,
                    contentDescription = "clear book name input",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun SearchResultTab() {

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
    if (pages.isEmpty()) {
        problemListViewModel.addDefaultProblems(bookInfo!!.selectedBook!!.id)
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
            problemListViewModel.addDefaultProblems(bookId)
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
fun ColumnScope.ProblemListTab(
    setProblemToUpdate: (Problem) -> Unit,
    isProblemEditing: (Problem) -> Boolean,
    finishEditingProblem: () -> Unit,
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel(),
    problemListViewModel: ProblemListViewModel = viewModel(),
    problemRecordListViewModel: ProblemRecordListViewModel = viewModel()
) {
    val problems by problemListViewModel.problems.observeAsState()
    val problemRecordList by problemRecordListViewModel.problemRecordListOnSelectedPage.observeAsState()
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

    ProblemListTabStateless(
        selectedBookInfo!!.selectedPage,
        selectedBookInfo!!.isBookSelected(),
        selectedBookInfo!!.isPageSelected(),
        problemsOnSelectedPage,
        problemRecordsMapOnSelectedPage,
        setProblemToUpdate,
        isProblemEditing,
        finishEditingProblem,
        { isGradeMode },
        { isGradeMode = !isGradeMode }
    )
}

@Composable
fun ColumnScope.ProblemListTabStateless(
    selectedPage: Int?,
    isBookSelected: Boolean,
    isPageSelected: Boolean,
    problemsOnSelectedPage: List<Problem>,
    problemRecordsMapOnSelectedPage: Map<String, List<ProblemRecord>>,
    setProblemToUpdate: (Problem) -> Unit,
    isProblemEditing: (Problem) -> Boolean,
    finishEditingProblem: () -> Unit,
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
                text = if (!isBookSelected) context.getString(R.string.select_book) else context.getString(R.string.select_page),
                fontSize = 12.sp
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                ProblemListHeaderTab(isGradeMode, toggleGradeMode)
                ProblemListBodyTab(
                    selectedPage,
                    problemsOnSelectedPage,
                    problemRecordsMapOnSelectedPage,
                    setProblemToUpdate,
                    isProblemEditing,
                    finishEditingProblem,
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
fun ColumnScope.ProblemListHeaderTab(isGradeMode: () -> Boolean, toggleGradeMode: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        this@ProblemListHeaderTab.AnimatedVisibility(
            visible = !isGradeMode(),
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut()
        ) {
            ProblemListHeaderInNormalModeTab(toggleGradeMode)
        }
        this@ProblemListHeaderTab.AnimatedVisibility(
            visible = isGradeMode(),
            enter = slideInHorizontally(),
            exit = slideOutHorizontally(animationSpec = tween(500))
        ) {
            ProblemListHeaderInGradeModeTab()
        }
    }
}

@Composable
fun ProblemListHeaderInNormalModeTab(toggleGradeMode: () -> Unit) {
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
fun ProblemListHeaderInGradeModeTab() {
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
fun ColumnScope.ProblemListBodyTab(
    selectedPage: Int?,
    problemsOnSelectedPage: List<Problem>,
    problemRecordsMapOnSelectedPage: Map<String, List<ProblemRecord>>,
    setProblemToUpdate: (Problem) -> Unit,
    isProblemEditing: (Problem) -> Boolean,
    finishEditingProblem: () -> Unit,
    isGradeMode: () -> Boolean,
    problemRecordListViewModel: ProblemRecordListViewModel = viewModel()
) {
    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .wrapContentHeight(),
        contentPadding = PaddingValues(vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items = problemsOnSelectedPage) { problem ->
            val problemRecords =
                problemRecordsMapOnSelectedPage[problem.number] ?: emptyList()
            var isShowingProblemRecords by remember { mutableStateOf(false) }
            var currentTimeRecord by remember { mutableIntStateOf(0) }
            var currentGrade by remember { mutableStateOf(Unranked) }
            val addProblemRecord = {
                problemRecordListViewModel.addProblemRecord(
                    ProblemRecord(
                        bookId = problem.bookId,
                        page = problem.page,
                        number = problem.number,
                        timeRecord = currentTimeRecord,
                        grade = currentGrade,
                        solvedAt = getNow()
                    )
                )
            }

            ProblemAndProblemRecordTabStateless(
                selectedPage,
                problem,
                problemRecords,
                { setProblemToUpdate(problem) },
                { isProblemEditing(problem) },
                finishEditingProblem,
                isGradeMode,
                { currentTimeRecord },
                { value: Int -> currentTimeRecord += value },
                { isShowingProblemRecords },
                { value: Boolean -> isShowingProblemRecords = value },
                { currentGrade },
                { currentGrade = currentGrade.next() },
                addProblemRecord
            )
        }
    }
}

@Composable
fun ProblemAndProblemRecordTabStateless(
    selectedPage: Int?,
    problem: Problem,
    problemRecords: List<ProblemRecord>,
    updateProblem: () -> Unit,
    isProblemEditing: () -> Boolean,
    finishEditingProblem: () -> Unit,
    isGradeMode: () -> Boolean,
    getCurrentTimeRecord: () -> Int,
    increaseCurrentTimeRecord: (Int) -> Unit,
    isShowingProblemRecords: () -> Boolean,
    setShowingProblemRecords: (Boolean) -> Unit,
    getCurrentGrade: () -> Grade,
    setNextGrade: () -> Unit,
    addProblemRecord: () -> Unit
) {
    LaunchedEffect(key1 = selectedPage) {
        setShowingProblemRecords(false)
    }

    LaunchedEffect(key1 = isGradeMode()) {
        setShowingProblemRecords(false)
        if (!isGradeMode() && !shouldWaitToRecordAgain(problemRecords.firstOrNull()) && getCurrentGrade() != Unranked) {
            addProblemRecord()
        }
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
            Row(
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(all = 20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyProblemNumberTab(problem, getCurrentGrade)
                Spacer(Modifier.width(24.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clickable { setNextGrade() },
                    contentAlignment = Alignment.Center
                ) {
                    TextWithoutPadding(
                        modifier = Modifier.wrapContentWidth(),
                        textAlign = TextAlign.Center,
                        text = getCurrentGrade().text,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = notosanskr,
                        color = getCurrentGrade().color
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(all = 20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                ProblemContentTab(problem, getCurrentTimeRecord, increaseCurrentTimeRecord, getCurrentGrade)
                AnimatedVisibility(
                    visible = isShowingProblemRecords(),
                    enter = expandVertically(expandFrom = Alignment.Top),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        ProblemRecordListTab(problemRecords)
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
                    isShowingProblemRecords,
                    { setShowingProblemRecords(!isShowingProblemRecords()) }
                )
//            ProblemTab(
//                problem,
//                problemRecords,
//                updateProblem,
//                isProblemEditing,
//                finishEditingProblem,
//                isGradeMode,
//                isShowingProblemRecords,
//                { setShowingProblemRecords(!isShowingProblemRecords) }
//            )
//            AnimatedVisibility(
//                visible = isShowingProblemRecords,
//                enter = expandVertically(expandFrom = Alignment.Top),
//                exit = shrinkVertically(shrinkTowards = Alignment.Top)
//            ) {
//                Spacer(modifier = Modifier.height(10.dp))
//                ProblemRecordListTab(problemRecords)
//            }
            }
        }
    }
}

@Composable
fun ProblemContentTab(
    problem: Problem,
    getCurrentTimeRecord: () -> Int,
    increaseCurrentTimeRecord: (Int) -> Unit,
    getCurrentGrade: () -> Grade
) {
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isTimerRunning) {
        while (isTimerRunning) {
            delay(100)
            increaseCurrentTimeRecord(100)
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
        MyProblemNumberTab(problem, getCurrentGrade)
        Spacer(Modifier.width(24.dp))
        MyProblemTimerTab(getCurrentTimeRecord)
        Spacer(Modifier.weight(1f))
        MyProblemTimberButton(isTimerRunning) { isTimerRunning = !isTimerRunning }
    }
}

@Composable
fun MyProblemNumberTab(problem: Problem, getCurrentGrade: () -> Grade) {
    Box(
        modifier = Modifier
            .padding(vertical = 5.dp)
            .size(40.dp)
            .background(color = getCurrentGrade().color, shape = RoundedCornerShape(10.dp)),
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
fun MyProblemTimerTab(getCurrentTimeRecord: () -> Int) {
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
fun MyProblemTimberButton(isTimerRunning: Boolean, toggleTimerRunning: () -> Unit) {
    Row(
        modifier = Modifier
            .background(
                color = colorResource(id = R.color.black_200),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { toggleTimerRunning() }
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
            color = colorResource(id = R.color.black_500)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.clock_24px),
            contentDescription = "record time",
        )
    }
}

@Composable
fun ProblemRecordViewMoreTab(
    isShowingProblemRecords: () -> Boolean,
    toggleVisibilityOfProblemRecords: () -> Unit
) {
    val viewMoreIconRotationZ by animateFloatAsState(
        targetValue = if (!isShowingProblemRecords()) 0f else 180f,
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
            text = if (isShowingProblemRecords()) "접기" else "내 기록 보기",
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
fun ProblemTab(
    problem: Problem,
    problemRecords: List<ProblemRecord>,
    updateProblem: () -> Unit,
    isProblemEditing: () -> Boolean,
    finishEditingProblem: () -> Unit,
    isGradeMode: () -> Boolean,
    isVisible: Boolean,
    toggleVisibility: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProblemNumberTab(problem, problemRecords, updateProblem, isProblemEditing, finishEditingProblem, isGradeMode)
        ProblemTimerTab(problem, problemRecords, isGradeMode)
        ViewMoreButton(isVisible, toggleVisibility)
    }
}

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
            .background(
                color = colorResource(id = R.color.black_100),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 30.dp, vertical = 15.dp)
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
                        text = problemRecord.grade.text,
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