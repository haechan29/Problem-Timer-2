package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hc.problem_timer_2.data_class.Book
import com.hc.problem_timer_2.data_class.Problem
import com.hc.problem_timer_2.util.added
import com.hc.problem_timer_2.util.removed
import com.hc.problem_timer_2.util.updated
import java.lang.IndexOutOfBoundsException

class BookViewModel: ViewModel() {
    // book
    private val _book = MutableLiveData<Book>()
    val book: LiveData<Book> get() = _book

    fun setBook(book: Book) {
        _book.value = book
        _page.value = book.getFirstPage()
        _problems.value = book.problems
    }

    // page
    private val _page = MutableLiveData<Int>()
    val page: LiveData<Int> get() = _page

    fun setPage(page: Int) {
        _page.value = page
        updateProblemsOnCurrentPage()
    }

    // problems
    private var problemsOrigin: List<Problem>?
        get() = book.value?.problems
        set(value) { if (value != null) _book.value!!.problems = value }
    private val _problems: MutableLiveData<List<Problem>> get() = MutableLiveData(problemsOrigin)
    val problems: LiveData<List<Problem>> get() = _problems

    fun addProblem(problem: Problem) { problemsOrigin = problems.value!!.added(problem) }
    fun removeProblem(problem: Problem) { problemsOrigin = problems.value!!.removed(problem) }

    fun update(problem: Problem, newNumber: String) {
        if (problem !in problemsOrigin!!) throw IndexOutOfBoundsException("problem index out of problems")
        val index = problemsOrigin!!.indexOf(problem)
        problemsOrigin = problemsOrigin!!.updated(index, problem.copy(number = newNumber))
        updateProblemsOnCurrentPage()
    }

    private val problemsOnCurrentPageOrigin: List<Problem>?
        get() = problemsOrigin?.filter { problem -> problem.page == page.value }

    private val _problemsOnCurrentPage: MutableLiveData<List<Problem>> = MutableLiveData()
    val problemsOnCurrentPage: LiveData<List<Problem>> get() = _problemsOnCurrentPage

    private fun updateProblemsOnCurrentPage() {
        _problemsOnCurrentPage.value = problemsOnCurrentPageOrigin
    }
}
