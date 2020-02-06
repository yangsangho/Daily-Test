package kr.yangbob.memorization.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
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
import kr.yangbob.memorization.R
import kr.yangbob.memorization.calendar.BaseCalendar
import kr.yangbob.memorization.databinding.ActivityCalendarBinding
import kr.yangbob.memorization.databinding.ItemCalendarDateBinding
import kr.yangbob.memorization.db.QstCalendar
import kr.yangbob.memorization.viewmodel.CalendarViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private val model: CalendarViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityCalendarBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_calendar)
        binding.lifecycleOwner = this
        binding.model = model

        val calendarList = model.getCalendarList()

        calendarViewPager.adapter =
            CalendarPagerAdapter(supportFragmentManager, lifecycle, calendarList)
        calendarViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        calendarViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                model.setCalendar(calendarList[position])
            }
        })
    }
}

class CalendarPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    private val calendarList: List<Calendar>
) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = calendarList.size

    override fun createFragment(position: Int): Fragment {
        return CalendarFragment.newInstance(calendarList[position])
    }
}

class CalendarFragment : Fragment() {
    private lateinit var baseCalendar: BaseCalendar
    private lateinit var qstCalendarList: List<QstCalendar>
    private val model: CalendarViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            val cal = bundle.getSerializable("calendar") as Calendar
            qstCalendarList = model.getQstCalendarList(cal)
            baseCalendar = BaseCalendar(cal)
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
        calendarRecycler.adapter = CalendarRecyclerAdapter(baseCalendar, qstCalendarList)
    }

    companion object {
        fun newInstance(calendar: Calendar) = CalendarFragment().apply {
            arguments = Bundle().apply {
                putSerializable("calendar", calendar)
            }
        }
    }
}

class CalendarViewHolder(private val binding: ItemCalendarDateBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(date: Int, color: Int, isCompleted: Boolean?) {
        binding.tvDateNum.text = "$date"
        binding.tvDateNum.setTextColor(color)
        binding.isCompleted = isCompleted
    }
}

class CalendarRecyclerAdapter(
    baseCalendar: BaseCalendar,
    private val qstCalendarList: List<QstCalendar>
) :
    RecyclerView.Adapter<CalendarViewHolder>() {
    private val dateList = baseCalendar.getDateList()
    private val maxIdxPrevMonthDate = baseCalendar.cntPrevMonthDate - 1
    private val minIdxNextMonthDate =
        baseCalendar.cntPrevMonthDate + baseCalendar.maxDateCurrentMonth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding: ItemCalendarDateBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_calendar_date,
            parent,
            false
        )
        val itemHeight = parent.measuredHeight / BaseCalendar.LOW_OF_CALENDAR
        binding.dateLayout.layoutParams.height = itemHeight
        return CalendarViewHolder(binding)
    }

    override fun getItemCount(): Int = dateList.size

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        var opacity = 255
        var isCompleted: Boolean? = null
        val date = dateList[position]
        if (position <= maxIdxPrevMonthDate || position >= minIdxNextMonthDate) {
            opacity = 80
        } else {
            qstCalendarList.find { it.id.substring(8, 10).toInt() == date }?.let {
                isCompleted = it.test_completion
            }
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

        holder.onBind(date, color, isCompleted)
    }
}