package kr.yangbob.memorization

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.viewpager2.widget.ViewPager2
import kr.yangbob.memorization.data.SimpleDate
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.QstCalendar
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.view.CalendarActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.jetbrains.annotations.NotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.*

@RunWith(AndroidJUnit4::class)
@LargeTest
class CalendarActivityTest : KoinTest {
    @get:Rule
    var activityRule: ActivityTestRule<CalendarActivity> = ActivityTestRule(CalendarActivity::class.java, true, false)

    private val cntForStartDate = 90
    private val cntNullCalendar = 6 // 짝수일 때만 유효
    private val todayDate = SimpleDate.newInstanceToday()
    private val startDate = todayDate.clone().apply {
        addDate(Calendar.DAY_OF_MONTH, -cntForStartDate)
    }
    private var isInit = false
    private val memRepo: MemRepository by inject()

    @Before
    fun before() {
        if (isInit) return
        isInit = true
        makeDbData()
        activityRule.launchActivity(null)
    }

    private fun makeDbData() {
        val baseDate = startDate.clone()

        memRepo.insertQstCalendar(QstCalendar(baseDate))
        makeNullCalendar(baseDate)
        makeFakeCalendar(baseDate)
    }

    private fun makeNullCalendar(baseDate: SimpleDate) {
        for (i in 1..cntNullCalendar) {
            baseDate.addDate(Calendar.DAY_OF_MONTH, 1)

            memRepo.insertQstCalendar(QstCalendar(baseDate))
        }
        insertNewQst(baseDate, cntNullCalendar)
    }

    private fun makeFakeCalendar(baseDate: SimpleDate) {
        for (day in (cntNullCalendar + 1)..cntForStartDate) {
            baseDate.addDate(Calendar.DAY_OF_MONTH, 1)

            if (day % 2 == 1)
                insertOddCalendarForNotCompleted(baseDate)
            else {
                insertEvenCalendarForCompleted(baseDate)
                insertNewQst(baseDate, day)
            }
        }
    }

    private fun insertOddCalendarForNotCompleted(baseDate: SimpleDate) {
        val testList: List<Qst> = memRepo.getNeedTestList(baseDate)

        memRepo.insertQstCalendar(QstCalendar(baseDate, if (testList.isEmpty()) null else false))

        testList.forEach {
            memRepo.insertQstRecord(QstRecord(it.id!!, baseDate, it.cur_stage + 1))
        }
    }

    private fun insertEvenCalendarForCompleted(baseDate: SimpleDate) {
        val testList: List<Qst> = memRepo.getNeedTestList(baseDate)

        memRepo.insertQstCalendar(QstCalendar(baseDate, if (testList.isEmpty()) null else true))

        testList.forEach {
            memRepo.insertQstRecord(QstRecord(it.id!!, baseDate, it.cur_stage + 1, true))
            memRepo.insertQst(it.apply {
                cur_stage += 1
                next_test_date = baseDate.clone().apply {
                    addDate(Calendar.DAY_OF_MONTH, STAGE_LIST[it.cur_stage + 1].nextTest)
                }
            })
        }
    }

    private fun insertNewQst(baseDate: SimpleDate, seed: Int) {
        memRepo.insertQst(Qst("문제$seed", "답$seed", baseDate, baseDate.clone().apply { addDate(Calendar.DAY_OF_MONTH, 1) }, id = seed))
    }

    @Test
    fun pagerCountTest() {
        Thread.sleep(4000)
        val cntPager = startDate.getDateDiff(todayDate, Calendar.MONTH)

        val viewPager = onView(withId(R.id.viewpager_calendar))
        viewPager.check(matches(inPosition(cntPager)))
        for (position in (cntPager - 1) downTo 0) {
            viewPager.perform(ViewActions.swipeRight()).check(matches(inPosition(position)))
        }
    }

    @NotNull
    private fun inPosition(position: Int): Matcher<View> {
        return object : BoundedMatcher<View, ViewPager2>(ViewPager2::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("in Position: $position")
            }

            override fun matchesSafely(item: ViewPager2): Boolean {
                val currentPosition = item.currentItem
                return currentPosition == position
            }
        }
    }
}