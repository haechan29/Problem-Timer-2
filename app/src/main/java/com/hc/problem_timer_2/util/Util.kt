package com.hc.problem_timer_2.util

import android.content.Context
import android.content.res.Resources
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlin.math.roundToInt

@Composable
fun customComposableToast(text: String, context: Context = LocalContext.current) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
fun customToast(text: String, context: Context) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

fun Int.toPx() = (this * Resources.getSystem().displayMetrics.density).roundToInt()
fun Int.toDp() = (this / Resources.getSystem().displayMetrics.density).roundToInt()

fun <T> List<T>.added(t: T) = this.copy().toMutableList().apply { add(t) }
fun <T> List<T>.removed(t: T) = this.copy().toMutableList().apply { remove(t) }
fun <T> List<T>.copy() = mutableListOf<T>().apply { addAll(this@copy) }