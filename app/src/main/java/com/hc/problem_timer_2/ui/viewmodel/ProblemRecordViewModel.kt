package com.hc.problem_timer_2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hc.problem_timer_2.data.repository.ProblemRecordRepository
import com.hc.problem_timer_2.data.vo.Grade
import com.hc.problem_timer_2.data.vo.Problem
import com.hc.problem_timer_2.data.vo.ProblemRecord
import com.hc.problem_timer_2.util.getNow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProblemRecordViewModel @Inject constructor(private val problemRecordRepository: ProblemRecordRepository): ViewModel() {
    private val _problemRecordsOnSelectedPage = MutableLiveData(listOf<ProblemRecord>())
    val problemRecordListOnSelectedPage: LiveData<List<ProblemRecord>> get() = _problemRecordsOnSelectedPage

    fun getProblemRecords() {
        viewModelScope.launch {
            _problemRecordsOnSelectedPage.value = withContext(Dispatchers.IO) { problemRecordRepository.getAll() }
        }
    }

    fun addProblemRecord(problem: Problem) = doIOAndGetProblemRecords {
        problemRecordRepository.upsert(
            ProblemRecord(
                bookId = problem.bookId,
                page = problem.page,
                number = problem.number,
                timeRecord = 0,
                grade = Grade.Unranked,
                solvedAt = getNow()
            )
        )
    }

    fun updateProblemRecord(problemRecord: ProblemRecord, timeRecord: Int, grade: Grade) = doIOAndGetProblemRecords {
        if (problemRecord.id == 0L) {
            throw IllegalArgumentException("id cannot be zero. " +
                    "If you want to insert new problem record, use addProblemRecord()")
        }
        problemRecordRepository.upsert(
            ProblemRecord(
                id = problemRecord.id,
                bookId = problemRecord.bookId,
                page = problemRecord.page,
                number = problemRecord.number,
                timeRecord = timeRecord,
                grade = grade,
                solvedAt = getNow()
            )
        )
    }

    fun removeProblemRecord(id: Long) = doIOAndGetProblemRecords {
        problemRecordRepository.deleteById(id)
    }

    private fun doIOAndGetProblemRecords(f: suspend () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                f()
            }
            getProblemRecords()
        }
    }
}