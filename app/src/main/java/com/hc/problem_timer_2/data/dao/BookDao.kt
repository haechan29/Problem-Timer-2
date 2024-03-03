package com.hc.problem_timer_2.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hc.problem_timer_2.data.dto.BookDto

@Dao
interface BookDao {
    @Query("SELECT * FROM book")
    suspend fun getAll(): List<BookDto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bookDto: BookDto)

    @Update
    suspend fun update(bookDto: BookDto)

    @Delete
    suspend fun delete(bookDto: BookDto)

    @Query("DELETE FROM book WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM book")
    suspend fun deleteProblemRecordDB()
}