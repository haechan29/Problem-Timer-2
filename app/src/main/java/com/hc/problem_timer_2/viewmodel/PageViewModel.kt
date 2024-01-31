package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PageViewModel: ViewModel() {
    private val _page = MutableLiveData<Int>(null)
    val page: LiveData<Int> get() = _page

    fun setPage(value: Int) = _page.postValue(value)
}