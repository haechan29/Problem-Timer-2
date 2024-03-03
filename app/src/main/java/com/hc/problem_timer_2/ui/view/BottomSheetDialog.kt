package com.hc.problem_timer_2.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hc.problem_timer_2.R
import com.hc.problem_timer_2.util.notosanskr

@Composable
fun BottomSheetDialog(
    title: String,
    items: List<BottomSheetDialogItem>,
    isVisible: Boolean,
    dismiss: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(id = R.color.black_400))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { dismiss() }
            )
        }
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                    .padding(vertical = 12.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Divider(
                    modifier = Modifier
                        .width(48.dp)
                        .height(2.dp)
                        .background(color = colorResource(id = R.color.black_400))
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextWithoutPadding(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 16.dp),
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = notosanskr
                )
                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .clickable { item.onItemClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = item.itemIcon,
                            contentDescription = null,
                            tint = colorResource(id = R.color.black_400)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        TextWithoutPadding(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            text = item.itemName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = notosanskr
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetPreview() {
    BottomSheetDialog(
        title = "문제 수정",
        items = listOf(
            BottomSheetDialogItem(Icons.Default.Add, "꼬리 문제 추가", {}),
            BottomSheetDialogItem(Icons.Default.Edit, "전체 문제 수정", {})
        ),
        true,
        {}
    )
}

data class BottomSheetDialogItem(val itemIcon: ImageVector, val itemName: String, val onItemClick: () -> Unit)
