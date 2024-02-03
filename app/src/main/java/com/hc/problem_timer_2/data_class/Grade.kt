package com.hc.problem_timer_2.data_class

import androidx.compose.ui.graphics.Color

sealed class Grade(val color: Color, val text: String) {
    fun next() = when (this) {
        is Correct -> Wrong
        is Wrong -> Ambiguous
        is Ambiguous -> Correct
        is Unranked -> Correct
    }
}

data object Correct     : Grade(Color(0xff89e7f8), "⭕")
data object Wrong       : Grade(Color(0xfff9d9df), "❌")
data object Ambiguous   : Grade(Color(0xfff9e586), "❓")
data object Unranked    : Grade(Color.White, "")