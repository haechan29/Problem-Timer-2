package com.hc.problem_timer_2.vo

data class Book(
    val id: Long = 0L,
    val name: String,
    val problems: MutableList<Problem> = mutableListOf()
) {
    fun getFirstPage() = problems[0].page
    fun getPages() = problems.map { problem -> problem.page }.distinct()
    fun addDefaultProblems() {
        problems.addAll(getDefaultProblems(id))
    }
}

fun getDefaultProblems(bookId: Long): List<Problem> {
    if (bookId == 0L) throw UninitializedPropertyAccessException("book is not initialized")
    return (1 .. 100).map { page ->
        ((page - 1) * 5 + 1 .. (page - 1) * 5 + 5)
            .map { it.toString() }
            .map { number ->
                Problem(bookId = bookId, page = page, number = number)
            }
        }.flatten()
}