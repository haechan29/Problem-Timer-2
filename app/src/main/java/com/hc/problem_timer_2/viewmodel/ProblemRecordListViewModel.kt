package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hc.problem_timer_2.data_class.ProblemRecord
import com.hc.problem_timer_2.data_class.Correct
import com.hc.problem_timer_2.data_class.Wrong
import com.hc.problem_timer_2.util.added
import com.hc.problem_timer_2.util.removed
import java.time.LocalDateTime

class ProblemRecordListViewModel: ViewModel() {
    private val _problemRecordList = MutableLiveData(listOf<ProblemRecord>())
    val problemRecordList: LiveData<List<ProblemRecord>> get() = _problemRecordList

    fun addProblemRecord(problemRecord: ProblemRecord) {
        _problemRecordList.value = _problemRecordList.value!!.added(problemRecord)
    }
    fun removeProblemRecord(problemRecord: ProblemRecord) {
        _problemRecordList.value = _problemRecordList.value!!.removed(problemRecord)
    }

    fun getProblemRecordsFromLocalDB() {
        _problemRecordList.value = listOf(
            ProblemRecord("1", 50_000, Wrong, LocalDateTime.now().plusDays(-2)),
            ProblemRecord("1", 45_000, Correct, LocalDateTime.now().plusDays(-1))
        )
    }
}