package com.hc.problem_timer_2.dto

import androidx.room.TypeConverter
import com.hc.problem_timer_2.vo.ProblemRecord
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class ProblemRecordConverter {
    @TypeConverter
    fun toJson(instant: Instant?): String {
        return instant.toString()
    }

    @TypeConverter
    fun toProblemRecord(json: String): Instant {
        return Instant.parse(json)
    }
}

fun ProblemRecordDto.toVO() = ProblemRecord(
    id = id,
    bookId = bookId,
    timeRecord = timeRecord,
    grade = grade,
    solvedAt = solvedAt.toLocalDateTime(TimeZone.currentSystemDefault())
)

fun ProblemRecord.toDto() = ProblemRecordDto(
    id = id,
    bookId = bookId,
    timeRecord = timeRecord,
    grade = grade,
    solvedAt = solvedAt.toInstant(TimeZone.currentSystemDefault())
)