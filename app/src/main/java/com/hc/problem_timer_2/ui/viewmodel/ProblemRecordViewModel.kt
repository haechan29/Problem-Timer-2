package com.hc.problem_timer_2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hc.problem_timer_2.data.repository.ProblemRecordRepository
import com.hc.problem_timer_2.data.vo.ProblemRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProblemRecordViewModel @Inject constructor(private val problemRecordRepository: ProblemRecordRepository): ViewModel() {
    private val _problemRecordListOnSelectedPage = MutableLiveData(listOf<ProblemRecord>())
    val problemRecordListOnSelectedPage: LiveData<List<ProblemRecord>> get() = _problemRecordListOnSelectedPage

    fun getProblemRecords() {
        viewModelScope.launch {
            _problemRecordListOnSelectedPage.value = withContext(Dispatchers.IO) { problemRecordRepository.getAll() }
        }
    }

    fun addProblemRecord(problemRecord: ProblemRecord) = doIOAndGetBookList {
        problemRecordRepository.insert(problemRecord)
    }

    fun removeProblemRecord(problemRecord: ProblemRecord) = doIOAndGetBookList {
        problemRecordRepository.deleteById(problemRecord.id)
    }

    private fun doIOAndGetBookList(f: suspend () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                f()
            }
            getProblemRecords()
        }
    }
}