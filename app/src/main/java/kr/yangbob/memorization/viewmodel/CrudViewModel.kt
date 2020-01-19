package kr.yangbob.memorization.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.MILLIS_A_DAY
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.model.MemRepository
import org.koin.core.context.GlobalContext.get
import java.util.*

class CrudViewModel(private val memRepo: MemRepository) : ViewModel() {
    val title = MutableLiveData<String>()      // 문제 add 및 update의 문제명
    val answer = MutableLiveData<String>()      // 문제 add 및 update의 정답

    fun insertDataIsEmpty(): Boolean = title.value.isNullOrEmpty() || answer.value.isNullOrEmpty()
    fun insertQst() {
        val cal = get().koin.get<Calendar>()
        val registrationDate = cal.timeInMillis
        val qst = Qst(null, title.value!!, answer.value!!, registrationDate, registrationDate + MILLIS_A_DAY)
        Log.i("TEST", "title = ${qst.title} answer = ${qst.answer}, regi = ${qst.registrationDate}, next = ${qst.nextTestDate}")
        memRepo.insertQst(qst)
    }
}