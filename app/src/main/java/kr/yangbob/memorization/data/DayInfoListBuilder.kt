package kr.yangbob.memorization.data

import kr.yangbob.memorization.db.InfoCalendar
import java.util.*

class DayInfoListBuilder(date: SimpleDate, private val infoCalList: List<InfoCalendar>){
    private val daysOfWeek = 7
    private val lowsOfCalendar = 6
    private val dayList = mutableListOf<DayInfo>()
    private val cntPrevMonthDay: Int
    private val calendar: Calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, date.getYear())
        set(Calendar.MONTH, date.getMonth() - 1)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    init {
        cntPrevMonthDay = calendar.get(Calendar.DAY_OF_WEEK) - 1
        makeMonthDate()
    }

    private fun makeMonthDate() {
        makePrevMonthTail()
        makeCurrentMonth()
        makeNextMonthHead()
    }

    private fun makePrevMonthTail() {
        val calendar = this.calendar.clone() as Calendar
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        var maxOffsetDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - cntPrevMonthDay

        for (i in 1..cntPrevMonthDay) dayList.add(DayInfo(++maxOffsetDate, true))
    }

    private fun makeCurrentMonth() {
        for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            val infoCalendar = infoCalList.find { it.date.getDayOfMonth() == i }
            dayList.add(DayInfo(i, false, infoCalendar))
        }
    }

    private fun makeNextMonthHead() {
        val cntNextMonthDate =
                daysOfWeek * lowsOfCalendar - (cntPrevMonthDay + calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        var date = 1
        for (i in 1..cntNextMonthDate) dayList.add(DayInfo(date++, true))
    }

    fun getCntPrevMonthDay() = cntPrevMonthDay

    fun build(): List<DayInfo> = dayList
}
