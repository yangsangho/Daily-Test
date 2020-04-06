package kr.yangbob.memorization

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import kr.yangbob.memorization.data.DayInfo
import kr.yangbob.memorization.data.SimpleDate
import kr.yangbob.memorization.db.InfoCalendar
import kr.yangbob.memorization.db.Qst

object DataBindingAdapter {

    @BindingAdapter("app:onlyFirstPagerMainText")
    @JvmStatic
    fun setStartPagerMainText(view: TextView, index: Int?) {
        index?.also {
            view.text = view.context.getString(when (it) {
                0 -> R.string.only_first_pager_main1
                1 -> R.string.only_first_pager_main2
                2 -> R.string.only_first_pager_main3
                3 -> R.string.only_first_pager_main4
                else -> throw IllegalArgumentException()
            })
        }
    }

    @BindingAdapter("app:onlyFirstPagerSubText")
    @JvmStatic
    fun setStartPagerSubText(view: TextView, index: Int?) {
        index?.also {
            view.text = view.context.getString(when (it) {
                0 -> R.string.only_first_pager_sub1
                1 -> R.string.only_first_pager_sub2
                2 -> R.string.only_first_pager_sub3
                3 -> R.string.only_first_pager_sub4
                else -> throw IllegalArgumentException()
            })
        }
    }

    @BindingAdapter("app:onlyFirstPagerImage")
    @JvmStatic
    fun setStartPagerImage(view: ImageView, index: Int?) {
        index?.also {
            val resourceId: Int = when (it) {
                0 -> R.drawable.ic_start_test
                1 -> R.drawable.ic_start_meta
                2 -> R.drawable.ic_start_output
                3 -> R.drawable.ic_start_memory
                else -> throw IllegalArgumentException()
            }
            view.setImageResource(resourceId)
            view.tag = resourceId
        }
    }

    @BindingAdapter("app:onlyFirstIndexImage")
    @JvmStatic
    fun setStartIndex(view: ImageView, isTrue: Boolean?) {
        isTrue?.also {
            view.setImageResource(
                    if (it) R.drawable.ic_start_index_true
                    else R.drawable.ic_start_index_false
            )
            view.tag = it
        }
    }


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
    fun setFormatDate(view: TextView, date: SimpleDate) {
        view.text = date.getFormattedDate()
    }

    @BindingAdapter("app:nextTestDate")
    @JvmStatic
    fun nextTestDate(view: TextView, qst: Qst) {
        view.text = if(qst.is_dormant) view.context.getString(R.string.dormant)
        else qst.next_test_date.getFormattedDate()
    }
}