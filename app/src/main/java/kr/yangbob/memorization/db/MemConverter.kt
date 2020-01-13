package kr.yangbob.memorization.db

import androidx.room.TypeConverter
import java.util.*

object MemConverter
{
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Calendar? = value?.let { value ->
        GregorianCalendar().also { calendar ->
            calendar.timeInMillis = value
        }
    }

    @TypeConverter
    @JvmStatic
    fun toTimestamp(timestamp: Calendar?): Long? = timestamp?.timeInMillis

    @TypeConverter
    @JvmStatic
    fun fromStage(stage: Stage?): Byte? = stage?.num?.toByte()

    @TypeConverter
    @JvmStatic
    fun toStage(num: Byte?): Stage? = when (num?.toInt())
    {
        1    -> Stage.BEGIN_ONE
        2    -> Stage.BEGIN_TWO
        3    -> Stage.BEGIN_THREE
        4    -> Stage.AFTER_THREE
        5    -> Stage.AFTER_WEEK
        6    -> Stage.AFTER_HALF
        7    -> Stage.AFTER_MONTH
        else -> null
    }
}


