package kr.yangbob.memorization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.data.SimpleDate
import kr.yangbob.memorization.databinding.ActivityCalendarLayoutBinding
import kr.yangbob.memorization.viewmodel.CalendarViewModel

class CalendarPagerAdapter(
        private val dateList: List<SimpleDate>,
        private val model: CalendarViewModel,
        private val viewHolderList: Array<CalendarViewHolder?>)
    : RecyclerView.Adapter<CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding =
                ActivityCalendarLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding, model)
    }

    override fun getItemCount(): Int = dateList.size

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        viewHolderList[position] = holder
        var isLastMonth: Boolean? = null   // true : 마지막날을 click으로 , false : 첫날을 click으로, null : 1일을 click으로
        if (position == dateList.size - 1) {
            isLastMonth = true
        } else if (position == 0) {
            isLastMonth = false
        }
        holder.bind(dateList[position], isLastMonth)
    }
}