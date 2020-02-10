package kr.yangbob.memorization.calendar

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by WoochanLee on 25/03/2019.
 */
class BaseCalendar(yearMonth: String){

    companion object {
        const val DAYS_OF_WEEK = 7
        const val LOW_OF_CALENDAR = 6
    }

    private val calendar: Calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, yearMonth.substring(0, 4).toInt())
        set(Calendar.MONTH, yearMonth.substring(4).toInt() - 1)
    }

    var cntPrevMonthDate = 0
    var cntNextMonthDate = 0
    var maxDateCurrentMonth = 0

    val dateList = mutableListOf<Int>()

    /**
     * Init calendar.
     */
    init {
        makeMonthDate()
    }

    /**
     * make month date.
     */
    private fun makeMonthDate() {

        dateList.clear()

        calendar.set(Calendar.DATE, 1)

        maxDateCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        cntPrevMonthDate = calendar.get(Calendar.DAY_OF_WEEK) - 1

        makePrevMonthTail(calendar.clone() as Calendar)
        makeCurrentMonth(calendar)

        cntNextMonthDate = LOW_OF_CALENDAR * DAYS_OF_WEEK - (cntPrevMonthDate + maxDateCurrentMonth)
        makeNextMonthHead()
    }

    /**
     * Generate data for the last month displayed before the first day of the current calendar.
     */
    private fun makePrevMonthTail(calendar: Calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        val maxDate = calendar.getActualMaximum(Calendar.DATE)
        var maxOffsetDate = maxDate - cntPrevMonthDate

        for (i in 1..cntPrevMonthDate) dateList.add(++maxOffsetDate)
    }

    /**
     * Generate data for the current calendar.
     */
    private fun makeCurrentMonth(calendar: Calendar) {
        for (i in 1..calendar.getActualMaximum(Calendar.DATE)) dateList.add(i)
    }

    /**
     * Generate data for the next month displayed before the last day of the current calendar.
     */
    private fun makeNextMonthHead() {
        var date = 1

        for (i in 1..cntNextMonthDate) dateList.add(date++)
    }
}