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
    @ColumnInfo(name = "number") val mainNumber: String,
    @ColumnInfo(name = "sub_number") val subNumber: String? = null
)

fun ProblemDto.toVO() = Problem(id = id, bookId = bookId, page = page, mainNumber = mainNumber, subNumber = subNumber)
fun Problem.toDto() = ProblemDto(id = id, bookId = bookId, page = page, mainNumber = mainNumber, subNumber = subNumber)
