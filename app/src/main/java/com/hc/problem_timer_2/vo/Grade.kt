package com.hc.problem_timer_2.vo

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.hc.problem_timer_2.R

enum class Grade(val color: Color, val text: String) {
    Correct     (Color(0xFF5275F1), "맞았어요"),
    Wrong       (Color(0xB3000000), "틀렸어요"),
//    Ambiguous   (Color(0xfff9e586)),
    Unranked    (Color(0x4C000000), "");

    fun next(): Grade {
        val index = entries.indexOf(this)
        return entries[(index + 1) % entries.size]
    }
}