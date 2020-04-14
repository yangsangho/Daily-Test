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
    private lateinit var currentDate: SimpleDate

    init {
        binding.holder = this
    }

    fun bind(date: SimpleDate, isLastMonth: Boolean) {
        infoCalendarList = model.getInfoCalendarList(date)

        val dayInfoListBuilder = DayInfoListBuilder(date, infoCalendarList)
        dayInfoList = dayInfoListBuilder.build()
        binding.dayList = dayInfoList

        binding.cntPrevMonthDay = dayInfoListBuilder.getCntPrevMonthDay()

        currentDate = when (isLastMonth) {
            true -> infoCalendarList.last().date
            false -> infoCalendarList.first().date
        }
    }

    fun updateDayInfoList(dateIntSetForDelete: HashSet<Int>) {
        var isUpdated = false

        dayInfoList.forEach {
            it.infoCalendar?.let { infoCal ->
                if (dateIntSetForDelete.contains(infoCal.date.getDateInt())) {
                    val newIsCompleted = model.getTestCompletionOnDate(infoCal.date)
                    if (infoCal.isCompleted != newIsCompleted) {
                        infoCal.isCompleted = newIsCompleted
                        isUpdated = true
                    }
                }
            }
        }

        if (isUpdated) binding.dayList = dayInfoList
    }


    fun attached() {
        setBindingCurrentDay()
        setRecordText()
    }

    private fun setBindingCurrentDay(){
        binding.currentDay = currentDate.getDayOfMonth()
    }

    fun setRecordText(infoCalendar: InfoCalendar? = null){
        val infoCal = infoCalendar ?: infoCalendarList.find { it.date.getDayOfMonth() == currentDate.getDayOfMonth() }
        model.setRecordText(infoCal, binding.root.resources)
    }

    fun click(infoCalendar: InfoCalendar?) {
        infoCalendar?.also { infoCal ->
            currentDate = infoCal.date
            setBindingCurrentDay()
            setRecordText(infoCal)
        }
    }
}