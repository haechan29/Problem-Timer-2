package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(private val bookRepository: BookRepository): ViewModel() {
    private val _bookList = MutableLiveData(listOf<Book>())
    val bookList: LiveData<List<Book>> get() = _bookList

    fun getBookListFromLocalDB() {
        viewModelScope.launch {
            val books = withContext(Dispatchers.IO) {
                bookRepository.getAll()
            }
            _bookList.value = books
        }
    }

    fun addBook(book: Book) = doIOAndGetBookListFromLocalDB {
        bookRepository.insert(book)
    }

    fun updateBook(book: Book) = doIOAndGetBookListFromLocalDB {
        bookRepository.update(book)
    }

    fun deleteBook(id: Long) = doIOAndGetBookListFromLocalDB {
        bookRepository.deleteById(id)
    }
    
    private fun doIOAndGetBookListFromLocalDB(f: suspend () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                f()
            }
            getBookListFromLocalDB()
        }
    }
}