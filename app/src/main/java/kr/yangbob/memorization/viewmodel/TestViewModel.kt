package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.model.MemRepository

class TestViewModel(private val memRepo: MemRepository) : ViewModel() {
    private val testList: List<Qst> = memRepo.getNeedTestList()

    fun getTestList() = testList
}