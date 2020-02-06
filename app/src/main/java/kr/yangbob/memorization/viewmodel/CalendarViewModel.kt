package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.QstCalendar
import kr.yangbob.memorization.model.MemRepository
import java.util.*

class CalendarViewModel(private val memRepo: MemRepository) : ViewModel() {
    private val _strMonth = MutableLiveData<String>()
    private val _strYear = MutableLiveData<String>()
    val strMonth: LiveData<String> = _strMonth
    val strYear: LiveData<String> = _strYear
    private val qstCalendarList: List<QstCalendar> = memRepo.getAllCalendar()

    fun getQstCalendarList(cal: Calendar) = qstCalendarList.filter {
        it.id.substring(0, 4).toInt() == cal.get(Calendar.YEAR)
                && it.id.substring(5, 7).toInt() == cal.get(Calendar.MONTH) + 1
    }

    fun getCalendarList(): List<Calendar> {
        val todayCal: Calendar = Calendar.getInstance()
        val minTime = qstCalendarList.minBy { it.id }?.let {
            memRepo.getDateLong(it.id)
        } ?: throw NoSuchFieldException()
        val minCal = (todayCal.clone() as Calendar).apply {
            timeInMillis = minTime
        }

        return makeCalList(todayCal, minCal)
    }

    fun setCalendar(calendar: Calendar) {
        _strMonth.value = "${calendar.get(Calendar.MONTH) + 1}월"
        _strYear.value = "${calendar.get(Calendar.YEAR)}년"
    }

    private tailrec fun makeCalList(
        cal: Calendar,
        minCal: Calendar,
        acc: List<Calendar> = listOf()
    ): List<Calendar> = when {
        cal.get(Calendar.YEAR) <= minCal.get(Calendar.YEAR)
                && cal.get(Calendar.MONTH) < minCal.get(Calendar.MONTH) -> acc
        else -> {
            val cal2: Calendar = (cal.clone() as Calendar).apply {
                if (get(Calendar.MONTH) == 0) {
                    set(Calendar.YEAR, get(Calendar.YEAR) - 1)
                    set(Calendar.MONTH, Calendar.DECEMBER)
                } else {
                    set(Calendar.MONTH, get(Calendar.MONTH) - 1)
                }
            }
            makeCalList(cal2, minCal, listOf(cal) + acc)
        }
    }
}