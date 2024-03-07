package com.hc.problem_timer_2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.hc.problem_timer_2.data.repository.ProblemRepository
import com.hc.problem_timer_2.data.vo.Problem
import com.hc.problem_timer_2.data.vo.onBook
import com.hc.problem_timer_2.data.vo.onPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProblemViewModel @Inject constructor(private val problemRepository: ProblemRepository): ViewModel() {
    private val _problems = MutableLiveData<List<Problem>>(emptyList())
    val problems: LiveData<List<Problem>> get() = _problems

    private val _problemToEdit = MutableLiveData<Problem?>()
    val isEditingProblem = _problemToEdit.map { it != null }

    fun getProblems() {
        viewModelScope.launch {
            _problems.value = withContext(Dispatchers.IO) { problemRepository.getAll() }!!
        }
    }

    fun isProblemNumberDuplicated(problem: Problem, number: String)= withProblemsOfSelectedBookNotNull { problems ->
        val problemsOnnSamePage = problems
            .onBook(problem.bookId)
            .onPage(problem.page)
        number in problemsOnnSamePage.map { it.number }
    }

    fun setProblemToEdit(problem: Problem) = viewModelScope.launch { _problemToEdit.value = problem }
    fun unsetProblemToEdit() = viewModelScope.launch { _problemToEdit.value = null }

    fun addSequentialProblemOfProblemToEdit() = withProblemToEditNotNull { problem ->
        val problemWithoutId = problem.copy(id = 0L)
        val nextSubProblem = problemWithoutId.copy(subNumber =
            if (_problemToEdit.value!!.isMainProblem()) "1"
            else "${_problemToEdit.value!!.subNumber!!.toInt() + 1}"
        )
        addProblem(nextSubProblem)
    }

    fun deleteProblemToEdit() = withProblemToEditNotNull { problem ->
        deleteProblem(problem)
    }

    fun changeProblemsOnSelectedPage(firstProblemNumberInput: String, lastProblemNumberInput: String) = withProblemToEditNotNull { problem ->
        try {
            deleteProblemsOnPage(problem.bookId, problem.page)
            (firstProblemNumberInput.toInt() .. lastProblemNumberInput.toInt()).forEach { problemNumber ->
                addProblem(Problem(
                    bookId = problem.bookId,
                    page = problem.page,
                    mainNumber = "$problemNumber"
                ))
            }
        } catch (_: Exception) {}
    }

    fun addProblem(problem: Problem) = doIOAndGetProblemsOfSelectedBook {
        if (problem.id != 0L) throw IllegalArgumentException("insert problem with id already set")
        problemRepository.insert(problem)
    }

    fun updateProblemNumber(problem: Problem, newNumber: String) = doIOAndGetProblemsOfSelectedBook {
        problemRepository.update(problem.copy(mainNumber = newNumber))
    }

    fun deleteProblem(problem: Problem) = doIOAndGetProblemsOfSelectedBook {
        problemRepository.delete(problem)
    }

    fun addDefaultProblems(bookId: Long) = doIOAndGetProblemsOfSelectedBook {
        problemRepository.insertAll(getDefaultProblems(bookId = bookId))
    }

    private fun getDefaultProblems(bookId: Long): List<Problem> {
        if (bookId == 0L) throw UninitializedPropertyAccessException("book is not initialized")
        val lastPage = getLastProblem(bookId)?.page ?: 0
        val numberOfPages = 100
        val numberOfProblemsPerPage = 5
        return (lastPage + 1 .. lastPage + numberOfPages).map { page ->
            ((page - 1) * numberOfProblemsPerPage + 1 .. page * numberOfProblemsPerPage)
                .map { it.toString() }
                .map { number ->
                    Problem(bookId = bookId, page = page, mainNumber = number)
                }
        }.flatten()
    }

    private fun getLastProblem(bookId: Long) = problems.value!!
        .filter { it.bookId == bookId }
        .maxOfOrNull { it }

    fun deleteProblemsOnBook(bookId: Long) = doIOAndGetProblemsOfSelectedBook {
        val problemsOnBook = problems.value!!.onBook(bookId)
        problemRepository.deleteAll(problemsOnBook)
    }

    private fun deleteProblemsOnPage(bookId: Long, page: Int) = doIOAndGetProblemsOfSelectedBook {
        val problemsOnPage = problems.value!!
            .onBook(bookId)
            .onPage(page)
        problemRepository.deleteAll(problemsOnPage)
    }

    private fun <R> withProblemsOfSelectedBookNotNull(f: (List<Problem>) -> R): R {
        if (problems.value.isNullOrEmpty()) throw UninitializedPropertyAccessException("problemsOfSelectedBook not initialized")
        return f(problems.value!!)
    }

    private fun withProblemToEditNotNull(f: (Problem) -> Unit) {
        if (_problemToEdit.value == null) throw UninitializedPropertyAccessException("problemToEdit not initialized")
        f(_problemToEdit.value!!)
        unsetProblemToEdit()
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