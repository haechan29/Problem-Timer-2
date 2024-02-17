package com.hc.problem_timer_2.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.hc.problem_timer_2.ui.theme.Primary

@Composable
fun BaseAlertDialog(
    title: String? = null,
    confirmText: String,
    dismissText: String,
    text: @Composable () -> Unit,
    onConfirm: () -> Unit,
    hideDialog: () -> Unit
) {
    AlertDialog(
        title = { if (title != null) Text(text = title, fontSize = 16.sp) },
        text = text,
        containerColor = Color.White,
        onDismissRequest = hideDialog,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    hideDialog()
                }
            ) {
                Text(
                    text = confirmText,
                    fontSize = 14.sp,
                    color = Primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = hideDialog) {
                Text(
                    text = dismissText,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    )
}