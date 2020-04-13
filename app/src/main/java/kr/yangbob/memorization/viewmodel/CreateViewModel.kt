package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.MutableLiveData
import kr.yangbob.memorization.SETTING_IS_FIRST_CREATE
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.todayDate
import kr.yangbob.memorization.tomorrowDate

class CreateViewModel(private val memRepo: MemRepository) : BaseViewModel() {
    private val logTag = "AddViewModel"

    val title = MutableLiveData<String>()      // 문제 add 및 update의 문제명
    val answer = MutableLiveData<String>()      // 문제 add 및 update의 정답

    fun isPossibleInsert(): Boolean = !title.value.isNullOrEmpty() && !answer.value.isNullOrEmpty()
    fun insertQst(): Boolean {
        return if (memRepo.chkDuplication(title.value!!)) false
        else {
            val qst = Qst(title.value!!, answer.value!!, todayDate, tomorrowDate)
            memRepo.insertQst(qst)
            true
        }
    }

    fun isFirst(): Boolean = if(memRepo.getIsFirst(SETTING_IS_FIRST_CREATE)){
        memRepo.setFirstValueFalse(SETTING_IS_FIRST_CREATE)
        true
    } else false
}