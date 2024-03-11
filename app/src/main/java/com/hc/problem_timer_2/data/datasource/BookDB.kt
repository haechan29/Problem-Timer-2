package com.hc.problem_timer_2.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hc.problem_timer_2.data.dao.BookDao
import com.hc.problem_timer_2.data.dto.BookConverter
import com.hc.problem_timer_2.data.dto.BookDto

@Database(entities = [BookDto::class], version = 3)
@TypeConverters(BookConverter::class)
abstract class BookDB : RoomDatabase() {
    abstract fun bookDao(): BookDao
}