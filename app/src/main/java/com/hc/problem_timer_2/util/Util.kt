package com.hc.problem_timer_2.util

import android.content.Context
import android.content.res.Resources
import android.widget.Toast
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

fun Int.toPx() = (this * Resources.getSystem().displayMetrics.density).roundToInt()
fun Int.toDp() = (this / Resources.getSystem().displayMetrics.density).roundToInt()

fun <E> List<E>.added(e: E) = this.copy().apply { add(e) }
fun <E> List<E>.removed(e: E) = this.copy().apply { remove(e) }
fun <E> List<E>.updated(index: Int, e: E) = this.copy().apply { set(index, e) }
fun <E> List<E>.copy() = mutableListOf<E>().apply { addAll(this@copy) }

fun getNow() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())