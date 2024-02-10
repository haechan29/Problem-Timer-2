package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hc.problem_timer_2.repository.ProblemRecordRepository
import com.hc.problem_timer_2.vo.ProblemRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProblemRecordListViewModel @Inject constructor(private val problemRecordRepository: ProblemRecordRepository): ViewModel() {
    private val _problemRecordListOnSelectedPage = MutableLiveData(listOf<ProblemRecord>())
    val problemRecordListOnSelectedPage: LiveData<List<ProblemRecord>> get() = _problemRecordListOnSelectedPage

    fun getProblemRecords(bookId: Long, page: Int) {
        viewModelScope.launch {
            _problemRecordListOnSelectedPage.value =
                withContext(Dispatchers.IO) {
                    problemRecordRepository
                        .getByBookId(bookId)
                        .filter { it.page == page }
                }
        }
    }

    fun addProblemRecord(problemRecord: ProblemRecord) = doIOAndGetBookList(problemRecord) {
        problemRecordRepository.insert(problemRecord)
    }

    fun removeProblemRecord(problemRecord: ProblemRecord) = doIOAndGetBookList(problemRecord) {
        problemRecordRepository.deleteById(problemRecord.id)
    }

    private fun doIOAndGetBookList(problemRecord: ProblemRecord, f: suspend () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                f()
            }
            getProblemRecords(problemRecord.bookId, problemRecord.page)
        }
    }
}