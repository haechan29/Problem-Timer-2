package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hc.problem_timer_2.data_class.BookVO
import com.hc.problem_timer_2.entity.toVO
import com.hc.problem_timer_2.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(private val bookRepository: BookRepository): ViewModel() {
    private val _bookList = MutableLiveData(listOf<BookVO>())
    val bookList: LiveData<List<BookVO>> get() = _bookList

    fun getBookListFromLocalDB() {
        viewModelScope.launch {
            val books = withContext(Dispatchers.IO) {
                bookRepository.getBooks()
            }
            _bookList.value = books
        }
    }

    fun addBook(name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                bookRepository.insert(name = name)
            }
            getBookListFromLocalDB()
        }
    }

    fun updateBook(book: BookVO) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                bookRepository.update(book)
            }
            getBookListFromLocalDB()
        }
    }

    fun deleteBook(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                bookRepository.deleteById(id)
            }
            getBookListFromLocalDB()
        }
    }
}