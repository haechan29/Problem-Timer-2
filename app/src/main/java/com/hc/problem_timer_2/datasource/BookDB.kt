package com.hc.problem_timer_2.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hc.problem_timer_2.dao.BookDao
import com.hc.problem_timer_2.entity.Book
import com.hc.problem_timer_2.entity.BookConverter

@Database(entities = [Book::class], version = 1)
@TypeConverters(BookConverter::class)
abstract class BookDB : RoomDatabase() {
    abstract fun bookDao(): BookDao
}