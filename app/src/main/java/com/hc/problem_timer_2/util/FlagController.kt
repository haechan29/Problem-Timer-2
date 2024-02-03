package com.hc.problem_timer_2.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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