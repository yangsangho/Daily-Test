package kr.yangbob.memorization.db

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.room.TypeConverter
import kr.yangbob.memorization.R
import java.text.DateFormat
import java.util.*

enum class Stage(val num: Int) {
    INIT(0),
    BEGIN_ONE(1), BEGIN_TWO(2), BEGIN_THREE(3), AFTER_THREE(4),
    AFTER_WEEK(5), AFTER_HALF(6), AFTER_MONTH(7)

}

object DataBindingAdapter {
    @BindingAdapter("date")
    @JvmStatic
    fun date(view: TextView, calendar: Calendar) {
        val formatter = DateFormat.getDateInstance(DateFormat.MEDIUM)
        formatter.timeZone = calendar.timeZone

        val formatStr = formatter.format(calendar.time)
        view.text = formatStr
    }

    @BindingAdapter("checked")
    @JvmStatic
    fun checked(view: ImageView, isCorrect: Boolean?) {
        isCorrect?.let {
            if(it) {
                view.setImageResource(R.drawable.ic_check_black_24dp)
                view.setColorFilter(android.R.color.holo_green_light)
            } else {
                view.setImageResource(R.drawable.ic_close_black_24dp)
                view.setColorFilter(android.R.color.holo_red_light)
            }
        }
    }

    @BindingAdapter("challenge_stage")
    @JvmStatic
    fun challengeStage(view: ImageView, stage: Stage) {
        val resourceId = when(stage){
            Stage.INIT, Stage.BEGIN_ONE, Stage.BEGIN_TWO -> R.drawable.ic_stage_1_1
            Stage.BEGIN_THREE -> R.drawable.ic_stage_3
            Stage.AFTER_THREE -> R.drawable.ic_stage_7
            Stage.AFTER_WEEK -> R.drawable.ic_stage_15
            Stage.AFTER_HALF -> R.drawable.ic_stage_30
            else -> 0
        }
        if(resourceId > 0) view.setImageResource(resourceId)
    }
}

object MemConverter {
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Calendar? = value?.let {
        GregorianCalendar().also { calendar ->
            calendar.timeInMillis = it
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
    fun toStage(num: Byte?): Stage? = when (num?.toInt()) {
        0 -> Stage.INIT
        1 -> Stage.BEGIN_ONE
        2 -> Stage.BEGIN_TWO
        3 -> Stage.BEGIN_THREE
        4 -> Stage.AFTER_THREE
        5 -> Stage.AFTER_WEEK
        6 -> Stage.AFTER_HALF
        7 -> Stage.AFTER_MONTH
        else -> null
    }
}


