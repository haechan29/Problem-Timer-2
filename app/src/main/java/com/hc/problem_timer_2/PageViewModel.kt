package com.hc.problem_timer_2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PageViewModel: ViewModel() {
    private val _pageIdx = MutableLiveData<Int>(null)
    val pageIdx: LiveData<Int> get() = _pageIdx

    fun setPageIdx(idx: Int) = _pageIdx.postValue(idx)
    fun varyPageIdx(diff: Int) {
        if (!_pageIdx.isInitialized) return
        _pageIdx.postValue(_pageIdx.value!! + diff)
    }
}