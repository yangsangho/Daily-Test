package kr.yangbob.memorization.data

import kr.yangbob.memorization.db.InfoCalendar

data class DayInfo(
        val day: Int,
        val isInOut: Boolean,
        val infoCalendar: InfoCalendar? = null
)
