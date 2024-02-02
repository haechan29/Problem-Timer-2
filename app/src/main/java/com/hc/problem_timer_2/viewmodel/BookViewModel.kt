package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hc.problem_timer_2.data_class.Book
import com.hc.problem_timer_2.data_class.Problem
import com.hc.problem_timer_2.util.added
import com.hc.problem_timer_2.util.removed

class BookViewModel: ViewModel() {
    // book
    private val _book = MutableLiveData<Book>()
    val book: LiveData<Book> get() = _book

    fun setBook(book: Book) {
        _book.value = book
        _page.value = book.getFirstPage()
    }

    // page
    private val _page = MutableLiveData<Int>()
    val page: LiveData<Int> get() = _page

    fun setPage(value: Int) = _page.postValue(value)

    // problems
    private val _problems = MutableLiveData<List<Problem>>()
    val problems: LiveData<List<Problem>> get() = _problems

    fun setProblems(problems: List<Problem>) { _problems.value = problems }
    fun addProblem(problem: Problem) { _problems.value = _problems.value!!.added(problem) }
    fun removeProblem(problem: Problem) { _problems.value = _problems.value!!.removed(problem) }

    fun getProblemsOnCurrentPage(): List<Problem> {
        if (book.value == null) throw UninitializedPropertyAccessException("book not initialized")
        return book.value!!.problems.filter { problem -> problem.page == page.value }
    }
}