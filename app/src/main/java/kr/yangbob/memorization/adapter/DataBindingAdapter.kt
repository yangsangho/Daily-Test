package kr.yangbob.memorization.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import kr.yangbob.memorization.R
import kr.yangbob.memorization.Stage
import java.text.DateFormat
import java.util.*

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