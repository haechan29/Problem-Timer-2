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
    fun getBooks(): List<Book>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(book: Book)

    @Update
    fun update(book: Book)

    @Delete
    fun delete(book: Book)
}