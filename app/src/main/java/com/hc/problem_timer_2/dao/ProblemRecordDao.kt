package com.hc.problem_timer_2.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hc.problem_timer_2.dto.ProblemRecordDto

@Dao
interface ProblemRecordDao {
    @Query("SELECT * FROM problem_record")
    suspend fun getAll(): List<ProblemRecordDto>

    @Query("SELECT * FROM problem_record WHERE book_id = :bookId")
    suspend fun getByBookId(bookId: Long): List<ProblemRecordDto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(problemRecordDto: ProblemRecordDto)

    @Update
    suspend fun update(problemRecordDto: ProblemRecordDto)

    @Delete
    suspend fun delete(problemRecordDto: ProblemRecordDto)

    @Query("DELETE FROM problem_record WHERE id = :id")
    suspend fun deleteById(id: Long)
}