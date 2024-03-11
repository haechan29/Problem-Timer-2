package com.hc.problem_timer_2.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.hc.problem_timer_2.data.vo.Book
import com.hc.problem_timer_2.data.vo.Problem
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Entity(tableName = "book")
data class BookDto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "added_at") val addedAt: Instant,
)

class BookConverter {
    @TypeConverter
    fun toJson(problems: List<Problem>?): String? = Gson().toJson(problems)

    @TypeConverter
    fun toList(json: String): List<Problem>? = Gson().fromJson(json, Array<Problem>::class.java)?.toList()

    @TypeConverter
    fun toJson(instant: Instant) = instant.toString()

    @TypeConverter
    fun toInstant(json: String) = Instant.parse(json)
}

fun BookDto.toVO() = Book(id = id, name = name, addedAt = addedAt.toLocalDateTime(TimeZone.currentSystemDefault()))
fun Book.toDto() = BookDto(id = id, name = name, addedAt = addedAt.toInstant(TimeZone.currentSystemDefault()))