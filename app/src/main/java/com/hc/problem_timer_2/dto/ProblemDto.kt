package com.hc.problem_timer_2.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hc.problem_timer_2.vo.Problem

@Entity(tableName = "problem")
data class ProblemDto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "book_id") val bookId: Long,
    @ColumnInfo(name = "page") val page: Int,
    @ColumnInfo(name = "number") val number: String
)

fun ProblemDto.toVO() = Problem(id = id, bookId = bookId, page = page, number = number)
fun Problem.toDto() = ProblemDto(id = id, bookId = bookId, page = page, number = number)
