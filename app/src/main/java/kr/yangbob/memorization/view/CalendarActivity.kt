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
import kr.yangbob.memorization.R
import kr.yangbob.memorization.calendar.BaseCalendar
import kr.yangbob.memorization.databinding.ActivityCalendarBinding
import kr.yangbob.memorization.databinding.CalendarLayoutBinding
import kr.yangbob.memorization.db.InfoCalendar
import kr.yangbob.memorization.viewmodel.CalendarViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalendarActivity : AppCompatActivity() {

    private val model: CalendarViewModel by viewModel()
    private lateinit var viewHolderList: Array<TestViewHolder2?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        val binding: ActivityCalendarBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_calendar)
        binding.lifecycleOwner = this
        binding.model = model

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

//class TestAdapter(fm: FragmentManager, lifecycle: Lifecycle, private val yearMonthList: List<String>) : FragmentStateAdapter(fm, lifecycle) {
//    override fun getItemCount(): Int = yearMonthList.size
//
//    override fun createFragment(position: Int): Fragment {
//        return TestFragment.newInstance(yearMonthList[position])
//    }
//}
//
//class TestFragment : Fragment() {
//    private lateinit var baseCalendar: BaseCalendar
//    private lateinit var infoCalendarList: List<InfoCalendar>
//    private val model: CalendarViewModel by sharedViewModel()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let { bundle ->
//            val yearMonth = bundle.getString("yearMonth")!!
//            infoCalendarList = model.getQstCalendarList(yearMonth.toInt())
//            baseCalendar = BaseCalendar(yearMonth)
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val binding = CalendarLayoutBinding.inflate(inflater, container, false)
//        binding.baseCalendar = baseCalendar
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//    }
//
//    companion object {
//        fun newInstance(yearMonth: String) = TestFragment().apply {
//            arguments = Bundle().apply {
//                putString("yearMonth", yearMonth)
//            }
//        }
//    }
//}


//class CalendarPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private val yearMonthList: List<String>, private val itemHeight: Int) : FragmentStateAdapter(fm, lifecycle) {
//    override fun getItemCount(): Int = yearMonthList.size
//
//    override fun createFragment(position: Int): Fragment {
//        return CalendarFragment.newInstance(yearMonthList[position], itemHeight)
//    }
//}
//
//class CalendarFragment : Fragment() {
//    private lateinit var baseCalendar: BaseCalendar
//    private lateinit var infoCalendarList: List<InfoCalendar>
//    private var itemHeight: Int = 0
//    private val model: CalendarViewModel by sharedViewModel()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let { bundle ->
//            val yearMonth = bundle.getString("yearMonth")!!
//            itemHeight = bundle.getInt("itemHeight")
//            Log.i("TEST", "Fragment onCreate() : $yearMonth, itemHeight = $itemHeight")
//            infoCalendarList = model.getQstCalendarList(yearMonth.toInt())
//            baseCalendar = BaseCalendar(yearMonth)
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.item_calendar_pager, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        calendarRecycler.adapter = CalendarRecyclerAdapter(baseCalendar, infoCalendarList, model, itemHeight)
//    }
//
//    companion object {
//        fun newInstance(yearMonth: String, itemHeight: Int) = CalendarFragment().apply {
//            arguments = Bundle().apply {
//                putString("yearMonth", yearMonth)
//                putInt("itemHeight", itemHeight)
//                Log.i("TEST", "Fragment newInstance() : $yearMonth, itemHeight = $itemHeight")
//            }
//        }
//    }
//}

//class CalPagerAdapter(private val yearMonthList: List<String>, private val model: CalendarViewModel, private val itemHeight: Int) : RecyclerView.Adapter<CalPagerViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalPagerViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_pager, parent, false)
//        return  CalPagerViewHolder(view, model, itemHeight)
//    }
//
//    override fun getItemCount(): Int = yearMonthList.size
//
//    override fun onBindViewHolder(holder: CalPagerViewHolder, position: Int) {
//        holder.bind(yearMonthList[position])
//    }
//}
//
//class CalPagerViewHolder(view: View, private val model: CalendarViewModel, private val itemHeight: Int) : RecyclerView.ViewHolder(view) {
//    private val calendarRecycler = view.findViewById<RecyclerView>(R.id.calendarRecycler)
//    private lateinit var baseCalendar: BaseCalendar
//    private lateinit var infoCalendarList: List<InfoCalendar>
//
//    fun bind(yearMonth: String) {
//        infoCalendarList = model.getQstCalendarList(yearMonth.toInt())
//        baseCalendar = BaseCalendar(yearMonth)
//        calendarRecycler.adapter = CalendarRecyclerAdapter(baseCalendar, infoCalendarList, model, itemHeight)
//    }
//}
//
//class CalendarViewHolder(private val binding: ItemCalendarDateBinding, private val model: CalendarViewModel) : RecyclerView.ViewHolder(binding.root) {
//
//    fun onBind(date: Int, color: Int, infoCalendar: InfoCalendar?) {
//        binding.tvDateNum.text = "$date"
//        binding.tvDateNum.setTextColor(color)
//
//        if (infoCalendar != null) {
//            if (infoCalendar.isStartOrToday == null) {
//                binding.completeIcon.visibility = View.VISIBLE
//                binding.isCompleted = infoCalendar.isCompleted
//                binding.dateLayout.setOnClickListener {
//                    if (model.checkIsPossibleClick()) {
//                        val context = binding.root.context
//                        context.startActivity(Intent(context, ResultActivity::class.java).apply {
//                            putExtra(EXTRA_TO_RESULT_DATESTR, infoCalendar.id)
//                        })
//                    }
//                }
//            } else {
//                binding.completeIcon.visibility = View.GONE
//                binding.tvDesc.visibility = View.VISIBLE
//                binding.isNeedBackground = true
//                binding.isStart = infoCalendar.isStartOrToday
//            }
//        }
//    }
//}
//
//class CalendarRecyclerAdapter(private val baseCalendar: BaseCalendar, private val infoCalendarList: List<InfoCalendar>,
//                              private val model: CalendarViewModel, private val itemHeight: Int) : RecyclerView.Adapter<CalendarViewHolder>() {
//    private val minIdxNextMonthDate =
//            baseCalendar.cntPrevMonthDate + baseCalendar.maxDateCurrentMonth
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
//        val binding: ItemCalendarDateBinding = DataBindingUtil.inflate(
//                LayoutInflater.from(parent.context),
//                R.layout.item_calendar_date,
//                parent,
//                false
//        )
//        Log.i("TEST", "Recycler Adapter : itemHeight = $itemHeight")
//        binding.dateLayout.layoutParams.height = itemHeight
//        return CalendarViewHolder(binding, model)
//    }
//
//    override fun getItemCount(): Int = baseCalendar.dateList.size
//
//    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
//        var opacity = 255
//        var infoCalendar: InfoCalendar? = null
//        val date = baseCalendar.dateList[position]
//
//        if (position < baseCalendar.cntPrevMonthDate || position >= minIdxNextMonthDate) {
//            opacity = 80
//        } else {
//            infoCalendar = infoCalendarList.find { it.date == date }
//        }
//        val color = when {
//            position % BaseCalendar.DAYS_OF_WEEK == 0 -> {
//                Color.argb(opacity, 255, 0, 0)
//            }
//            position % BaseCalendar.DAYS_OF_WEEK == 6 -> {
//                Color.argb(opacity, 0, 0, 255)
//            }
//            else -> {
//                Color.argb(opacity, 0, 0, 0)
//            }
//        }
//
//        holder.onBind(date, color, infoCalendar)
//    }
//}