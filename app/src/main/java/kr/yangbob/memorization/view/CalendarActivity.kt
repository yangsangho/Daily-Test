package kr.yangbob.memorization.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kr.yangbob.memorization.R
import kr.yangbob.memorization.data.DayInfo
import kr.yangbob.memorization.data.DayInfoListBuilder
import kr.yangbob.memorization.data.SimpleDate
import kr.yangbob.memorization.databinding.ActivityCalendarBinding
import kr.yangbob.memorization.databinding.ActivityCalendarLayoutBinding
import kr.yangbob.memorization.db.InfoCalendar
import kr.yangbob.memorization.viewmodel.CalendarViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class CalendarActivity : AppCompatActivity(), CoroutineScope {

    private val model: CalendarViewModel by viewModel()
    private lateinit var viewHolderList: Array<CalendarViewHolder?>
    private lateinit var calendarListAdapter: CalendarListAdapter
    private lateinit var job: Job
    private var prevPosition = 0
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            model.isPortrait = false
        } else {
            model.isPortrait = true
        }
        val binding: ActivityCalendarBinding = DataBindingUtil.setContentView(this, R.layout.activity_calendar)
        binding.lifecycleOwner = this
        binding.model = model

        job = Job()
        launch {
            toolBar.title = getString(R.string.calendar_appbar_title)
            setSupportActionBar(toolBar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            val dateList = model.getDateList()
            val maxIdx = dateList.size
            val recyclerDetachBindIdx = 3
            viewHolderList = arrayOfNulls(maxIdx)
            calendarListAdapter = CalendarListAdapter(dateList, model, viewHolderList)
            calendarViewPager.adapter = calendarListAdapter
            calendarViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (maxIdx > 4) { // item이 4개 이하면, onbind를 반복 안하더라
                        val direction = prevPosition - position
                        if (direction > 0) { // 좌측으로
                            val needSetNullIdx = position + recyclerDetachBindIdx
                            if (needSetNullIdx < maxIdx) {
                                viewHolderList[needSetNullIdx] = null
                            }
                        } else if (direction < 0) { // 우측으로
                            val needSetNullIdx = position - recyclerDetachBindIdx
                            if (needSetNullIdx >= 0) {
                                viewHolderList[needSetNullIdx] = null
                            }
                        }
                    }

                    viewHolderList[prevPosition]?.also {
                        it.detached()
                    }
                    model.setCalendar(dateList[position])
                    viewHolderList[position]?.also {
                        it.attached()
                    }
                    prevPosition = position
                }
            })
            calendarViewPager.currentItem = maxIdx - 1
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val deleteSet: HashSet<Int>? = data?.getSerializableExtra("deleteSet") as HashSet<Int>?

            viewHolderList[prevPosition]?.also {
                it.checkForDelete(deleteSet)
            }
            viewHolderList.forEachIndexed { idx, holder ->
                if (holder != null && idx != prevPosition) {
                    holder.updateBaseCal(deleteSet)
                }
            }
            model.updateInfoCal(deleteSet)
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onResume() {
        model.resetIsPossibleClick()
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}

class CalendarListAdapter(private val dateList: List<SimpleDate>, private val model: CalendarViewModel, private val viewHolderList: Array<CalendarViewHolder?>) : RecyclerView.Adapter<CalendarViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ActivityCalendarLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        model.setCurrentCalendar(infoCalendarList.find { it.date.getDayOfMonth() == this.clickedDay - cntPrevMonthDay }, binding.root.resources)
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