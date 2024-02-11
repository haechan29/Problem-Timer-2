package com.hc.problem_timer_2.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem

@Entity(tableName = "book")
data class BookDto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "problems") val problems: MutableList<Problem>
)

class BookConverter {
    @TypeConverter
    fun toJson(problems: List<Problem>?): String? = Gson().toJson(problems)

    @TypeConverter
    fun toList(json: String): List<Problem>? = Gson().fromJson(json, Array<Problem>::class.java)?.toList()
}

fun BookDto.toVO() = Book(id = id, name = name, problems = problems)
fun Book.toDto() = BookDto(id = id, name = name, problems = problems)