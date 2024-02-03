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

fun <E> List<E>.added(e: E) = this.copy().apply { add(e) }
fun <E> List<E>.removed(e: E) = this.copy().apply { remove(e) }
fun <E> List<E>.updated(index: Int, e: E) = this.copy().apply { set(index, e) }
fun <E> List<E>.copy() = mutableListOf<E>().apply { addAll(this@copy) }