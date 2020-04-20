package kr.yangbob.memorization.data

data class DayInfo(
        val day: Int,
        val isInOut: Boolean,
        val infoCalendar: InfoCalendar? = null
)
