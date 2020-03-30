package kr.yangbob.memorization.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.SETTING_RESULT_ACTIVITY_SORT_ITEM
import kr.yangbob.memorization.SETTING_RESULT_ACTIVITY_SORT_ORDER
import kr.yangbob.memorization.SortInfo
import kr.yangbob.memorization.data.SimpleDate
import kr.yangbob.memorization.db.QstRecordWithName
import kr.yangbob.memorization.model.MemRepository

class ResultViewModel(private val memRepo: MemRepository, private val settings: SharedPreferences) : ViewModel() {
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
    private var sortInfo: SortInfo

    init {
        val sortItem = settings.getInt(SETTING_RESULT_ACTIVITY_SORT_ITEM, 0)
        val sortOrder = settings.getBoolean(SETTING_RESULT_ACTIVITY_SORT_ORDER, true)
        sortInfo = SortInfo(sortItem, sortOrder)
    }

    fun getSortInfo() = sortInfo
    fun getRecordList(calendarId: SimpleDate): LiveData<List<QstRecordWithName>> = memRepo.getAllRecordWithName(calendarId)

    fun getSortedList(recordList: List<QstRecordWithName>): List<QstRecordWithName> = when (sortInfo.sortedItemIdx) {
        0 -> {
            if (sortInfo.isAscending) recordList.sortedBy { it.challenge_stage }
            else recordList.sortedByDescending { it.challenge_stage }
        }
        1 -> {
            if (sortInfo.isAscending) recordList.sortedBy { it.qst_name }
            else recordList.sortedByDescending { it.qst_name }
        }
        2 -> {
            if (sortInfo.isAscending) recordList.sortedBy { it.is_correct }
            else recordList.sortedByDescending { it.is_correct }
        }
        else -> { recordList }
    }

    fun saveSortInfo(sortInfo: SortInfo){
        this.sortInfo = sortInfo
        val editor = settings.edit()
        editor.putInt(SETTING_RESULT_ACTIVITY_SORT_ITEM, sortInfo.sortedItemIdx)
        editor.putBoolean(SETTING_RESULT_ACTIVITY_SORT_ORDER, sortInfo.isAscending)
        editor.apply()
    }
}
