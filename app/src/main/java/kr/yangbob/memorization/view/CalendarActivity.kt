package kr.yangbob.memorization.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kr.yangbob.memorization.R
import kr.yangbob.memorization.adapter.CalendarPagerAdapter
import kr.yangbob.memorization.databinding.ActivityCalendarBinding
import kr.yangbob.memorization.viewmodel.CalendarViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class CalendarActivity : AppCompatActivity(), CoroutineScope {
    private val model: CalendarViewModel by viewModel()
    private var currentViewPagerPosition = 0
    private lateinit var job: Job
    private lateinit var calendarPagerAdapter: CalendarPagerAdapter
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            model.setPortrait(false)
        } else {
            model.setPortrait(true)
        }

        val binding: ActivityCalendarBinding = DataBindingUtil.setContentView(this, R.layout.activity_calendar)
        binding.lifecycleOwner = this
        binding.model = model

        toolBar.title = getString(R.string.calendar_appbar_title)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        job = Job()
        launch {
            val dateList = model.getDateList()
            val maxIdx = dateList.size

            calendarPagerAdapter = CalendarPagerAdapter(dateList, model)
            viewpager_calendar.adapter = calendarPagerAdapter

            viewpager_calendar.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    currentViewPagerPosition = position
                    model.setYearMonthText(dateList[position])
                }
            })

            viewpager_calendar.currentItem = maxIdx - 1
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            (data?.getSerializableExtra("deleteSet") as HashSet<Int>?)?.let { dateIntSetForDelete ->
                calendarPagerAdapter.deleteProcess(dateIntSetForDelete, currentViewPagerPosition)
                model.updateInfoCal(dateIntSetForDelete)
            }
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