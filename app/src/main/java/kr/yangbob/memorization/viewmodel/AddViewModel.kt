package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.todayDateStr
import kr.yangbob.memorization.tomorrowDateStr

class AddViewModel(private val memRepo: MemRepository) : ViewModel() {
    private val logTag = "AddViewModel"
    private var isPossibleClick = false
    fun resetIsPossibleClick() {
        isPossibleClick = false
    }

    fun checkIsPossibleClick(): Boolean {
        return if (isPossibleClick) false
        else {
            isPossibleClick = true
            true
        }
    }

    val title = MutableLiveData<String>()      // 문제 add 및 update의 문제명
    val answer = MutableLiveData<String>()      // 문제 add 및 update의 정답

    fun isPossibleInsert(): Boolean = !title.value.isNullOrEmpty() && !answer.value.isNullOrEmpty()
    fun insertQst() {
        val qst = Qst(title.value!!, answer.value!!, todayDateStr, tomorrowDateStr)
        memRepo.insertQst(qst)
    }
}