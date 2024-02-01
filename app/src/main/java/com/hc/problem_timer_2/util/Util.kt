package com.hc.problem_timer_2.util

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

fun Int.toPx() = (this * Resources.getSystem().displayMetrics.density).roundToInt()
fun Int.toDp() = (this / Resources.getSystem().displayMetrics.density).roundToInt()

fun <T> List<T>.added(t: T) = this.copy().toMutableList().apply { add(t) }
fun <T> List<T>.removed(t: T) = this.copy().toMutableList().apply { remove(t) }
fun <T> List<T>.copy() = mutableListOf<T>().apply { addAll(this@copy) }