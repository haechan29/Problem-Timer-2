package com.hc.problem_timer_2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hc.problem_timer_2.MainActivity.Companion.PAGE_ITEM_SIZE
import com.hc.problem_timer_2.ui.theme.ProblemTimer2Theme
import com.hc.problem_timer_2.util.Center
import com.hc.problem_timer_2.util.FlagController.invokeAndBlock
import com.hc.problem_timer_2.util.Flag.*
import com.hc.problem_timer_2.util.FlagController.block
import com.hc.problem_timer_2.util.ScrollPosition
import com.hc.problem_timer_2.util.Start
import com.hc.problem_timer_2.util.addedEmptyString
import com.hc.problem_timer_2.util.getFastScrollingFlingBehavior
import com.hc.problem_timer_2.util.toPx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos


class MainActivity : ComponentActivity() {
    val pageViewModel by viewModels<PageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProblemTimer2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TimerScreen(pageViewModel)
                }
            }
        }
    }

    companion object {
        const val PAGE_ITEM_SIZE = 40
    }
}

@Composable
fun TimerScreen(pageViewModel: PageViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(PAGE_ITEM_SIZE.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PageTab(pageViewModel)
            Spacer(modifier = Modifier.weight(1f))
            Row() {

            }
        }
    }
}

@Composable
fun PageTab(pageViewModel: PageViewModel) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val pages = (1..100).map { it.toString() }
        pageViewModel.setPageIdx(pages.indices.first)
        val scope = rememberCoroutineScope()
        val listState = LazyListState()
        PageButton(true) {
            invokeAndBlock(SET_PAGE, 500) {
                pageViewModel.varyPageIdx(-1)
            }
        }
        PageBox(pageViewModel, scope, pages, listState)
        PageButton(false) {
            invokeAndBlock(SET_PAGE, 500) {
                pageViewModel.varyPageIdx(1)
            }
        }
        pageViewModel.pageIdx.observe(LocalLifecycleOwner.current) {
            it ?: return@observe
            scope.launch { listState.animateScrollToItem(it) }
        }
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
    pageViewModel: PageViewModel,
    scope: CoroutineScope,
    pages: List<String>,
    listState: LazyListState
) {
    LazyRow(
        modifier = Modifier
            .width(PAGE_ITEM_SIZE.dp * 1.5f)
            .fillMaxHeight()
            .border(width = 1.dp, color = Color.Black),
        verticalAlignment = Alignment.CenterVertically,
        state = listState,
        contentPadding = PaddingValues(horizontal = 10.dp),
        userScrollEnabled = false
    ) {
        items(pages.size) { itemIdx ->
            // Todo : page 입력 기능 추가하기
            Text(
                modifier = Modifier
                    .width(PAGE_ITEM_SIZE.dp)
                    .fillMaxHeight()
                    .wrapContentHeight(),
                text = "${pages[itemIdx]}",
                textAlign = TextAlign.Center
            )
        }
    }
}