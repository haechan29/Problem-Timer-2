package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hc.problem_timer_2.repository.ProblemRepository
import com.hc.problem_timer_2.vo.Problem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProblemListViewModel @Inject constructor(private val problemRepository: ProblemRepository): ViewModel() {
    private val _problems = MutableLiveData<List<Problem>>(emptyList())
    val problems: LiveData<List<Problem>> get() = _problems

    fun getProblems() {
        viewModelScope.launch {
            _problems.value = withContext(Dispatchers.IO) { problemRepository.getAll() }!!
        }
    }

    fun addProblem(problem: Problem) = doIOAndGetProblemsOfSelectedBook {
        if (problem.id != 0L) throw IllegalArgumentException("insert problem with id already set")
        problemRepository.insert(problem)
    }

    fun updateProblemNumber(problem: Problem, newNumber: String, bookId: Long) = doIOAndGetProblemsOfSelectedBook {
        problemRepository.update(problem.copy(mainNumber = newNumber))
    }

    fun addDefaultProblems(bookId: Long) = doIOAndGetProblemsOfSelectedBook {
        problemRepository.insertAll(getDefaultProblems(bookId = bookId))
    }

    fun isProblemNumberDuplicated(number: String)= withProblemsOfSelectedBookNotNull { problems ->
        number in problems.map { it.number }
    }

    private fun getDefaultProblems(bookId: Long): List<Problem> {
        if (bookId == 0L) throw UninitializedPropertyAccessException("book is not initialized")
        return (1 .. 100).map { page ->
            ((page - 1) * 5 + 1 .. (page - 1) * 5 + 5)
                .map { it.toString() }
                .map { number ->
                    Problem(bookId = bookId, page = page, mainNumber = number)
                }
        }.flatten()
    }

    private fun <R> withProblemsOfSelectedBookNotNull(f: (List<Problem>) -> R): R {
        if (problems.value.isNullOrEmpty()) throw UninitializedPropertyAccessException("problemsOfSelectedBook not initialized")
        return f(problems.value!!)
    }

    private fun doIOAndGetProblemsOfSelectedBook(f: suspend () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                f()
            }
            getProblems()
        }
    }
}