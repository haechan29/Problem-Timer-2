package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hc.problem_timer_2.data_class.Book
import com.hc.problem_timer_2.util.added
import com.hc.problem_timer_2.util.removed

class BookListViewModel: ViewModel() {
    private val _bookList = MutableLiveData(listOf<Book>())
    val bookList: LiveData<List<Book>> get() = _bookList

    fun addBook(book: Book) { _bookList.value = _bookList.value!!.added(book) }
    fun removeBook(book: Book) { _bookList.value = _bookList.value!!.removed(book) }

    fun getBookListFromLocalDB() {
        _bookList.value = listOf(Book("책1"), Book("책2"))
    }
}