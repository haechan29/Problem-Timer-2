package com.hc.problem_timer_2.datasource

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hc.problem_timer_2.dao.BookDao
import com.hc.problem_timer_2.dto.BookDto
import com.hc.problem_timer_2.dto.BookConverter

@Database(entities = [BookDto::class], version = 1)
@TypeConverters(BookConverter::class)
abstract class BookDB : RoomDatabase() {
    abstract fun bookDao(): BookDao
}