package kr.yangbob.memorization.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Qst::class, Test::class, Track::class], version = 1)
@TypeConverters(MemConverter::class)
abstract class MemDatabase : RoomDatabase()
{
    abstract fun getQnaDao(): QstDao
    abstract fun getTestDao(): TestDao
    abstract fun getTrackDao(): TrackDao

    companion object
    {
        private var INSTANCE: MemDatabase? = null

        fun getInstance(context: Context): MemDatabase?
        {
            if (INSTANCE == null)
            {
                synchronized(MemDatabase::class)
                {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                                                    MemDatabase::class.java, "Memorization"
                                                   ).fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE
        }
    }
}