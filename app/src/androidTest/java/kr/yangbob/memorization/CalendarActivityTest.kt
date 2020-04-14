package kr.yangbob.memorization

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.viewpager2.widget.ViewPager2
import kr.yangbob.memorization.data.SimpleDate
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.QstCalendar
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.view.CalendarActivity
import kr.yangbob.memorization.view.ResultActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
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

    private val context = ApplicationProvider.getApplicationContext<Context>()

    /* cntForStartDate와 cntNullCalendar 변수로 날짜 데이터 변경해서 테스트 가능 */
    private val cntForStartDate = 90
    private val cntNullCalendar = 5

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
                insertCalendarForNotCompleted(baseDate)
            else {
                insertCalendarForCompleted(baseDate)
                insertNewQst(baseDate, day)
            }
        }
    }

    private fun insertCalendarForNotCompleted(baseDate: SimpleDate) {
        val testList: List<Qst> = memRepo.getNeedTestList(baseDate)

        memRepo.insertQstCalendar(QstCalendar(baseDate, if (testList.isEmpty()) null else false))

        testList.forEach {
            memRepo.insertQstRecord(QstRecord(it.id!!, baseDate, it.cur_stage + 1))
        }
    }

    private fun insertCalendarForCompleted(baseDate: SimpleDate) {
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
        val cntPager = startDate.getDateDiff(todayDate, Calendar.MONTH)
        val viewPager = onView(withId(R.id.viewpager_calendar))

        for (position in cntPager downTo 0) {
            if (position != cntPager) viewPager.perform(ViewActions.swipeRight())
            viewPager.check(matches(inPosition(position)))
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

    @Test
    fun monthYearCheckTest() {
        val cntPager = startDate.getDateDiff(todayDate, Calendar.MONTH)
        val viewPager = onView(withId(R.id.viewpager_calendar))
        val tvMonth = onView(withId(R.id.tv_month))
        val tvYear = onView(withId(R.id.tv_year))
        val cloneDate = todayDate.clone()

        for (i in 0..cntPager) {
            if (i != 0) {
                viewPager.perform(swipeRight())
                cloneDate.addDate(Calendar.MONTH, -1)
            }
            tvYear.check(matches(withText(getYear(cloneDate))))
            tvMonth.check(matches(withText(getMonth(cloneDate))))
        }
    }

    private fun getYear(date: SimpleDate): String = String.format(context.resources.getString(R.string.calendar_year), date.getYear())

    private fun getMonth(date: SimpleDate): String = context.resources.getStringArray(R.array.calendar_month)[date.getMonth() - 1]

    @Test
    fun clickDetailBtnTest() {
        val btnMoveToDetail = onView(withId(R.id.btn_move_to_detail))

        if (cntForStartDate > 0) {
            btnMoveToDetail.check(matches(isClickable()))
            btnMoveToDetail.perform(click())

            var curActivity = getCurrentActivity()
            MatcherAssert.assertThat(curActivity, Matchers.instanceOf(ResultActivity::class.java))

            pressBack()

            curActivity = getCurrentActivity()
            MatcherAssert.assertThat(curActivity, Matchers.instanceOf(CalendarActivity::class.java))
        } else {
            btnMoveToDetail.check(matches(not(isClickable())))
        }
    }

    private fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            run {
                currentActivity = ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(Stage.RESUMED).elementAtOrNull(0)
            }
        }
        return currentActivity
    }

    private enum class DateType {
        TODAY, START, NORMAL, ONLY_ONE
    }

    private var isCompletedForCheckDayBackground = if (cntNullCalendar % 2 == 0) true else false
    private var cntNullCalendarForCounting = 0
    private var isNotStartDayBackgroundCheck = true

    @Test
    fun dayBackgroundTest() {
        val cntPager = startDate.getDateDiff(todayDate, Calendar.MONTH)
        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, startDate.getYear())
            set(Calendar.MONTH, startDate.getMonth() - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val viewPager = onView(withId(R.id.viewpager_calendar))

        for (i in 0 until cntPager) viewPager.perform(swipeRight())

        if (cntPager == 0)
            checkDayBackground(calendar, DateType.ONLY_ONE)
        else
            for (position in 0..cntPager) {
                if (position != 0) viewPager.perform(ViewActions.swipeLeft())
                Thread.sleep(500)

                val dateType = when (position) {
                    0 -> DateType.START
                    cntPager -> DateType.TODAY
                    else -> DateType.NORMAL
                }
                checkDayBackground(calendar, dateType)
            }
    }

    private fun checkDayBackground(calendar: Calendar, dateType: DateType) {
        if (dateType != DateType.START && dateType != DateType.ONLY_ONE) calendar.add(Calendar.MONTH, 1)
        val cntPrevMonthDay = calendar.get(Calendar.DAY_OF_WEEK) - 1

        repeatCheckDayBackground(cntPrevMonthDay, getStartDay(dateType), getEndDay(calendar, dateType))
    }

    private fun getStartDay(dateType: DateType) = when (dateType) {
        DateType.TODAY, DateType.NORMAL -> 0
        DateType.START, DateType.ONLY_ONE -> startDate.getDayOfMonth() - 1
    }

    private fun getEndDay(calendar: Calendar, dateType: DateType) = when (dateType) {
        DateType.TODAY, DateType.ONLY_ONE -> todayDate.getDayOfMonth()
        DateType.START, DateType.NORMAL -> calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun repeatCheckDayBackground(cntPrevMonthDay: Int, startDay: Int, endDay: Int) {
        for (day in 0 until cntPrevMonthDay + startDay) {
            getBackgroundView(day).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
        for (day in cntPrevMonthDay + startDay until cntPrevMonthDay + endDay) {
            val tagValue = getDayBackgroundTagValue()
            getBackgroundView(day).check(matches(allOf(withTagValue(`is`(tagValue)), isDisplayed())))
        }
        for (day in cntPrevMonthDay + endDay until 42) {
            getBackgroundView(day).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }

    private fun getBackgroundView(day: Int) = onView(
            allOf(
                    withId(R.id.iv_day_background),
                    withParent(
                            allOf(
                                    withParent(
                                            allOf(
                                                    withId(R.id.layout_days),
                                                    isDisplayed()
                                            )
                                    ),
                                    withParentIndex(day)
                            )
                    )
            )
    )

    private fun getDayBackgroundTagValue(): Int {
        return if (isNotStartDayBackgroundCheck) {
            isNotStartDayBackgroundCheck = false
            R.color.colorAccent
        } else if (cntNullCalendarForCounting < cntNullCalendar) {
            cntNullCalendarForCounting++
            0
        } else {
            isCompletedForCheckDayBackground = !isCompletedForCheckDayBackground
            if (isCompletedForCheckDayBackground) {
                android.R.color.holo_green_light
            } else {
                android.R.color.holo_red_light
            }
        }
    }
}
