package com.hc.problem_timer_2.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hc.problem_timer_2.vo.Problem

@Entity(tableName = "book")
data class BookDto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "problems") val problems: MutableList<Problem>
)