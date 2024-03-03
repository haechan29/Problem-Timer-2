package com.hc.problem_timer_2.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.hc.problem_timer_2.data.vo.Book
import com.hc.problem_timer_2.data.vo.Grade
import com.hc.problem_timer_2.data.vo.Problem
import com.hc.problem_timer_2.data.vo.ProblemRecord
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Entity(tableName = "problem_record")
data class ProblemRecordDto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "book_id") val bookId: Long,
    @ColumnInfo(name = "page") val page: Int,
    @ColumnInfo(name = "number") val number: String,
    @ColumnInfo(name = "timeRecord") val timeRecord: Int,
    @ColumnInfo(name = "grade") val grade: Grade,
    @ColumnInfo(name = "solvedAt") val solvedAt: Instant
)

class ProblemRecordConverter {
    @TypeConverter
    fun toJson(instant: Instant) = instant.toString()

    @TypeConverter
    fun toProblemRecord(json: String) = Instant.parse(json)
}

fun ProblemRecordDto.toVO() = ProblemRecord(
    id = id,
    bookId = bookId,
    page = page,
    number = number,
    timeRecord = timeRecord,
    grade = grade,
    solvedAt = solvedAt.toLocalDateTime(TimeZone.currentSystemDefault())
)

fun ProblemRecord.toDto() = ProblemRecordDto(
    id = id,
    bookId = bookId,
    page = page,
    number = number,
    timeRecord = timeRecord,
    grade = grade,
    solvedAt = solvedAt.toInstant(TimeZone.currentSystemDefault())
)