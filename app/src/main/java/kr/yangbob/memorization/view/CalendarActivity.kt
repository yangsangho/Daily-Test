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
import kr.yangbob.memorization.adapter.CalendarViewHolder
import kr.yangbob.memorization.databinding.ActivityCalendarBinding
import kr.yangbob.memorization.viewmodel.CalendarViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class CalendarActivity : AppCompatActivity(), CoroutineScope {

    private val model: CalendarViewModel by viewModel()
    private lateinit var viewHolderList: Array<CalendarViewHolder?>
    private lateinit var calendarPagerAdapter: CalendarPagerAdapter
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
            calendarPagerAdapter = CalendarPagerAdapter(dateList, model, viewHolderList)
            viewpager_calendar.adapter = calendarPagerAdapter
            viewpager_calendar.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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
            viewpager_calendar.currentItem = maxIdx - 1
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