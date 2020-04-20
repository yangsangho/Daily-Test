package kr.yangbob.memorization.data

import java.text.DateFormat
import java.util.*
import kotlin.math.absoluteValue

class SimpleDate : Comparable<SimpleDate> {
    private var dateInt: Int = 0
    private var year: Int = 0
    private var month: Int = 0
    private var dayOfMonth: Int = 0

    private constructor(calendar: Calendar) {
        makeFromCalendar(calendar)
    }

    private constructor(dateInt: Int) {
        this.dateInt = dateInt
        makeFromDateInt()
    }

    private fun makeFromCalendar(calendar: Calendar) {
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        setDateInt()
    }

    private fun makeFromDateInt() {
        val dateStr = dateInt.toString()
        if (dateStr.length != 8) throw IllegalArgumentException()
        setYearFromDateStr(dateStr)
        setMonthFromDateStr(dateStr)
        setDayFromDateStr(dateStr)
    }

    private fun setDateInt() {
        dateInt = (year.toString() + getMonthStr() + getDayStr()).toInt()
    }

    private fun getMonthStr(): String = if (month < 10) "0$month" else month.toString()

    private fun getDayStr(): String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()

    private fun setYearFromDateStr(dateStr: String) {
        year = dateStr.substring(0, 4).toInt()
        if (year < 1960 || year > 2100) throw IllegalArgumentException()
    }

    private fun setMonthFromDateStr(dateStr: String) {
        month = dateStr.substring(4, 6).toInt()
        if (month < 1 || month > 12) throw IllegalArgumentException()
    }

    private fun setDayFromDateStr(dateStr: String) {
        dayOfMonth = dateStr.substring(6, 8).toInt()
        if (dayOfMonth < 1 || dayOfMonth > 31) throw IllegalArgumentException()
    }

    private fun getCalendar(): Calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }

    fun getFormattedDate(dateFormat: Int = DateFormat.DEFAULT, locale: Locale = Locale.getDefault()): String {
        val formatter = DateFormat.getDateInstance(dateFormat, locale)
        return formatter.format(getCalendar().time)
    }

    fun addDate(field: Int, value: Int) {
        if(field != Calendar.YEAR && field != Calendar.MONTH && field != Calendar.DAY_OF_MONTH)
            throw IllegalArgumentException()
        val cal = getCalendar()
        cal.add(field, value)
        makeFromCalendar(cal)
    }

    fun setDate(field: Int, value: Int) {
        if(field != Calendar.YEAR && field != Calendar.MONTH && field != Calendar.DAY_OF_MONTH)
            throw IllegalArgumentException()
        val cal = getCalendar()
        cal.set(field, if (field == Calendar.MONTH) value - 1 else value)
        makeFromCalendar(cal)
    }

    fun getDateDiff(other: SimpleDate, field: Int): Int = when (field) {
        Calendar.YEAR -> {
            getYearDifference(other)
        }
        Calendar.MONTH -> {
            getMonthDifference(other)
        }
        Calendar.DAY_OF_MONTH -> {
            getDayDifference(other)
        }
        else -> throw IllegalArgumentException()
    }

    private fun getYearDifference(other: SimpleDate): Int = (this.year - other.year).absoluteValue

    private fun getMonthDifference(other: SimpleDate): Int {
        return if(this.year == other.year)
            (this.month - other.month).absoluteValue
        else{
            val diffDateString = ("$year${getMonthStr()}".toInt() - "${other.year}${other.getMonthStr()}".toInt()).absoluteValue.toString()
            val diffYear = diffDateString.substring(0, diffDateString.length - 2).toInt()
            var diffMonth = diffDateString.substring(diffDateString.length - 2).toInt()
            if (diffMonth > 11) diffMonth -= 88
            diffYear * 12 + diffMonth
        }
    }

    private fun getDayDifference(other: SimpleDate): Int {
        val originCal = this.getCalendar()
        val otherCal = other.getCalendar()
        val diffTime = (originCal.timeInMillis - otherCal.timeInMillis).absoluteValue
        return (diffTime / (24 * 60 * 60 * 1000)).toInt()
    }

    fun clone(): SimpleDate = SimpleDate(dateInt)

    fun getDateInt(): Int = dateInt

    fun getYear(): Int = year

    fun getMonth(): Int = month

    fun getDayOfMonth(): Int = dayOfMonth

    override fun compareTo(other: SimpleDate): Int = dateInt - other.dateInt

    override fun equals(other: Any?): Boolean {
        val otherDate = other as SimpleDate
        return dateInt == otherDate.dateInt
    }

    override fun hashCode(): Int {
        return dateInt
    }

    companion object {
        fun newInstanceToday(): SimpleDate = SimpleDate(Calendar.getInstance())
        fun newInstanceFromDateInt(dateInt: Int): SimpleDate = SimpleDate(dateInt)
    }
}