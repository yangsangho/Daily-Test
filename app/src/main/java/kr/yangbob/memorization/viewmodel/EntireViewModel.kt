package kr.yangbob.memorization.viewmodel

import android.app.Application
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kr.yangbob.memorization.IconSetting
import kr.yangbob.memorization.R
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.model.MemRepository
import java.text.DateFormat

class EntireViewModel(application: Application, private val memRepo: MemRepository) :
    AndroidViewModel(application) {
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

    fun getAllQst() = qstList
    fun getFormattedDate(dateStr: String): String =
        memRepo.getFormattedDate(dateStr, DateFormat.DEFAULT)

    val sortByBasis1: String
    val sortByBasis2: String
    val sortByBasis3: String

    init {
        val resource = getApplication<Application>().resources
        sortByBasis1 = resource.getString(R.string.entire_alert_sort1)
        sortByBasis2 = resource.getString(R.string.entire_alert_sort2)
        sortByBasis3 = resource.getString(R.string.entire_alert_sort3)
    }

    private val sortObserver = MutableLiveData<Boolean>(false)
    fun getObserverForSort(): LiveData<Boolean> = sortObserver

    var isAscending = true
    var sortedItemIdx = 0

    val sortIcon1 = MutableLiveData<IconSetting>(IconSetting.UP)
    val sortIcon2 = MutableLiveData<IconSetting>(IconSetting.NONE)
    val sortIcon3 = MutableLiveData<IconSetting>(IconSetting.NONE)
    private val sortIconList = listOf(sortIcon1, sortIcon2, sortIcon3)

    fun clickSort(view: View) {
        when (val idx = (view.parent as ViewGroup).indexOfChild(view)) {
            sortedItemIdx -> {
                isAscending = !isAscending
                sortIconList[sortedItemIdx].value =
                    if (isAscending) IconSetting.UP else IconSetting.DOWN
            }
            else -> {
                sortIconList[sortedItemIdx].value = IconSetting.NONE
                sortedItemIdx = idx
                isAscending = true
                sortIconList[idx].value = IconSetting.UP
            }
        }
        sortObserver.value = !(sortObserver.value!!)
    }

    fun getSortedList(qstList: List<Qst>): List<Qst> = when (sortedItemIdx) {
        0 -> {
            if (isAscending) qstList.sortedBy { it.cur_stage }
            else qstList.sortedByDescending { it.cur_stage }
        }
        1 -> {
            if (isAscending) qstList.sortedBy { it.title }
            else qstList.sortedByDescending { it.title }
        }
        2 -> {
            if (isAscending) qstList.sortedBy { it.registration_date }
            else qstList.sortedByDescending { it.registration_date }
        }
        else -> { qstList }
    }
}