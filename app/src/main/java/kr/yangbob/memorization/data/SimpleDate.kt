package kr.yangbob.memorization.data

import java.text.DateFormat
import java.util.*

class SimpleDate : Comparable<SimpleDate> {
    private var dateInt: Int = 0
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

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
        day = calendar.get(Calendar.DAY_OF_MONTH)
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

    private fun getDayStr(): String = if (day < 10) "0$day" else day.toString()

    private fun setYearFromDateStr(dateStr: String) {
        year = dateStr.substring(0, 4).toInt()
        if (year < 1960 || year > 2100) throw IllegalArgumentException()
    }

    private fun setMonthFromDateStr(dateStr: String) {
        month = dateStr.substring(4, 6).toInt()
        if (month < 1 || month > 12) throw IllegalArgumentException()
    }

    private fun setDayFromDateStr(dateStr: String) {
        day = dateStr.substring(6, 8).toInt()
        if (day < 1 || day > 31) throw IllegalArgumentException()
    }

    private fun getCalendar(): Calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, day)
    }

    fun getFormattedDate(dateFormat: Int = DateFormat.DEFAULT): String {
        val formatter = DateFormat.getDateInstance(dateFormat)
        return formatter.format(getCalendar().time)
    }

    fun addDate(field: Int, value: Int) {
        val cal = getCalendar()
        cal.add(field, value)
        makeFromCalendar(cal)
    }

    fun setDate(field: Int, value: Int) {
        val cal = getCalendar()
        cal.set(field, if(field == Calendar.MONTH) value - 1 else value)
        makeFromCalendar(cal)
    }

    fun clone(): SimpleDate = SimpleDate(dateInt)

    fun getDateInt(): Int = dateInt

    fun getYear(): Int = year

    fun getMonth(): Int = month

    fun getDay(): Int = day

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