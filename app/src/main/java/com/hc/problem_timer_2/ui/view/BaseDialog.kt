package com.hc.problem_timer_2.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun BaseDialog(
    hideDialog: () -> Unit,
    text: (@Composable () -> Unit)? = null,
) {
    Dialog(onDismissRequest = hideDialog) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            text?.invoke()
        }
    }
}

@Preview
@Composable
fun BaseDialogPreview() {
    BaseDialog(hideDialog = {})
}
