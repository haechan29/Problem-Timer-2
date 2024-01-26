package com.hc.problem_timer_2.util

import android.content.res.Resources
import kotlin.math.roundToInt

private fun List<String>.addedOneEmptyString() = listOf(listOf(""), this, listOf("")).flatten()
fun List<String>.addedEmptyString(emptyStringNumber: Int): List<String> {
    return if (emptyStringNumber == 1) addedOneEmptyString()
    else listOf(listOf(""), addedEmptyString(emptyStringNumber - 1), listOf("")).flatten()
}

fun Int.toPx() = (this * Resources.getSystem().displayMetrics.density).roundToInt()
fun Int.toDp() = (this / Resources.getSystem().displayMetrics.density).roundToInt()