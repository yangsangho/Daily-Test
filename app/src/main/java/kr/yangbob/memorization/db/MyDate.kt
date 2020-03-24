package kr.yangbob.memorization.db

import java.text.DateFormat
import java.util.*

class MyDate : Comparable<MyDate> {
    private lateinit var cal: Calendar
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var dateInt: Int = 0

    constructor(calendar: Calendar) {
        cal = calendar.clone() as Calendar
        makeDateInt()
    }

    constructor(dateInt: Int) {
        this.dateInt = dateInt
        makeCalendar()
    }

    fun getTime(): Long = cal.timeInMillis

    fun getString(style: Int = DateFormat.DEFAULT): String {
        val formatter = DateFormat.getDateInstance(style)
        return formatter.format(cal.time)
    }

    fun addDate(type: Int, value: Int) {
        cal.add(type, value)
        makeDateInt()
    }

    fun clone(): MyDate = MyDate(cal)

    private fun makeDateInt() {
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH) + 1
        day = cal.get(Calendar.DAY_OF_MONTH)

        val monthStr = if (month < 10) "0$month"
        else month.toString()
        val dayStr = if (day < 10) "0$day"
        else day.toString()

        dateInt = (year.toString() + monthStr + dayStr).toInt()
    }

    private fun makeCalendar() {
        val dateStr = dateInt.toString()
        if (dateStr.length != 8) throw IllegalArgumentException()

        year = dateStr.substring(0, 4).toInt()
        month = dateStr.substring(4, 6).toInt()
        day = dateStr.substring(6, 8).toInt()

        cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, day)
    }

    override fun compareTo(other: MyDate): Int = dateInt - other.dateInt
    override fun equals(other: Any?): Boolean {
        val otherDate = other as MyDate
        return dateInt == otherDate.dateInt
    }
}