package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hc.problem_timer_2.data_class.BookVO
import com.hc.problem_timer_2.data_class.Problem
import com.hc.problem_timer_2.repository.BookRepository
import com.hc.problem_timer_2.util.updated
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.IndexOutOfBoundsException

@HiltViewModel
class BookInfoViewModel @Inject constructor(private val bookRepository: BookRepository): ViewModel() {
    private val _bookInfo = MutableLiveData<BookInfo>()
    val bookInfo: LiveData<BookInfo> get() = _bookInfo

    fun setBook(bookVO: BookVO) { _bookInfo.value = BookInfo(bookVO = bookVO, currentPage = bookVO.getFirstPage()) }
    fun setCurrentPage(page: Int) { _bookInfo.value = withBookInfoNotNull { it.copy(currentPage = page) } }
    fun isProblemNumberDuplicated(number: String) = withBookInfoNotNull { number in it.getProblemsOnCurrentPage().map { it.number } }

    fun updateProblemNumber(problem: Problem, newNumber: String) {
        _bookInfo.value = withBookInfoNotNull { bookInfo ->
            val problems = bookInfo.getBook().problems
            if (problem !in problems) throw IndexOutOfBoundsException("problem out of problems")
            val index = problems.indexOf(problem)
            val newProblems = problems.updated(index, problem.copy(number = newNumber))
            val newBook = bookInfo.getBook().copy(problems = newProblems)
            bookInfo.copy(bookVO = newBook)
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
