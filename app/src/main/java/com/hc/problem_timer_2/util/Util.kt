package com.hc.problem_timer_2.util

import android.content.res.Resources
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

fun <R> Any.tryOrNull(f: () -> R): R? {
    return try {
        f()
    } catch (e: Exception) {
        null
    }
}

object JamoUtil {
    val CHOSUNG = listOf(
        "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ",
        "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ",
    )
    val JUNGSUNG = listOf(
        "ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅗㅏ",
        "ㅗㅐ", "ㅗㅣ", "ㅛ", "ㅜ", "ㅜㅓ", "ㅜㅔ", "ㅜㅣ", "ㅠ", "ㅡ", "ㅡㅣ", "ㅣ",
    )
    val JONGSUNG = listOf(
        "", "ㄱ", "ㄲ", "ㄱㅅ", "ㄴ", "ㄴㅈ", "ㄴㅎ", "ㄷ",
        "ㄹ", "ㄹㄱ", "ㄹㅁ", "ㄹㅂ", "ㄹㅅ", "ㄹㅌ", "ㄹㅍ", "ㄹㅎ", "ㅁ", "ㅂ", "ㅂㅅ", "ㅅ", "ㅆ", "ㅇ",
        "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ",
    )

    fun toJamoeum(eumjeols: String): String {
        return eumjeols
            .map(JamoUtil::toJamoeum)
            .joinToString("")
    }

    fun toJamoeum(eumjeol: Char): String {
        return if (eumjeol.code in 0xAC00..0xD79D) {
            val startValue = (eumjeol - 0xAC00).code
            val jong = startValue % 28
            val jung = (startValue - jong) / 28 % 21
            val cho = ((startValue - jong) / 28 - jung) / 21
            mutableListOf<String>().apply {
                add(CHOSUNG[cho])
                add(JUNGSUNG[jung])
                add(JONGSUNG[jong])
            }
        } else {
            listOf(eumjeol.toString())
        }.joinToString("")
    }
}