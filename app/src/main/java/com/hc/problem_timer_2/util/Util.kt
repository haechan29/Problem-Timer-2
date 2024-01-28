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

private fun List<String>.addedOneEmptyString() = listOf(listOf(""), this, listOf("")).flatten()
fun List<String>.addedEmptyString(emptyStringNumber: Int): List<String> {
    return if (emptyStringNumber == 1) addedOneEmptyString()
    else listOf(listOf(""), addedEmptyString(emptyStringNumber - 1), listOf("")).flatten()
}

fun Int.toPx() = (this * Resources.getSystem().displayMetrics.density).roundToInt()
fun Int.toDp() = (this / Resources.getSystem().displayMetrics.density).roundToInt()

object FlagController {
    private val flagMap = mutableMapOf<String, Boolean>().apply {
        Flag.entries.forEach { this[it.key] = true }
    }

    fun invokeAndBlock(flag: Flag, duration: Long, f: () -> Unit) {
        val key = flag.key
        flagMap[key] ?: return
        if (flagMap[key]!!) {
            f()
            block(flag, duration)
        }
    }

    fun block(flag: Flag, duration: Long = 0) {
        val key = flag.key
        flagMap[key] ?: return
        if (flagMap[key]!!) {
            flagMap[key] = false
            CoroutineScope(Dispatchers.Default).launch {
                delay(duration)
                flagMap[key] = true
            }
        }
    }

    fun block(flag: Flag, predicate: () -> Boolean) {
        val key = flag.key
        flagMap[key] ?: return
        if (flagMap[key]!!) {
            flagMap[key] = false
            CoroutineScope(Dispatchers.Default).launch {
                var repeatingLimit = 0
                while (!predicate() && repeatingLimit++ < 100) {
                    delay(100)
                }
                flagMap[key] = true
            }
        }
    }
}

enum class Flag(val key: String) {
    SET_PAGE("PAGE")
}

fun Modifier.addFocusCleaner(focusManager: FocusManager) = this.pointerInput(Unit) {
    detectTapGestures(onTap = {
        focusManager.clearFocus()
    })
}