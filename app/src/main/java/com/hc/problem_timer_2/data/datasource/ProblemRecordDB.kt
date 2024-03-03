package com.hc.problem_timer_2.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hc.problem_timer_2.data.dao.ProblemRecordDao
import com.hc.problem_timer_2.data.dto.ProblemRecordConverter
import com.hc.problem_timer_2.data.dto.ProblemRecordDto

@Database(entities = [ProblemRecordDto::class], version = 3)
@TypeConverters(ProblemRecordConverter::class)
abstract class ProblemRecordDB : RoomDatabase() {
    abstract fun problemRecordDao(): ProblemRecordDao
}
