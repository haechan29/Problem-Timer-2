package com.hc.problem_timer_2.data.vo

import androidx.compose.ui.graphics.Color

enum class Grade(val color: Color, val textShort: String, val textLong: String) {
    Correct     (Color(0xB30035F0), "맞았어요", "맞았어요"),
    Wrong       (Color(0xB3000000), "틀렸어요", "틀렸어요"),
//    Ambiguous   (Color(0xfff9e586)),
    Unranked    (Color(0x4C000000), "-", "클릭하여 채점하기");

    fun next(): Grade {
        val index = entries.indexOf(this)
        return entries[((index + 1) % entries.size ) % (entries.size - 1)]
    }
}