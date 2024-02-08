package com.hc.problem_timer_2.vo

import androidx.compose.ui.graphics.Color

enum class Grade(val color: Color, val text: String) {
    Correct     (Color(0xff89e7f8), "⭕"),
    Wrong       (Color(0xfff9d9df), "❌"),
    Ambiguous   (Color(0xfff9e586), "❓"),
    Unranked    (Color.White,       ""  );

    fun next(): Grade {
        val index = entries.indexOf(this)
        return entries[(index + 1) % entries.size]
    }
}