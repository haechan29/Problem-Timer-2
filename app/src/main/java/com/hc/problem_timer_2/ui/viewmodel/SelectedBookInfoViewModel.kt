package com.hc.problem_timer_2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hc.problem_timer_2.data.vo.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectedBookInfoViewModel @Inject constructor(): ViewModel() {
    private val _selectedBookInfo = MutableLiveData(SelectedBookInfo())
    val selectedBookInfo: LiveData<SelectedBookInfo> get() = _selectedBookInfo

    fun select(book: Book) { _selectedBookInfo.value = SelectedBookInfo(selectedBook = book, selectedPage = null) }
    fun select(page: Int) { _selectedBookInfo.value = withSelectedBookInfoNotNull { it.copy(selectedPage = page) } }
    fun unselect() { _selectedBookInfo.value = SelectedBookInfo() }

    private fun <R> withSelectedBookInfoNotNull(f: (SelectedBookInfo) -> R): R {
        if (selectedBookInfo.value == null) throw UninitializedPropertyAccessException("bookInfo not initialized")
        return f(selectedBookInfo.value!!)
    }
}

data class SelectedBookInfo(val selectedBook: Book? = null, val selectedPage: Int? = null) {
    fun isBookSelected() = selectedBook != null
    fun isPageSelected() = selectedPage != null
}
