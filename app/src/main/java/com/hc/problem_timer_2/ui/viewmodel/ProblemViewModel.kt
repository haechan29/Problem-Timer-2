package com.hc.problem_timer_2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProblemViewModel @Inject constructor(private val problemRepository: ProblemRepository): ViewModel() {
    private val _problems = MutableLiveData<List<Problem>>(emptyList())
    val problems: LiveData<List<Problem>> get() = _problems

    private val _problemToEdit = MutableLiveData<Problem?>(null)
    val isEditingProblem = _problemToEdit.map { it != null }

    private val _bookInfoToChangeProblems = MutableLiveData<SelectedBookInfo?>(null)
    val isChangingProblems = _bookInfoToChangeProblems.map { it != null }
    var firstProblemNumberInput = MutableLiveData("")
    var lastProblemNumberInput = MutableLiveData("")
    var canChangeProblemsButton = MediatorLiveData(false).apply {
        addSource(firstProblemNumberInput) {
            value = firstProblemNumberInput.value!!.isNotEmpty() && lastProblemNumberInput.value!!.isNotEmpty()
        }
        addSource(lastProblemNumberInput) {
            value = firstProblemNumberInput.value!!.isNotEmpty() && lastProblemNumberInput.value!!.isNotEmpty()
        }
    }

    fun getProblems() {
        viewModelScope.launch {
            val problems = withContext(Dispatchers.IO) { problemRepository.getAll() }!!
            _problems.value = problems.sortedBy { it.number }
        }
    }

    fun addProblem(problem: Problem) = doIOAndGetProblemsOfSelectedBook {
        if (problem.id != 0L) throw IllegalArgumentException("insert problem with id already set")
        problemRepository.insert(problem)
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

    fun updateProblemNumber(problem: Problem, newNumber: String) = doIOAndGetProblemsOfSelectedBook {
        problemRepository.update(problem.copy(mainNumber = newNumber))
    }

    fun deleteProblem(problem: Problem) = doIOAndGetProblemsOfSelectedBook {
        problemRepository.delete(problem)
    }

    fun deleteProblemsOnBook(bookId: Long) = doIOAndGetProblemsOfSelectedBook {
        val problemsOnBook = problems.value!!.onBook(bookId)
        problemRepository.deleteAll(problemsOnBook)
    }

    fun deleteProblemsOnPage(bookId: Long, page: Int) = doIOAndGetProblemsOfSelectedBook {
        val problemsOnPage = problems.value!!
            .onBook(bookId)
            .onPage(page)
        problemRepository.deleteAll(problemsOnPage)
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

    fun setBookInfoToChangeProblems(bookInfo: SelectedBookInfo) = viewModelScope.launch { _bookInfoToChangeProblems.value = bookInfo }
    fun unsetBookInfoToChangeProblems() = viewModelScope.launch { _bookInfoToChangeProblems.value = null }

    fun addProblemsOnSelectedPage() = withBookInfoToChangeProblemsNotNull { bookInfo ->
        try {
            val firstProblemNumber = firstProblemNumberInput.value!!.toInt()
            val lastProblemNumber = lastProblemNumberInput.value!!.toInt()
            if (lastProblemNumber - firstProblemNumber > PROBLEM_NUMBER_DIFF_LIMIT)
                throw IllegalArgumentException("first and last problem number differ by larger than limit ")
            (firstProblemNumber .. lastProblemNumber).forEach { problemNumber ->
                addProblem(Problem(
                    bookId = bookInfo.selectedBook!!.id,
                    page = bookInfo.selectedPage!!,
                    mainNumber = "$problemNumber"
                ))
            }
        } catch (_: Exception) {}

        firstProblemNumberInput.value = ""
        lastProblemNumberInput.value = ""
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

    private fun withBookInfoToChangeProblemsNotNull(f: (SelectedBookInfo) -> Unit) {
        if (_bookInfoToChangeProblems.value == null) throw UninitializedPropertyAccessException("bookInfoToChangeProblems not initialized")
        f(_bookInfoToChangeProblems.value!!)
        unsetBookInfoToChangeProblems()
    }

    private fun doIOAndGetProblemsOfSelectedBook(f: suspend () -> Unit): Job {
        return viewModelScope.launch {
            withContext(Dispatchers.IO) {
                f()
            }
            getProblems()
        }
    }

    companion object {
        private const val PROBLEM_NUMBER_DIFF_LIMIT = 1000
    }
}