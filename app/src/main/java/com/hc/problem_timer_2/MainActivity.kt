package com.hc.problem_timer_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hc.problem_timer_2.ui.theme.ProblemTimer2Theme
import com.hc.problem_timer_2.util.addedEmptyString
import com.hc.problem_timer_2.util.getFastScrollingFlingBehavior
import com.hc.problem_timer_2.util.toPx
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
}

@Composable
fun TimerScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PageBox(40)
            }
            Spacer(modifier = Modifier.weight(1f))
            Row() {

            }
        }
    }
}

@Composable
fun PageBox(itemSize: Int) {
    val listState = rememberLazyListState()
    val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }
    val scope = rememberCoroutineScope()
    val firstItemIdx by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    LazyRow(
        modifier = Modifier
            .width(itemSize.dp * 2)
            .wrapContentHeight()
            .border(width = 1.dp, color = Color.Black),
        verticalAlignment = Alignment.CenterVertically,
        state = listState,
        flingBehavior = getFastScrollingFlingBehavior {
            scope.launch {
                scrollToCenter(listState, layoutInfo)
            }
        }
    ) {
        val pages = (1..100).map { it.toString() }.addedEmptyString(1)
        items(pages.size) { itemIdx ->
            if (itemIdx == pages.indices.first || itemIdx == pages.indices.last) {
                Spacer(modifier = Modifier.width(itemSize.dp / 2))
            }
            else {
                val idxInVisibleItems = itemIdx - firstItemIdx
                Text(
                    modifier = Modifier
                        .width(itemSize.dp)
                        .height(40.dp)
                        .wrapContentHeight()
                        .alpha(
                            if (isItemVisible(layoutInfo, idxInVisibleItems)) {
                                val itemInfo = layoutInfo.visibleItemsInfo[idxInVisibleItems]
                                cos(getDistanceFromViewportCenter(layoutInfo, itemInfo).toFloat() / itemSize.toPx() * 1.5f)
                            } else {
                                0f
                            }
                        ),
                    text = "${pages[itemIdx]}",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

suspend fun scrollToCenter(listState: LazyListState, layoutInfo: LazyListLayoutInfo) = layoutInfo
    .visibleItemsInfo
    .map { getDistanceFromViewportCenter(layoutInfo, it) }
    .minBy { abs(it) }
    .let { listState.animateScrollBy(-it.toFloat()) }

fun getDistanceFromViewportCenter(layoutInfo: LazyListLayoutInfo, itemInfo: LazyListItemInfo) =
    layoutInfo.viewportSize.width / 2 - getItemCenterOffset(itemInfo)

fun getItemCenterOffset(itemInfo: LazyListItemInfo) = with(itemInfo) { offset + size / 2 }

fun isItemVisible(layoutInfo: LazyListLayoutInfo, idxInVisibleItems: Int) =
    idxInVisibleItems in layoutInfo.visibleItemsInfo.indices

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProblemTimer2Theme {
        TimerScreen()
    }
}