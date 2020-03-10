package kr.yangbob.memorization.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
import kr.yangbob.memorization.calendar.BaseCalendar
import kr.yangbob.memorization.databinding.ActivityCalendarBinding
import kr.yangbob.memorization.databinding.CalendarLayoutBinding
import kr.yangbob.memorization.db.InfoCalendar
import kr.yangbob.memorization.viewmodel.CalendarViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class CalendarActivity : AppCompatActivity(), CoroutineScope {

    private val model: CalendarViewModel by viewModel()
    private lateinit var viewHolderList: Array<CalendarViewHolder?>
    private lateinit var calendarAdapter: CalendarAdapter
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
        val binding: ActivityCalendarBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_calendar)
        binding.lifecycleOwner = this
        binding.model = model

        job = Job()
        launch {
            toolBar.title = getString(R.string.calendar_appbar_title)
            setSupportActionBar(toolBar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            val yearMonthList = model.yearMonthList()
            val maxIdx = yearMonthList.size
            val recyclerDetachBindIdx = 3
            viewHolderList = arrayOfNulls(maxIdx)
            calendarAdapter = CalendarAdapter(yearMonthList, model, viewHolderList)
            calendarViewPager.adapter = calendarAdapter
            calendarViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    Log.i("TEST", "onPageSelected : $position")
                    if(maxIdx > 4){ // item이 4개 이하면, onbind를 반복 안하더라
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
                    model.setCalendar(yearMonthList[position])
                    viewHolderList[position]?.also {
                        it.attached()
                    }
                    prevPosition = position
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val deleteSet: HashSet<String>? = data?.getSerializableExtra("deleteSet") as HashSet<String>?

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

class CalendarAdapter(private val yearMonthList: List<String>, private val model: CalendarViewModel, private val viewHolderList: Array<CalendarViewHolder?>) : RecyclerView.Adapter<CalendarViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = CalendarLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding, model)
    }

    override fun getItemCount(): Int = yearMonthList.size

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        Log.i("TEST", "onBind : $position")
        viewHolderList[position] = holder
        var isLastMonth: Boolean? = null   // true : 마지막날을 click으로 , false : 첫날을 click으로, null : 1일을 click으로
        if (position == yearMonthList.size - 1) {
            isLastMonth = true
        } else if (position == 0) {
            isLastMonth = false
        }
        holder.bind(yearMonthList[position], isLastMonth)
    }
}

class CalendarViewHolder(private val binding: CalendarLayoutBinding, private val model: CalendarViewModel) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var infoCalendarList: List<InfoCalendar>
    private lateinit var baseCalendar: BaseCalendar
    private var clickedDay = 0
    private var dayPrefix = 0

    fun bind(yearMonth: String, isLastMonth: Boolean?) {
        infoCalendarList = model.getInfoCalendarList(yearMonth.toInt())
        baseCalendar = BaseCalendar(yearMonth, infoCalendarList)
        dayPrefix = baseCalendar.cntPrevMonthDate
        binding.dayList = baseCalendar.dayList
        binding.holder = this
        this.clickedDay = when (isLastMonth) {
            true -> infoCalendarList.last().date
            false -> infoCalendarList.first().date
            else -> 1
        }
        this.clickedDay += dayPrefix
    }

    fun checkForDelete(deleteSet: HashSet<String>?) {
        model.setCurrentCalendar(infoCalendarList.find { it.date == this.clickedDay - dayPrefix }, binding.root.resources)
        updateBaseCal(deleteSet)
    }

    fun updateBaseCal(deleteSet: HashSet<String>?) {
        baseCalendar.dayList.forEach {
            it.infoCalendar?.apply {
                deleteSet?.also { set ->
                    if (set.contains(id)) {
                        val newValue = model.getCalTestComplete(id)
                        if (isCompleted != newValue) {
                            isCompleted = newValue
                            binding.dayList = baseCalendar.dayList
                        }
                    }
                }
            }
        }
    }

    fun attached() {
        binding.clickedDay = this.clickedDay
        model.setCurrentCalendar(infoCalendarList.find { it.date == this.clickedDay - dayPrefix }, binding.root.resources)
    }

    fun detached() {
        binding.clickedDay = 0
    }

    fun click(infoCalendar: InfoCalendar?) {
        infoCalendar?.also { infoCal ->
            this.clickedDay = dayPrefix + infoCal.date
            binding.clickedDay = this.clickedDay
            model.setCurrentCalendar(infoCal, binding.root.resources)
        }
    }
}