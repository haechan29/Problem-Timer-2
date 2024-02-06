package com.hc.problem_timer_2.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.hc.problem_timer_2.data_class.BookVO
import com.hc.problem_timer_2.data_class.Problem

class BookConverter {
    @TypeConverter
    fun toJson(problems: List<Problem>?): String? {
        return Gson().toJson(problems)
    }

    @TypeConverter
    fun toList(json: String): List<Problem>? {
        return Gson().fromJson(json, Array<Problem>::class.java)?.toList()
    }
}

fun Book.toVO() = BookVO(id = id, name = name, problems = problems)
fun BookVO.toDto() = Book(name = name, problems = problems)