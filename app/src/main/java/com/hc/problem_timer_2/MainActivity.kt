package com.hc.problem_timer_2

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hc.problem_timer_2.MainActivity.Companion.PAGE_ITEM_SIZE
import com.hc.problem_timer_2.ui.theme.Primary
import com.hc.problem_timer_2.ui.theme.ProblemTimer2Theme
import com.hc.problem_timer_2.util.FlagController.invokeAndBlock
import com.hc.problem_timer_2.util.Flag.*
import com.hc.problem_timer_2.util.TimberDebugTree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BookTab()
        Divider(thickness = 1.dp, color = Color.LightGray)
        PageAndGradeTab(isGradeMode) { value: Boolean -> isGradeMode = value }
        Divider(thickness = 1.dp, color = Color.LightGray)
    }
}

@Composable
fun BookTab() {
    val books = listOf("책1", "책2")
    var selectedItemIndex by remember { mutableStateOf<Int?>(null) }
    LazyRow(
        modifier = Modifier
            .padding(all = 10.dp)
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(books.size + 1) { itemIndex ->
            if (itemIndex in books.indices) {
                Button(
                    modifier = Modifier
                        .wrapContentWidth()
                        .fillMaxHeight()
                        .background(color = Color.Transparent, shape = CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (itemIndex == selectedItemIndex) Color.Black else Color.LightGray,
                        contentColor = if (itemIndex == selectedItemIndex) Color.White else Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    onClick = {
                        selectedItemIndex = itemIndex
                    }
                ) {
                    Text(text = books[itemIndex])
                }
            } else {
                Button(
                    onClick = { /*TODO*/ },
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp)
                ) {
                    Text("교재 추가하기")
                }
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
    pageViewModel: PageViewModel = viewModel(),
    context: Context = LocalContext.current,
    listState: LazyListState = LazyListState()
) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val pages = (1..100).toList()
        pageViewModel.setPage(pages.first())
        observePage(pageViewModel = pageViewModel, listState = listState, pages = pages)

        PageButton(true) {
            setPage((pageViewModel.page.value!! - 1).toString(), pageViewModel, pages) {
                notifyPageOutOfRange(context, pages)
            }
        }
        PageBox(pages = pages, listState = listState)
        PageButton(false) {
            setPage((pageViewModel.page.value!! + 1).toString(), pageViewModel, pages) {
                notifyPageOutOfRange(context, pages)
            }
        }
    }
}

@Composable
fun observePage(
    pageViewModel: PageViewModel,
    listState: LazyListState,
    pages: List<Int>,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    pageViewModel.page.observe(LocalLifecycleOwner.current) { page ->
        page ?: return@observe
        scope.launch { listState.animateScrollToItem(pages.indexOf(page)) }
    }
}

@Composable
fun PageButton(
    isBeforeButton: Boolean,
    onClick: () -> Unit
) {
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
    pageViewModel: PageViewModel = viewModel(),
    pages: List<Int>,
    listState: LazyListState
) {
    var pageInput by remember { mutableStateOf("") }
    var isPageBoxFocused by remember { mutableStateOf(false) }
    val textColor = if (isPageBoxFocused) Color.Transparent else Color.Black
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    LazyRow(
        modifier = Modifier
            .width(PAGE_ITEM_SIZE.dp * 1.5f)
            .fillMaxHeight()
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(5.dp)),
        verticalAlignment = Alignment.CenterVertically,
        state = listState,
        contentPadding = PaddingValues(horizontal = 10.dp),
        userScrollEnabled = false
    ) {
        items(pages.size) { itemIdx ->
            BasicTextField(
                modifier = Modifier
                    .width(PAGE_ITEM_SIZE.dp)
                    .fillMaxHeight()
                    .wrapContentHeight()
                    .onFocusChanged { isPageBoxFocused = it.isFocused },
                value = pageInput,
                onValueChange = { pageInput = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    setPage(pageInput, pageViewModel, pages) { notifyPageOutOfRange(context, pages) }
                    pageInput = ""
                }),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                singleLine = true,
                maxLines = 1,
                decorationBox = { innerTextField ->
                    if (pageInput.isEmpty()) {
                        Text(
                            text = "${pages[itemIdx]}",
                            textAlign = TextAlign.Center,
                            color = textColor
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
fun GradeTab(isGradeMode: Boolean, setGradeMode: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(end = 10.dp),
            text = "채점\n하기",
            lineHeight = 14.sp,
            fontSize = 12.sp
        )
        Switch(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight(),
            checked = isGradeMode,
            onCheckedChange = { setGradeMode(it) },
            colors = SwitchDefaults.colors(
                checkedTrackColor = Primary,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

fun setPage(pageString: String, pageViewModel: PageViewModel, pages: List<Int>, alert: () -> Unit) =
    try {
        val page = pageString.toInt()
        invokeAndBlock(SET_PAGE, 500) {
            if (page !in pages)
                throw Exception("page out of range: page must be between ${pages.first()} and ${pages.last()}")
            pageViewModel.setPage(page)
        }
    } catch(e: Exception) {
        alert()
    }

fun notifyPageOutOfRange(context: Context, pages: List<Int>)
        = Toast.makeText(context, "${pages.first()}과 ${pages.last()} 사이의 값을 입력해주세요", Toast.LENGTH_SHORT).show()