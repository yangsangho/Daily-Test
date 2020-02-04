package kr.yangbob.memorization.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kr.yangbob.memorization.*
import kr.yangbob.memorization.databinding.DashboardModuleBinding
import kr.yangbob.memorization.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val logTag = "MainActivity"
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTestChkAlarm(this)
        setContentView(R.layout.activity_main)

        // ToolBar 설정
        toolBar.title = resources.getString(R.string.app_name)
        setSupportActionBar(toolBar)
        supportActionBar?.setIcon(R.drawable.ic_appbar_icon)

        // Viewpager 및 TabLayout 설정
        mainViewPager.adapter = MainPagerFragmentAdpater(lifecycle, supportFragmentManager)
        mainViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.getTabAt(position)?.select()
            }
        })
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                mainViewPager.currentItem = tab?.position ?: 0
            }
        })

        // TEST 코드
//        val testList = model.getAllRecord()
//        testList.forEach { Log.i(logTag, "<RECORD>GET_ALL : $it") }
//        val testList2 = model.getAllCalendar()
//        testList2.forEach { Log.i(logTag, "<CALENDAR>GET_ALL : $it") }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_search -> {
            true
        }
        R.id.action_write -> {
            startActivity(Intent(this, AddActivity::class.java))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, R.string.main_toast_oneMore_back, Toast.LENGTH_SHORT).show()
        Handler().postDelayed(
            { doubleBackToExitPressedOnce = false },
            2000
        )
    }

    // 전체 문제 쪽에서 버튼 눌러서 activity 갔다가 돌아왔을 때 전체문제 탭이 선택되도록
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        tabLayout.getTabAt(1)?.select()
        mainViewPager.currentItem = 1
    }
}

class MainPagerFragmentAdpater(mainLifeCycle: Lifecycle, fm: FragmentManager) :
    FragmentStateAdapter(fm, mainLifeCycle) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment = PagerFragment.newInstance(position == 0)
}

class PagerFragment : Fragment() {
    private val model: MainViewModel by viewModel()
    private lateinit var binding: DashboardModuleBinding
    private var isInit = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isInit = false
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dashboard_module,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            val isToday = bundle.getBoolean("isToday")
            binding.isToday = isToday
            binding.model = model
            binding.fragment = this
            if (isToday) {
                binding.dashboardChart.setCount(7)
                val observeList = model.getQstRecordList()
                observeList.observe(viewLifecycleOwner, Observer { rawList ->
                    if (!isInit) {
                        isInit = true
                        model.setTodayTestCount()
                        if (rawList.isNotEmpty()) {
                            val map = rawList.groupBy { qstRecord -> qstRecord.challenge_stage }
                                .mapValues { it.value.size }.toMutableMap()
                            STAGE_LIST.filter { it.ordinal > 0 }
                                .forEach { if (!map.containsKey(it.ordinal)) map[it.ordinal] = 0 }
                            val reviewCnt = map[Stage.REVIEW.ordinal]
                            map.remove(Stage.REVIEW.ordinal)
                            map[Stage.AFTER_MONTH.ordinal] =
                                (map[Stage.AFTER_MONTH.ordinal] ?: 0) + (reviewCnt ?: 0)
                            binding.dashboardChart.setDataList(map.toSortedMap().values.toList())
                        }
                    }
                    if (model.setTodayCardData()) {
                        binding.dashboardBtn1.isEnabled = false
                        binding.dashboardBtn1.setBackgroundColor(Color.DKGRAY)
                    }
                })
            } else {
                binding.dashboardChart.setCount(8)
                val observeList = model.getQstList()
                observeList.observe(viewLifecycleOwner, Observer { rawList ->
                    model.setEntireCardData()
                    if (rawList.isNotEmpty()) {
                        val map =
                            rawList.groupBy { qst -> qst.cur_stage }.mapValues { it.value.size }
                                .toMutableMap()
                        STAGE_LIST.filter { it.ordinal < 8 }
                            .forEach { if (!map.containsKey(it.ordinal)) map[it.ordinal] = 0 }
                        binding.dashboardChart.setDataList(map.toSortedMap().values.toList())
                    }
                    model.setTestCompletionRate()
                })
            }
        }
    }

    companion object {
        fun newInstance(isToday: Boolean): PagerFragment {
            val bundle = Bundle()
            bundle.putBoolean("isToday", isToday)
            return PagerFragment().apply { arguments = bundle }
        }
    }

    fun clickTestBtn(view: View) {
        startActivity(Intent(context, TestActivity::class.java))
    }

    fun clickEntireList(view: View) {
        startActivityForResult(Intent(context, EntireActivity::class.java), 0)
    }

    fun clickTodayRecord(view: View) {
        startActivity(Intent(context, ResultActivity::class.java).apply {
            putExtra(MAIN_TO_RESULT_DATESTR, model.getTodayDateStr())
        })
    }

    fun clickEntireRecord(view: View) {

    }
}