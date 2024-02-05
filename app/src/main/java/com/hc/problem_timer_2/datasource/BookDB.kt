package com.hc.problem_timer_2.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hc.problem_timer_2.Dao.BookDao
import com.hc.problem_timer_2.data_class.Book

@Database(entities = [Book::class], version = 1)
abstract class BookDB : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: BookDB? = null

        fun getDatabase(context: Context): BookDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDB::class.java,
                    "book database"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}