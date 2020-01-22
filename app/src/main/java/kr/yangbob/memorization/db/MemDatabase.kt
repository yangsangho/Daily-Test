package kr.yangbob.memorization.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Qst::class, QstRecord::class, QstCalendar::class], version = 1)
abstract class MemDatabase : RoomDatabase()
{
    abstract fun getDaoQst(): DaoQst
    abstract fun getDaoQstRecord(): DaoQstRecord
    abstract fun getDaoQstCalendar(): DaoQstCalendar
}