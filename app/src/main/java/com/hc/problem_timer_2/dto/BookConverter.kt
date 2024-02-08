package com.hc.problem_timer_2.dto

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem

class BookConverter {
    @TypeConverter
    fun toJson(problems: List<Problem>?): String? = Gson().toJson(problems)

    @TypeConverter
    fun toList(json: String): List<Problem>? = Gson().fromJson(json, Array<Problem>::class.java)?.toList()
}

fun BookDto.toVO() = Book(id = id, name = name, problems = problems)
fun Book.toDto() = BookDto(id = id, name = name, problems = problems)