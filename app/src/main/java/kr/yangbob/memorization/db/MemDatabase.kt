package kr.yangbob.memorization.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Qst::class, QstRecord::class, QstCalendar::class], version = 1)
@TypeConverters(MemConverter::class)
abstract class MemDatabase : RoomDatabase()
{
    abstract fun getDaoQst(): DaoQst
    abstract fun getDaoQstRecord(): DaoQstRecord
    abstract fun getDaoQstCalendar(): DaoQstCalendar
}