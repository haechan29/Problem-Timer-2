package com.hc.problem_timer_2.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hc.problem_timer_2.dao.BookDao
import com.hc.problem_timer_2.data_class.Problem

@Entity(tableName = "book")
data class Book(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "problems") val problems: List<Problem>
)