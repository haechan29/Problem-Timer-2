package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hc.problem_timer_2.util.added
import com.hc.problem_timer_2.util.removed

class BookListViewModel: ViewModel() {
    private val _bookList = MutableLiveData(listOf<String>())
    val bookList: LiveData<List<String>> get() = _bookList

    fun addBook(book: String) { _bookList.value = _bookList.value!!.added(book) }
    fun removeBook(book: String) { _bookList.value = _bookList.value!!.removed(book) }

    fun getBookListFromLocalDB() {
        _bookList.value = listOf("책1", "책2", "책3")
    }
}