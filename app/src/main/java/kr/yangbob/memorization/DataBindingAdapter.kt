package kr.yangbob.memorization

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import kr.yangbob.memorization.calendar.DayInfo
import kr.yangbob.memorization.db.InfoCalendar
import java.text.DateFormat

object DataBindingAdapter {
    @BindingAdapter("app:noItemIcon")
    @JvmStatic
    fun setNoItemIcon(view: ImageView, isTest: Boolean?) {
        isTest?.also {
            if (it) view.setImageResource(R.drawable.ic_rest)
            else view.setImageResource(R.drawable.ic_write)
        }
    }

    @BindingAdapter("app:setCalendarBackground")
    @JvmStatic
    fun setCalendarBackground(view: ImageView, infoCalendar: InfoCalendar?) {
        infoCalendar?.also {
            view.visibility = View.VISIBLE
            if (it.isStartDay) {
                view.setColorFilter(
                    ContextCompat.getColor(view.context, R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                if (it.isCompleted == null) view.clearColorFilter()
                else {
                    if (it.isCompleted!!) view.setColorFilter(
                        ContextCompat.getColor(
                            view.context,
                            android.R.color.holo_green_light
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    else view.setColorFilter(
                        ContextCompat.getColor(
                            view.context,
                            android.R.color.holo_red_light
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            }
        }
    }

    @BindingAdapter("app:isSunday", "app:dayInfo")
    @JvmStatic
    fun setDayText(view: TextView, isSunday: Boolean?, dayInfo: DayInfo) {
        val alpha = if (dayInfo.isInOut) 80 else 255
        val color = when {
            isSunday == null -> Color.argb(alpha, 0, 0, 0)
            isSunday -> Color.argb(alpha, 183, 28, 28)
            else -> Color.argb(alpha, 0, 0, 183)
        }
        view.text = dayInfo.day.toString()
        view.setTextColor(color)
    }

    @BindingAdapter("app:setMonth")
    @JvmStatic
    fun setMonth(view: TextView, month: Int) {
        view.text = view.context.resources.getStringArray(R.array.calendar_month)[month]
    }

    @BindingAdapter("app:setYear")
    @JvmStatic
    fun setYear(view: TextView, year: Int) {
        view.text = String.format(view.context.resources.getString(R.string.calendar_year), year)
    }


    @BindingAdapter("app:complete")
    @JvmStatic
    fun setCompleteIcon(view: ImageView, complete: Boolean?) {
        if (complete != null) {
            if (complete) {
                view.setImageResource(R.drawable.ic_check_circle_black_24dp)
                view.setColorFilter(
                    ContextCompat.getColor(
                        view.context,
                        android.R.color.holo_green_light
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                view.setImageResource(R.drawable.ic_x_circle_black_24dp)
                view.setColorFilter(
                    ContextCompat.getColor(
                        view.context,
                        android.R.color.holo_red_light
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    @BindingAdapter("app:correct")
    @JvmStatic
    fun testChkIcon(view: ImageView, correct: Boolean?) {
        if (correct != null) {
            if (correct) {
                view.setImageResource(R.drawable.ic_check_circle_black_24dp)
                view.setColorFilter(
                    ContextCompat.getColor(
                        view.context,
                        android.R.color.holo_green_light
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                view.setImageResource(R.drawable.ic_x_circle_black_24dp)
                view.setColorFilter(
                    ContextCompat.getColor(
                        view.context,
                        android.R.color.holo_red_light
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        } else {
            view.setImageResource(R.drawable.ic_no_circle_black_24dp)
            view.setColorFilter(
                ContextCompat.getColor(
                    view.context,
                    android.R.color.darker_gray
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }

    @BindingAdapter("app:stage")
    @JvmStatic
    fun setStage(view: ImageView, stage: Int) {
        view.setImageResource(
            when (stage) {
                0 -> R.drawable.ic_stage_new
                1 -> R.drawable.ic_stage_1_1
                2 -> R.drawable.ic_stage_1_2
                3 -> R.drawable.ic_stage_1_3
                4 -> R.drawable.ic_stage_3
                5 -> R.drawable.ic_stage_7
                6 -> R.drawable.ic_stage_15
                7 -> R.drawable.ic_stage_30
                else -> R.drawable.ic_stage_30
            }
        )
    }

    @BindingAdapter("app:qnaIcon")
    @JvmStatic
    fun setQnaIcon(view: ImageView, isFront: Boolean) {
        view.setImageResource(if (isFront) R.drawable.ic_question_black_24dp else R.drawable.ic_answer_black_24dp)
    }

    @BindingAdapter("app:defaultFormatDate")
    @JvmStatic
    fun setFormatDate(view: TextView, dateStr: String) {
        val time = dateFormat.parse(dateStr)?.time ?: 0
        val formatter = DateFormat.getDateInstance(DateFormat.DEFAULT)
        view.text = formatter.format(time)
    }

    @BindingAdapter("app:alignIcon")
    @JvmStatic
    fun setAlignIcon(view: ImageView, iconSetting: IconSetting?) {
        if (iconSetting != null) {
            when (iconSetting) {
                IconSetting.NONE -> {
                    view.visibility = View.INVISIBLE
                }
                IconSetting.UP -> {
                    view.visibility = View.VISIBLE
                    view.setImageResource(R.drawable.ic_arrow_upward_black_24dp)
                }
                IconSetting.DOWN -> {
                    view.visibility = View.VISIBLE
                    view.setImageResource(R.drawable.ic_arrow_downward_black_24dp)
                }
            }
        }
    }

//    @BindingAdapter("date")
//    @JvmStatic
//    fun date(view: TextView, calendar: Calendar) {
//        val formatter = DateFormat.getDateInstance(DateFormat.MEDIUM)
//        formatter.timeZone = calendar.timeZone
//
//        val formatStr = formatter.format(calendar.time)
//        view.text = formatStr
//    }
//
//    @BindingAdapter("checked")
//    @JvmStatic
//    fun checked(view: ImageView, isCorrect: Boolean?) {
//        isCorrect?.let {
//            if(it) {
//                view.setImageResource(R.drawable.ic_check_black_24dp)
//                view.setColorFilter(android.R.color.holo_green_light)
//            } else {
//                view.setImageResource(R.drawable.ic_close_black_24dp)
//                view.setColorFilter(android.R.color.holo_red_light)
//            }
//        }
//    }
//
//    @BindingAdapter("challenge_stage")
//    @JvmStatic
//    fun challengeStage(view: ImageView, stage: Stage) {
//        val resourceId = when(stage){
//            Stage.INIT, Stage.BEGIN_ONE, Stage.BEGIN_TWO -> R.drawable.ic_stage_1_1
//            Stage.BEGIN_THREE -> R.drawable.ic_stage_3
//            Stage.AFTER_THREE -> R.drawable.ic_stage_7
//            Stage.AFTER_WEEK -> R.drawable.ic_stage_15
//            Stage.AFTER_HALF -> R.drawable.ic_stage_30
//            else -> 0
//        }
//        if(resourceId > 0) view.setImageResource(resourceId)
//    }
}