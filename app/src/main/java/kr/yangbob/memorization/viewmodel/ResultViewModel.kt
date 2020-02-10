package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.QstRecordWithName
import kr.yangbob.memorization.model.MemRepository
import java.text.DateFormat

class ResultViewModel(private val memRepo: MemRepository) : ViewModel() {

    private var isPossibleClick = false
    fun resetIsPossibleClick(){
        isPossibleClick = false
    }
    fun checkIsPossibleClick(): Boolean{
        return if(isPossibleClick){
            false
        } else {
            isPossibleClick = true
            true
        }
    }

    fun getRecordList(dateStr: String): LiveData<List<QstRecordWithName>> =
        memRepo.getAllRecordWithName(dateStr)

    fun getFormattedDate(dateStr: String): String =
        memRepo.getFormattedDate(dateStr, DateFormat.FULL)
}
