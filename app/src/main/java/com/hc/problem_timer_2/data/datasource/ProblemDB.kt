package com.hc.problem_timer_2.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hc.problem_timer_2.data.dao.ProblemDao
import com.hc.problem_timer_2.data.dto.ProblemDto

@Database(entities = [ProblemDto::class], version = 2)
abstract class ProblemDB : RoomDatabase() {
    abstract fun problemDao(): ProblemDao
}