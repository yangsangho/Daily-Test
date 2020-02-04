package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.QstRecordWithName
import kr.yangbob.memorization.model.MemRepository
import java.text.DateFormat

class ResultViewModel(private val memRepo: MemRepository) : ViewModel(){

    fun getRecordList(dateStr: String): LiveData<List<QstRecordWithName>> = memRepo.getAllRecordWithName(dateStr)
    fun getFormattedDate(dateStr: String): String {
        val time = memRepo.getDateLong(dateStr)
        val formatter = DateFormat.getDateInstance(DateFormat.DEFAULT)
//        formatter.timeZone = 나중에 추가가 필요할 수도
        return formatter.format(time)
    }
}
