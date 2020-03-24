package kr.yangbob.memorization.db

import java.util.*

class MyDate : Comparable<MyDate>{
    private val cal: Calendar
    val dateInt: Int

    constructor(calendar: Calendar){
        cal = calendar.clone() as Calendar
        dateInt = toDateInt(calendar)
    }
    constructor(dateInt: Int){
        this.dateInt = dateInt
        cal = toCalendar(dateInt)
    }

    companion object{
        fun toDateInt(cal: Calendar): Int{
            val yearStr = cal.get(Calendar.YEAR).toString()
            val month = cal.get(Calendar.MONTH) + 1
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val monthStr = if(month < 10) "0$month"
            else month.toString()
            val dayStr = if(day < 10) "0$day"
            else day.toString()

            return (yearStr + monthStr + dayStr).toInt()
        }
        fun toCalendar(dateInt: Int): Calendar{
            val dateStr = dateInt.toString()
            if(dateStr.length != 8) throw IllegalArgumentException()

            val year = dateStr.substring(0,4).toInt()
            val month = dateStr.substring(4,6).toInt()
            val day = dateStr.substring(6,8).toInt()

            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month - 1)
            cal.set(Calendar.DAY_OF_MONTH, day)

            return cal
        }
    }

    override fun compareTo(other: MyDate): Int = dateInt - other.dateInt
    override fun equals(other: Any?): Boolean {
        val otherDate = other as MyDate
        return dateInt == otherDate.dateInt
    }
}