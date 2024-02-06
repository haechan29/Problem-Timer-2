package com.hc.problem_timer_2.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hc.problem_timer_2.entity.Book

@Dao
interface BookDao {
    @Query("SELECT * FROM book")
    suspend fun getBooks(): List<Book>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: Book)

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("DELETE FROM book WHERE id = :id")
    suspend fun deleteById(id: Long)
}