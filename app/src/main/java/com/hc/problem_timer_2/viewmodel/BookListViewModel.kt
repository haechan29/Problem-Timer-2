package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hc.problem_timer_2.data_class.BookVO
import com.hc.problem_timer_2.util.added
import com.hc.problem_timer_2.util.removed

class BookListViewModel: ViewModel() {
    private val _bookList = MutableLiveData(listOf<BookVO>())
    val bookList: LiveData<List<BookVO>> get() = _bookList

    fun addBook(bookVO: BookVO) { _bookList.value = _bookList.value!!.added(bookVO) }
    fun removeBook(bookVO: BookVO) { _bookList.value = _bookList.value!!.removed(bookVO) }

//    fun getBookListFromLocalDB() {
//        _bookList.value = listOf(BookVO("책1"), BookVO("책2"))
//    }

    fun setBookList(books: List<BookVO>) {
        _bookList.value = books
    }
}