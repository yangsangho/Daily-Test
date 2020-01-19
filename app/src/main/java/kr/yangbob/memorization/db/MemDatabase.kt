package kr.yangbob.memorization.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Qst::class, Test::class, CalData::class], version = 1)
@TypeConverters(MemConverter::class)
abstract class MemDatabase : RoomDatabase()
{
    abstract fun getQstDao(): QstDao
    abstract fun getTestDao(): TestDao
    abstract fun getCalDataDao(): CalDataDao
}