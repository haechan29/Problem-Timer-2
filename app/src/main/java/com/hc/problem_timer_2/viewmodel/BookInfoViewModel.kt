package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.repository.ProblemRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BookInfoViewModel @Inject constructor(): ViewModel() {
    private val _bookInfo = MutableLiveData(BookInfo())
    val bookInfo: LiveData<BookInfo> get() = _bookInfo

    fun setBook(book: Book) { _bookInfo.value = BookInfo(selectedBook = book, selectedPage = 1) }
    fun setPage(page: Int) { _bookInfo.value = withBookInfoNotNull { it.copy(selectedPage = page) } }

    private fun <R> withBookInfoNotNull(f: (BookInfo) -> R): R {
        if (bookInfo.value == null) throw UninitializedPropertyAccessException("bookInfo not initialized")
        return f(bookInfo.value!!)
    }
}

data class BookInfo(val selectedBook: Book? = null, val selectedPage: Int? = null) {
    fun isBookSelected() = selectedBook != null
}
