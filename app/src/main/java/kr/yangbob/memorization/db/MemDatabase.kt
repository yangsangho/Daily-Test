package kr.yangbob.memorization.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kr.yangbob.memorization.data.SimpleDate

@Database(entities = [Qst::class, QstRecord::class, QstCalendar::class], version = 1)
@TypeConverters(Converters::class)
abstract class MemDatabase : RoomDatabase()
{
    abstract fun getDaoQst(): DaoQst
    abstract fun getDaoQstRecord(): DaoQstRecord
    abstract fun getDaoQstCalendar(): DaoQstCalendar
}

class Converters{
    @TypeConverter
    fun dateToInt(date: SimpleDate): Int = date.dateInt

    @TypeConverter
    fun intToDate(dateInt: Int): SimpleDate = SimpleDate(dateInt)
}