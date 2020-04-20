package kr.yangbob.memorization.data

data class InfoCalendar(
        val date: SimpleDate,
        var isCompleted: Boolean?,
        var isStartDay: Boolean = false        // true : start day , false : remain all
)