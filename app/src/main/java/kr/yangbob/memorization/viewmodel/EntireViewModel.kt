package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.model.MemRepository
import java.text.DateFormat

class EntireViewModel(private val memRepo: MemRepository) : ViewModel(){
    fun getAllQst(): LiveData<List<Qst>> = memRepo.getAllQstLD()
    fun getFormattedDate(dateStr: String): String {
        val time = memRepo.getDateLong(dateStr)
        val formatter = DateFormat.getDateInstance(DateFormat.DEFAULT)
//        formatter.timeZone = 나중에 추가가 필요할 수도
        return formatter.format(time)
    }
}