package kr.yangbob.memorization.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.SETTING_ENTIRE_ACTIVITY_SORT_ITEM
import kr.yangbob.memorization.SETTING_ENTIRE_ACTIVITY_SORT_ORDER
import kr.yangbob.memorization.SortInfo
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.model.MemRepository

class EntireViewModel(private val memRepo: MemRepository, private val settings: SharedPreferences) : ViewModel() {
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

    private val qstList: LiveData<List<Qst>> = memRepo.getAllQstLD()
    private var sortInfo: SortInfo

    init {
        val sortItem = settings.getInt(SETTING_ENTIRE_ACTIVITY_SORT_ITEM, 0)
        val sortOrder = settings.getBoolean(SETTING_ENTIRE_ACTIVITY_SORT_ORDER, true)
        sortInfo = SortInfo(sortItem, sortOrder)
    }

    fun getAllQst() = qstList
    fun getSortInfo() = sortInfo

    fun getSortedList(qstList: List<Qst>): List<Qst> = when (sortInfo.sortedItemIdx) {
        0 -> {
            if (sortInfo.isAscending) qstList.sortedBy { it.cur_stage }
            else qstList.sortedByDescending { it.cur_stage }
        }
        1 -> {
            if (sortInfo.isAscending) qstList.sortedBy { it.title }
            else qstList.sortedByDescending { it.title }
        }
        2 -> {
            if (sortInfo.isAscending) qstList.sortedBy { it.registration_date }
            else qstList.sortedByDescending { it.registration_date }
        }
        else -> { qstList }
    }

    fun saveSortInfo(sortInfo: SortInfo){
        this.sortInfo = sortInfo
        val editor = settings.edit()
        editor.putInt(SETTING_ENTIRE_ACTIVITY_SORT_ITEM, sortInfo.sortedItemIdx)
        editor.putBoolean(SETTING_ENTIRE_ACTIVITY_SORT_ORDER, sortInfo.isAscending)
        editor.apply()
    }
}