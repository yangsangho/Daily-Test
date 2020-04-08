package kr.yangbob.memorization.adapter

import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.data.DayInfo
import kr.yangbob.memorization.data.DayInfoListBuilder
import kr.yangbob.memorization.data.SimpleDate
import kr.yangbob.memorization.databinding.ActivityCalendarLayoutBinding
import kr.yangbob.memorization.db.InfoCalendar
import kr.yangbob.memorization.viewmodel.CalendarViewModel

class CalendarViewHolder(
        private val binding: ActivityCalendarLayoutBinding,
        private val model: CalendarViewModel) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var infoCalendarList: List<InfoCalendar>
    private lateinit var dayInfoList: List<DayInfo>
    private var clickedDay = 0
    private var cntPrevMonthDay = 0

    fun bind(date: SimpleDate, isLastMonth: Boolean?) {
        infoCalendarList = model.getInfoCalendarList(date)

        val dayInfoListBuilder = DayInfoListBuilder(date, infoCalendarList)
        cntPrevMonthDay = dayInfoListBuilder.getCntPrevMonthDay()
        dayInfoList = dayInfoListBuilder.build()

        binding.dayList = dayInfoList
        binding.holder = this

        this.clickedDay = when (isLastMonth) {
            true -> infoCalendarList.last().date.getDayOfMonth()
            false -> infoCalendarList.first().date.getDayOfMonth()
            else -> 1
        }
        this.clickedDay += cntPrevMonthDay
    }

    fun checkForDelete(deleteSet: HashSet<Int>?) {
        model.setCurrentCalendar(
                infoCalendarList.find {
                    it.date.getDayOfMonth() == this.clickedDay - cntPrevMonthDay
                }, binding.root.resources)
        updateBaseCal(deleteSet)
    }

    fun updateBaseCal(deleteSet: HashSet<Int>?) {
        dayInfoList.forEach {
            it.infoCalendar?.let { infoCal ->
                var isChanged = false
                deleteSet?.also { set ->
                    if (set.contains(infoCal.date.getDateInt())) {
                        val newValue = model.getCalTestComplete(infoCal.date)
                        if (infoCal.isCompleted != newValue) {
                            infoCal.isCompleted = newValue
                            isChanged = true
                        }
                    }
                }
                if (isChanged)
                    binding.dayList = dayInfoList
            }
        }
    }

    fun attached() {
        binding.clickedDay = this.clickedDay
        model.setCurrentCalendar(
                infoCalendarList.find {
                    it.date.getDayOfMonth() == this.clickedDay - cntPrevMonthDay
                },
                binding.root.resources)
    }

    fun detached() {
        binding.clickedDay = 0
    }

    fun click(infoCalendar: InfoCalendar?) {
        infoCalendar?.also { infoCal ->
            this.clickedDay = cntPrevMonthDay + infoCal.date.getDayOfMonth()
            binding.clickedDay = this.clickedDay
            model.setCurrentCalendar(infoCal, binding.root.resources)
        }
    }
}