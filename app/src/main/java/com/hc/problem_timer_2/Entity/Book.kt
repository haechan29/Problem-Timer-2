package com.hc.problem_timer_2.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hc.problem_timer_2.data_class.Problem

@Entity(tableName = "book")
data class Book(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "problems") val problems: List<Problem>
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}