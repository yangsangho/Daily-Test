package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.MILLIS_A_DAY
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.model.MemRepository

class AddViewModel(private val memRepo: MemRepository) : ViewModel() {
    private val logTag = "AddViewModel"

    val title = MutableLiveData<String>()      // 문제 add 및 update의 문제명
    val answer = MutableLiveData<String>()      // 문제 add 및 update의 정답

    fun isPossibleInsert(): Boolean = !title.value.isNullOrEmpty() && !answer.value.isNullOrEmpty()
    fun insertQst() {
        val curTime = System.currentTimeMillis()
        val todayDate = memRepo.getDateStr(curTime)
        val tomorrowDate = memRepo.getDateStr(curTime + MILLIS_A_DAY)
        val qst = Qst(title.value!!, answer.value!!, todayDate, tomorrowDate)
        memRepo.insertQst(qst)
    }
}