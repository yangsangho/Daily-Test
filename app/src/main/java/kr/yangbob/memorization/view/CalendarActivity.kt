package kr.yangbob.memorization.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.item_calendar_pager.*
import kr.yangbob.memorization.EXTRA_TO_RESULT_DATESTR
import kr.yangbob.memorization.R
import kr.yangbob.memorization.calendar.BaseCalendar
import kr.yangbob.memorization.databinding.ActivityCalendarBinding
import kr.yangbob.memorization.databinding.ItemCalendarDateBinding
import kr.yangbob.memorization.db.InfoCalendar
import kr.yangbob.memorization.viewmodel.CalendarViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalendarActivity : AppCompatActivity() {

    private val model: CalendarViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityCalendarBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_calendar)
        binding.lifecycleOwner = this
        binding.model = model

        toolBar.title = resources.getString(R.string.calendar_appbar_title)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val yearMonthList = model.yearMonthList()
        calendarViewPager.adapter =
            CalendarPagerAdapter(supportFragmentManager, lifecycle, yearMonthList)
        calendarViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        calendarViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                model.setCalendar(yearMonthList[position])
            }
        })
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

class CalendarPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    private val yearMonthList: List<String>
) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = yearMonthList.size

    override fun createFragment(position: Int): Fragment {
        return CalendarFragment.newInstance(yearMonthList[position])
    }
}

class CalendarFragment : Fragment() {
    private lateinit var baseCalendar: BaseCalendar
    private lateinit var infoCalendarList: List<InfoCalendar>
    private val model: CalendarViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            val yearMonth = bundle.getString("yearMonth")!!
            infoCalendarList = model.getQstCalendarList(yearMonth.toInt())
            baseCalendar = BaseCalendar(yearMonth)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_calendar_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarRecycler.layoutManager = GridLayoutManager(context, BaseCalendar.DAYS_OF_WEEK)
        calendarRecycler.adapter = CalendarRecyclerAdapter(baseCalendar, infoCalendarList, model)
    }

    companion object {
        fun newInstance(yearMonth: String) = CalendarFragment().apply {
            arguments = Bundle().apply {
                putString("yearMonth", yearMonth)
            }
        }
    }
}

class CalendarViewHolder(private val binding: ItemCalendarDateBinding, private val model: CalendarViewModel) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(date: Int, color: Int, infoCalendar: InfoCalendar?) {
        binding.tvDateNum.text = "$date"
        binding.tvDateNum.setTextColor(color)

        if(infoCalendar != null){
            if(infoCalendar.isStartOrToday == null){
                binding.isCompleted = infoCalendar.isCompleted
                binding.dateLayout.setOnClickListener {
                    if(model.checkIsPossibleClick()){
                        val context = binding.root.context
                        context.startActivity(Intent(context, ResultActivity::class.java).apply {
                            putExtra(EXTRA_TO_RESULT_DATESTR, infoCalendar.id)
                        })
                    }
                }
            } else {
                binding.isNeedBackground = true
                binding.isStart = infoCalendar.isStartOrToday
                binding.tvDesc.visibility = View.VISIBLE
                binding.completeIcon.visibility = View.GONE
            }
        }
    }
}

class CalendarRecyclerAdapter(
    private val baseCalendar: BaseCalendar,
    private val infoCalendarList: List<InfoCalendar>,
    private val model: CalendarViewModel
) : RecyclerView.Adapter<CalendarViewHolder>() {
    private val minIdxNextMonthDate = baseCalendar.cntPrevMonthDate + baseCalendar.maxDateCurrentMonth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding: ItemCalendarDateBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_calendar_date,
            parent,
            false
        )
        val itemHeight = parent.measuredHeight / BaseCalendar.LOW_OF_CALENDAR
        binding.dateLayout.layoutParams.height = itemHeight
        return CalendarViewHolder(binding, model)
    }

    override fun getItemCount(): Int = baseCalendar.dateList.size

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        var opacity = 255
        var infoCalendar: InfoCalendar? = null
        val date = baseCalendar.dateList[position]

        if (position < baseCalendar.cntPrevMonthDate || position >= minIdxNextMonthDate) {
            opacity = 80
        } else {
            infoCalendar = infoCalendarList.find { it.date == date }
        }
        val color = when {
            position % BaseCalendar.DAYS_OF_WEEK == 0 -> {
                Color.argb(opacity, 255, 0, 0)
            }
            position % BaseCalendar.DAYS_OF_WEEK == 6 -> {
                Color.argb(opacity, 0, 0, 255)
            }
            else -> {
                Color.argb(opacity, 0, 0, 0)
            }
        }

        holder.onBind(date, color, infoCalendar)
    }
}