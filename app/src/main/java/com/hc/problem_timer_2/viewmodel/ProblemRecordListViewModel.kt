package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hc.problem_timer_2.repository.ProblemRecordRepository
import com.hc.problem_timer_2.vo.ProblemRecord
import kotlinx.datetime.LocalDateTime
import com.hc.problem_timer_2.util.added
import com.hc.problem_timer_2.util.getNow
import com.hc.problem_timer_2.util.removed
import com.hc.problem_timer_2.vo.Grade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProblemRecordListViewModel @Inject constructor(private val problemRecordRepository: ProblemRecordRepository): ViewModel() {
    private val _problemRecordList = MutableLiveData(listOf<ProblemRecord>())
    val problemRecordList: LiveData<List<ProblemRecord>> get() = _problemRecordList

    fun getProblemRecordsFromLocalDB() {
        viewModelScope.launch {
            val problemRecords = withContext(Dispatchers.IO) {
                problemRecordRepository.getAll()
            }
            _problemRecordList.value = problemRecords
        }
    }

    fun addProblemRecord(problemRecord: ProblemRecord) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                problemRecordRepository.insert(problemRecord)
            }
            getProblemRecordsFromLocalDB()
        }
    }

    fun removeProblemRecord(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                problemRecordRepository.deleteById(id)
            }
            getProblemRecordsFromLocalDB()
        }
    }
}