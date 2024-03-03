package com.hc.problem_timer_2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hc.problem_timer_2.MainActivity
import com.hc.problem_timer_2.R
import com.hc.problem_timer_2.ui.view.BasicTextFieldWithHint
import com.hc.problem_timer_2.util.JamoUtil
import com.hc.problem_timer_2.ui.view.TextWithoutPadding
import com.hc.problem_timer_2.util.notosanskr
import com.hc.problem_timer_2.ui.viewmodel.BookListViewModel
import com.hc.problem_timer_2.data.vo.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddBookScreen(
    hideAddBookScreen: () -> Unit,
    bookListViewModel: BookListViewModel = viewModel()
) {
    var bookNameInput by remember { mutableStateOf("") }
    val addBook = { book: Book -> bookListViewModel.addBook(book) }
    Column(modifier = Modifier.background(Color.White)) {
        SearchBarTab({ bookNameInput }, { value: String -> bookNameInput = value }, addBook, hideAddBookScreen)
        Spacer(modifier = Modifier.height(12.dp))
        SearchResultTab({ bookNameInput }, addBook, hideAddBookScreen)
    }
}

@Composable
fun SearchBarTab(
    getBookNameInput: () -> String,
    setBookNameInput: (String) -> Unit,
    addBook: (Book) -> Unit,
    hideAddBookScreen: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope()
) {
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
                value = getBookNameInput(),
                onValueChange = { if (it.length <= MainActivity.BOOK_NAME_LENGTH_MAX) setBookNameInput(it) },
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
                    if (getBookNameInput().isNotEmpty()) {
                        addBook(Book(name = getBookNameInput()))
                        hideAddBookScreen()
                    }
                    setBookNameInput("")
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
                            while (getBookNameInput().isNotEmpty()) {
                                delay(50)
                                setBookNameInput(getBookNameInput().slice(0 until getBookNameInput().lastIndex))
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
fun ColumnScope.SearchResultTab(
    getBookNameInput: () -> String,
    addBook: (Book) -> Unit,
    hideAddBookScreen: () -> Unit
) {
    val books = listOf("개념원리", "쎈", "수학의정석", "라이트쎈", "블랙라벨", "자이스토리", "수학의바이블").sorted()
    val filteredBooks = books.filter { book ->
        JamoUtil.toJamoeum(book).startsWith(JamoUtil.toJamoeum(getBookNameInput()))
    }
    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        items(filteredBooks) { book ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable {
                        addBook(Book(name = book))
                        hideAddBookScreen()
                    }
                    .padding(vertical = 16.dp)
            ) {
                TextWithoutPadding(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Start,
                    text = book,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = notosanskr,
                )
            }
        }
    }
}