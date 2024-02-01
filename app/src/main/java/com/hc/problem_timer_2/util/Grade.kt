package com.hc.problem_timer_2.util

sealed class Grade
data object CORRECT : Grade()
data object WRONG : Grade()
data object AMBIGUOUS : Grade()