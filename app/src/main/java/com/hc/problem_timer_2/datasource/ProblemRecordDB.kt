package com.hc.problem_timer_2.datasource

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hc.problem_timer_2.dao.ProblemRecordDao
import com.hc.problem_timer_2.dto.BookConverter
import com.hc.problem_timer_2.dto.ProblemRecordConverter
import com.hc.problem_timer_2.dto.ProblemRecordDto

@Database(entities = [ProblemRecordDto::class], version = 2)
@TypeConverters(ProblemRecordConverter::class)
abstract class ProblemRecordDB : RoomDatabase() {
    abstract fun problemRecordDao(): ProblemRecordDao
}
