package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.InfoCalendar
import kr.yangbob.memorization.model.MemRepository
import java.util.*

class CalendarViewModel(private val memRepo: MemRepository) : ViewModel() {
    private var isPossibleClick = false
    fun resetIsPossibleClick() {
        isPossibleClick = false
    }

    fun checkIsPossibleClick(): Boolean {
        return if (isPossibleClick) false
        else {
            isPossibleClick = true
            true
        }
    }

    private val _month = MutableLiveData<Int>()
    private val _year = MutableLiveData<Int>()
    val month: LiveData<Int> = _month
    val year: LiveData<Int> = _year
    private val infoCalendarList: List<InfoCalendar> = memRepo.getAllInfoCalendar()

    fun getQstCalendarList(yearMonth: Int) = infoCalendarList.filter {
        it.yearMonth == yearMonth
    }

    fun yearMonthList(): List<String> {
        val todayCal: Calendar = Calendar.getInstance()
        val minTime = memRepo.getStartDateStr().let {
            memRepo.getDateLong(it)
        }
        val minCal = (todayCal.clone() as Calendar).apply {
            timeInMillis = minTime
        }

        return makeCalList(todayCal, minCal)
            .map { String.format("%d%02d", it.get(Calendar.YEAR), it.get(Calendar.MONTH) + 1) }
    }

    fun setCalendar(yearMonth: String) {
        _month.value = yearMonth.substring(4).toInt() - 1
        _year.value = yearMonth.substring(0, 4).toInt()
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