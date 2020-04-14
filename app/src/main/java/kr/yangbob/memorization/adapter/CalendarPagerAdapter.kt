package kr.yangbob.memorization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.data.SimpleDate
import kr.yangbob.memorization.databinding.ActivityCalendarLayoutBinding
import kr.yangbob.memorization.viewmodel.CalendarViewModel

class CalendarPagerAdapter(
        private val dateList: List<SimpleDate>,
        private val model: CalendarViewModel) : RecyclerView.Adapter<CalendarViewHolder>() {
    private val viewHolderList: Array<CalendarViewHolder?> = arrayOfNulls(dateList.size)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding =
                ActivityCalendarLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding, model)
    }

    override fun getItemCount(): Int = dateList.size

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        // true : 마지막날을 click으로 , false : 첫날을 click으로
        val isLastMonth: Boolean = position == dateList.size - 1
        holder.bind(dateList[position], isLastMonth)
        viewHolderList[position] = holder
    }

    override fun onViewRecycled(holder: CalendarViewHolder) {
        super.onViewRecycled(holder)
        viewHolderList[holder.adapterPosition] = null
    }

    override fun onViewAttachedToWindow(holder: CalendarViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.attached()
    }

    fun deleteProcess(dateIntSetForDelete: HashSet<Int>, currentPosition: Int){
        viewHolderList[currentPosition]?.run {
            setRecordText()
            updateDayInfoList(dateIntSetForDelete)
        }
        viewHolderList.forEachIndexed { idx, holder ->
            if (holder != null && idx != currentPosition) {
                holder.updateDayInfoList(dateIntSetForDelete)
            }
        }
    }
}