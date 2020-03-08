package kr.yangbob.memorization.view

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
import kr.yangbob.memorization.calendar.BaseCalendar
import kr.yangbob.memorization.databinding.ActivityCalendarBinding
import kr.yangbob.memorization.databinding.CalendarLayoutBinding
import kr.yangbob.memorization.db.InfoCalendar
import kr.yangbob.memorization.viewmodel.CalendarViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class CalendarActivity : AppCompatActivity(), CoroutineScope {

    private val model: CalendarViewModel by viewModel()
    private lateinit var viewHolderList: Array<TestViewHolder2?>
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
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

            var prevPosition = 0
            val yearMonthList = model.yearMonthList()
            viewHolderList = arrayOfNulls(yearMonthList.size)
            calendarViewPager.adapter = TestAdapter2(yearMonthList, model, viewHolderList)
            calendarViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewHolderList[prevPosition]?.apply {
                        detached()
                    }
                    model.setCalendar(yearMonthList[position])
                    viewHolderList[position]?.apply {
                        attached()
                    }
                    prevPosition = position
                }
            })
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

class TestAdapter2(private val yearMonthList: List<String>, private val model: CalendarViewModel, private val viewHolderList: Array<TestViewHolder2?>) : RecyclerView.Adapter<TestViewHolder2>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder2 {
        val binding = CalendarLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  TestViewHolder2(binding, model)
    }

    override fun getItemCount(): Int = yearMonthList.size

    override fun onBindViewHolder(holder: TestViewHolder2, position: Int) {
        viewHolderList[position] = holder
        var isLastMonth: Boolean? = null   // true : 마지막날을 click으로 , false : 첫날을 click으로, null : 1일을 click으로
        if(position == yearMonthList.size - 1){
            isLastMonth = true
        } else if(position == 0){
            isLastMonth = false
        }
        holder.bind(yearMonthList[position], isLastMonth)
    }
}

class TestViewHolder2(private val binding: CalendarLayoutBinding, private val model: CalendarViewModel) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var infoCalendarList: List<InfoCalendar>
    private var clickedDay = 0
    private var dayPrefix = 0

    fun bind(yearMonth: String, isLastMonth: Boolean?) {
        infoCalendarList = model.getQstCalendarList(yearMonth.toInt())
        val baseCalendar = BaseCalendar(yearMonth, infoCalendarList)
        dayPrefix = baseCalendar.cntPrevMonthDate
        binding.dayList = baseCalendar.dayList
        binding.holder = this
        this.clickedDay = when(isLastMonth){
            true -> infoCalendarList.last().date
            false -> infoCalendarList.first().date
            else -> 1
        }
        this.clickedDay += dayPrefix
    }

    fun attached(){
        binding.clickedDay = this.clickedDay
        model.setCurrentCalendar(infoCalendarList.find { it.date == this.clickedDay - dayPrefix }, binding.root.resources)
    }

    fun detached(){
        binding.clickedDay = 0
    }

    fun click(infoCalendar: InfoCalendar?){
        infoCalendar?.also { infoCal ->
            binding.clickedDay = dayPrefix + infoCal.date
            model.setCurrentCalendar(infoCal, binding.root.resources)
        }
    }
}