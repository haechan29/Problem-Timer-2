package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hc.problem_timer_2.vo.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectedBookInfoViewModel @Inject constructor(): ViewModel() {
    private val _selectedSelectedBookInfo = MutableLiveData(SelectedBookInfo())
    val selectedBookInfo: LiveData<SelectedBookInfo> get() = _selectedSelectedBookInfo

    fun select(book: Book) { _selectedSelectedBookInfo.value = SelectedBookInfo(selectedBook = book) }
    fun select(page: Int) { _selectedSelectedBookInfo.value = withSelectedBookInfoNotNull { it.copy(selectedPage = page) } }
    fun unselect() { _selectedSelectedBookInfo.value = SelectedBookInfo() }

    private fun <R> withSelectedBookInfoNotNull(f: (SelectedBookInfo) -> R): R {
        if (selectedBookInfo.value == null) throw UninitializedPropertyAccessException("bookInfo not initialized")
        return f(selectedBookInfo.value!!)
    }
}

data class SelectedBookInfo(val selectedBook: Book? = null, val selectedPage: Int? = null) {
    fun isBookSelected() = selectedBook != null
    fun isPageSelected() = selectedPage != null
}
