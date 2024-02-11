package com.hc.problem_timer_2.datasource

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hc.problem_timer_2.dao.BookDao
import com.hc.problem_timer_2.dao.ProblemDao
import com.hc.problem_timer_2.dto.BookDto
import com.hc.problem_timer_2.dto.BookConverter
import com.hc.problem_timer_2.dto.ProblemDto

@Database(entities = [ProblemDto::class], version = 1)
abstract class ProblemDB : RoomDatabase() {
    abstract fun problemDao(): ProblemDao
}