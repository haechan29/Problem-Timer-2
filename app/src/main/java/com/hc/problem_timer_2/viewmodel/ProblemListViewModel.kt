package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hc.problem_timer_2.Problem
import com.hc.problem_timer_2.util.added
import com.hc.problem_timer_2.util.removed

class ProblemListViewModel: ViewModel() {
    private val _problems = MutableLiveData(listOf<Problem>())
    val problems: LiveData<List<Problem>> get() = _problems

    fun addProblem(problem: Problem) { _problems.value = _problems.value!!.added(problem) }
    fun removeProblem(problem: Problem) { _problems.value = _problems.value!!.removed(problem) }

    fun getProblemsFromLocalDB() {
        _problems.value = listOf(Problem("1"), Problem("2"), Problem("3"))
    }
}