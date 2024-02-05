package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hc.problem_timer_2.data_class.BookVO
import com.hc.problem_timer_2.data_class.Problem
import com.hc.problem_timer_2.util.updated
import kotlin.IndexOutOfBoundsException

class BookInfoViewModel: ViewModel() {
    private val _bookInfo = MutableLiveData<BookInfo>()
    val bookInfo: LiveData<BookInfo> get() = _bookInfo

    fun setBookInfo(bookInfo: BookInfo) { _bookInfo.value = bookInfo }
    fun setBook(bookVO: BookVO) { _bookInfo.value = BookInfo(bookVO = bookVO, currentPage = bookVO.getFirstPage()) }
    fun setCurrentPage(page: Int) { _bookInfo.value = withBookInfoNotNull { it.copy(currentPage = page) } }
    fun isProblemNumberDuplicated(number: String) = withBookInfoNotNull { number in it.getProblemsOnCurrentPage().map { it.number } }

    fun updateProblemNumber(problem: Problem, newNumber: String) {
        _bookInfo.value = withBookInfoNotNull {
            val problems = it.getBook().problems
            if (problem !in problems) throw IndexOutOfBoundsException("problem out of problems")
            val index = problems.indexOf(problem)
            val newProblems = problems.updated(index, problem.copy(number = newNumber))
            val newBook = it.getBook().copy(problems = newProblems)
            it.copy(bookVO = newBook)
        }
    }

    private fun <R> withBookInfoNotNull(f: (BookInfo) -> R): R {
        if (bookInfo.value == null) throw UninitializedPropertyAccessException("bookInfo not initialized")
        return f(bookInfo.value!!)
    }
}

data class BookInfo(private val bookVO: BookVO, private val currentPage: Int) {
    fun getBook() = bookVO
    fun getCurrentPage() = currentPage
    fun getProblemsOnCurrentPage() = bookVO.problems.filter { problem -> problem.page == currentPage }
    fun getPages() = bookVO.problems.map { problem -> problem.page }.distinct()
}
