package com.hc.problem_timer_2.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hc.problem_timer_2.dto.ProblemDto

@Dao
interface ProblemDao {
    @Query("SELECT * FROM problem")
    suspend fun getAll(): List<ProblemDto>

    @Query("SELECT * FROM problem WHERE book_id = :bookId")
    suspend fun getByBookId(bookId: Long): List<ProblemDto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(problemDto: ProblemDto)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg problemDtoList: ProblemDto)

    @Update
    suspend fun update(problemDto: ProblemDto)

    @Delete
    suspend fun delete(problemDto: ProblemDto)
}