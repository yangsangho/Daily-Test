package kr.yangbob.memorization.adapter

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import kr.yangbob.memorization.R

object DataBindingAdapter {
    @BindingAdapter("app:correct")
    @JvmStatic fun testChkIcon(view: ImageView, correct: Boolean?) {
        if(correct != null){
            if(correct) {
                view.setImageResource(R.drawable.ic_check_circle_black_24dp)
                view.setColorFilter(ContextCompat.getColor(view.context, android.R.color.holo_green_light), android.graphics.PorterDuff.Mode.SRC_IN)
            } else {
                view.setImageResource(R.drawable.ic_close_black_24dp)
                view.setColorFilter(ContextCompat.getColor(view.context, android.R.color.holo_red_light), android.graphics.PorterDuff.Mode.SRC_IN)
            }
        } else {
            view.setImageResource(android.R.color.transparent)
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