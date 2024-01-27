package com.hc.problem_timer_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hc.problem_timer_2.MainActivity.Companion.PAGE_ITEM_SIZE
import com.hc.problem_timer_2.ui.theme.ProblemTimer2Theme
import com.hc.problem_timer_2.util.FlagController.invokeAndBlock
import com.hc.problem_timer_2.util.Flag.*
import com.hc.problem_timer_2.util.FlagController.block
import com.hc.problem_timer_2.util.addedEmptyString
import com.hc.problem_timer_2.util.getFastScrollingFlingBehavior
import com.hc.problem_timer_2.util.toPx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos

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
    }

    companion object {
        const val PAGE_ITEM_SIZE = 40
    }
}

@Composable
fun TimerScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(PAGE_ITEM_SIZE.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val pages = (1..100).map { it.toString() }.addedEmptyString(1)
                var currentPageIdx = pages.indices.first
                val scope = rememberCoroutineScope()
                val listState = LazyListState()
                val setCurrentPageIdx = { idx: Int -> currentPageIdx = idx }
                PageButton(true) {
                    invokeAndBlock(SET_PAGE, 500) {
                        scope.launch { listState.scrollTo(pageDiff = -1) }
                        currentPageIdx -= 1
                    }
                }
                PageBox(scope, pages, listState, setCurrentPageIdx)
                PageButton(false) {
                    invokeAndBlock(SET_PAGE, 500) {
                        scope.launch { listState.scrollTo(pageDiff = 1) }
                        currentPageIdx += 1
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row() {

            }
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
    scope: CoroutineScope,
    pages: List<String>,
    listState: LazyListState,
    setCurrentPageIdx: (Int) -> Unit
) {
    val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }
    val firstItemIdx by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    LazyRow(
        modifier = Modifier
            .width(PAGE_ITEM_SIZE.dp * 2)
            .fillMaxHeight()
            .border(width = 1.dp, color = Color.Black),
        verticalAlignment = Alignment.CenterVertically,
        state = listState,
        flingBehavior = getFastScrollingFlingBehavior(
            onStart = { block(SET_PAGE) { !listState.isScrollInProgress } },
            onFinish = {
                val closestItemToCenter = layoutInfo.getClosestItemToCenter()
                scope.launch { listState.scrollToCenterOf(closestItemToCenter) }
                setCurrentPageIdx(closestItemToCenter.index)
            }
        )
    ) {
        items(pages.size) { itemIdx ->
            if (itemIdx == pages.indices.first || itemIdx == pages.indices.last) {
                Spacer(modifier = Modifier.width(PAGE_ITEM_SIZE.dp / 2))
            }
            else {
                val idxInVisibleItems = itemIdx - firstItemIdx
                Text(
                    modifier = Modifier
                        .width(PAGE_ITEM_SIZE.dp)
                        .fillMaxHeight()
                        .wrapContentHeight()
                        .alpha(layoutInfo.getPageTextAlpha(idxInVisibleItems)),
                    text = "${pages[itemIdx]}",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

suspend fun LazyListState.scrollToCenterOf(item: LazyListItemInfo) {
    val delta = layoutInfo.getDistanceFromViewportCenter(item)
    animateScrollBy(-delta.toFloat())
}

fun LazyListLayoutInfo.getClosestItemToCenter() = visibleItemsInfo.minBy { abs(getDistanceFromViewportCenter(it)) }
suspend fun LazyListState.scrollTo(pageDiff: Int) = animateScrollBy(PAGE_ITEM_SIZE.toPx() * pageDiff.toFloat())

fun LazyListLayoutInfo.getPageTextAlpha(idxInVisibleItems: Int) =
    if (isItemVisible(idxInVisibleItems)) {
        val itemInfo = visibleItemsInfo[idxInVisibleItems]
        val dist = getDistanceFromViewportCenter(itemInfo)
        cos(dist / PAGE_ITEM_SIZE.toPx() * 1.5f)
    } else {
        0f
    }

fun LazyListLayoutInfo.getDistanceFromViewportCenter(itemInfo: LazyListItemInfo) = viewportSize.width / 2 - getItemCenterOffset(itemInfo)
fun getItemCenterOffset(itemInfo: LazyListItemInfo) = with(itemInfo) { offset + size / 2 }
fun LazyListLayoutInfo.isItemVisible(idxInVisibleItems: Int) = idxInVisibleItems in visibleItemsInfo.indices

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProblemTimer2Theme {
        TimerScreen()
    }
}