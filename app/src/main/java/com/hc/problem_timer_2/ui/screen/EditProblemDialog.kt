package com.hc.problem_timer_2.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hc.problem_timer_2.R
import com.hc.problem_timer_2.ui.view.BottomSheetDialog
import com.hc.problem_timer_2.ui.view.TextWithoutPadding
import com.hc.problem_timer_2.ui.viewmodel.ProblemViewModel
import com.hc.problem_timer_2.ui.viewmodel.SelectedBookInfoViewModel
import com.hc.problem_timer_2.util.notosanskr
import timber.log.Timber

@Composable
fun EditProblemDialog(
    selectedBookInfoViewModel: SelectedBookInfoViewModel = viewModel(),
    problemViewModel: ProblemViewModel = viewModel()
) {
    val selectedBookInfo by selectedBookInfoViewModel.selectedBookInfo.observeAsState()
    val isEditingProblem by problemViewModel.isEditingProblem.observeAsState()

    val items = listOf(
        EditProblemDialogItem(
            ImageVector.vectorResource(id = R.drawable.remove_black_24dp),
            "문제 삭제",
            { problemViewModel.deleteProblemToEdit() }
        ),
        EditProblemDialogItem(
            Icons.Default.Add,
            "꼬리 문제 추가",
            { problemViewModel.addSequentialProblemOfProblemToEdit() }
        ),
        EditProblemDialogItem(
            Icons.Default.Edit,
            "전체 문제 수정",
            {
                problemViewModel.unsetProblemToEdit()
                problemViewModel.setBookInfoToChangeProblems(selectedBookInfo!!)
            }
        )
    )

    BottomSheetDialog(
        title = "문제 수정",
        content = {
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
        },
        isVisible = isEditingProblem ?: false,
        dismiss = { problemViewModel.unsetProblemToEdit() }
    )
}

data class EditProblemDialogItem(val itemIcon: ImageVector, val itemName: String, val onItemClick: () -> Unit)