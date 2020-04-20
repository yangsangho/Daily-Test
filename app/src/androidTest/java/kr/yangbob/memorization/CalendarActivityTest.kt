package kr.yangbob.memorization

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
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
import kr.yangbob.memorization.db.MemDatabase
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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.get
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
    private val startNotNullDate = startDate.clone().apply {
        addDate(Calendar.DAY_OF_MONTH, cntNullCalendar + 1)
    }
    private val cntPager = startDate.getDateDiff(todayDate, Calendar.MONTH)
    private var isInit = false
    private val memRepo: MemRepository by inject()

    @Before
    fun before() {
//        if (isInit) return
//        isInit = true
        makeDbData()
        activityRule.launchActivity(null)
        if (isLandScape) activityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

   @After
   fun after(){
       val db: MemDatabase = get()
       db.clearAllTables()
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
            getDayView(R.id.iv_day_background, day).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
        for (day in cntPrevMonthDay + startDay until cntPrevMonthDay + endDay) {
            val tagValue = getDayBackgroundTagValue()
            getDayView(R.id.iv_day_background, day).check(matches(allOf(withTagValue(`is`(tagValue)), isDisplayed())))
        }
        for (day in cntPrevMonthDay + endDay until 42) {
            getDayView(R.id.iv_day_background, day).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }

    private fun getDayView(resourceId: Int, day: Int) = onView(
            allOf(
                    withId(resourceId),
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

    @Test
    fun dayTextTest() {
        val viewPager = onView(withId(R.id.viewpager_calendar))
        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, todayDate.getYear())
            set(Calendar.MONTH, todayDate.getMonth() - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val prevCalendar = (calendar.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }

        for (i in 0..cntPager) {
            if (i != 0) {
                viewPager.perform(swipeRight())
                calendar.add(Calendar.MONTH, -1)
                prevCalendar.add(Calendar.MONTH, -1)
            }
            Thread.sleep(500)
            repeatCheckDayText(calendar, prevCalendar)
        }
    }

    private fun repeatCheckDayText(calendar: Calendar, prevCalendar: Calendar) {
        val cntPrevMonthDay = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val curMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        var prevStartDay = prevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) - cntPrevMonthDay + 1
        var curStartDay = 1
        var nextStartDay = 1

        for (day in 0 until cntPrevMonthDay) {
            getDayView(R.id.tv_day, day).check(matches(withText("${prevStartDay++}")))
        }
        for (day in cntPrevMonthDay until cntPrevMonthDay + curMaxDay) {
            getDayView(R.id.tv_day, day).check(matches(withText("${curStartDay++}")))
        }
        for (day in cntPrevMonthDay + curMaxDay until 42) {
            getDayView(R.id.tv_day, day).check(matches(withText("${nextStartDay++}")))
        }
    }

    private data class DaysForTest(
            val cntPrevMonthDay: Int,
            val startDay: Int,
            val endDay: Int,
            val curClickedDay: Int
    )

    @Test
    fun dayClickTest() {
        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, todayDate.getYear())
            set(Calendar.MONTH, todayDate.getMonth() - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        if (cntPager == 0) checkDayClickForOnePage(calendar)
        else checkDayClickForMultiplePage(calendar)
    }

    private fun checkDayClickForOnePage(calendar: Calendar) {
        val days = createDaysForClickTest(calendar, DateType.ONLY_ONE)
        val dateForRecord = startDate.clone()

        repeatCheckDayClick(days, dateForRecord)
    }

    private fun checkDayClickForMultiplePage(calendar: Calendar) {
        var dateType: DateType
        var dateForRecord = todayDate.clone().apply {
            setDate(Calendar.DAY_OF_MONTH, 1)
        }
        val viewPager = onView(withId(R.id.viewpager_calendar))

        for (position in 0..cntPager) {
            if (position != 0) {
                viewPager.perform(swipeRight())
                calendar.add(Calendar.MONTH, -1)
            }
            Thread.sleep(500)

            when (position) {
                0 -> {
                    dateType = DateType.TODAY
                }
                cntPager -> {
                    dateType = DateType.START
                    dateForRecord = startDate
                }
                else -> {
                    dateType = DateType.NORMAL
                    dateForRecord.addDate(Calendar.MONTH, -1)
                }
            }

            repeatCheckDayClick(createDaysForClickTest(calendar, dateType), dateForRecord)
        }
    }

    private fun createDaysForClickTest(calendar: Calendar, dateType: DateType): DaysForTest {
        val cntPrevMonthDay = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val startDay = getStartDay(dateType)
        val endDay = getEndDay(calendar, dateType)
        val curClickedDay = getClickedDay(dateType)
        return DaysForTest(cntPrevMonthDay, startDay, endDay, curClickedDay)
    }

    private fun getClickedDay(dateType: DateType) = when (dateType) {
        DateType.TODAY, DateType.ONLY_ONE -> todayDate.getDayOfMonth()
        DateType.START -> startDate.getDayOfMonth()
        DateType.NORMAL -> 1
    }

    private fun repeatCheckDayClick(days: DaysForTest, date: SimpleDate) {
        var prevClickedDay = days.curClickedDay + days.cntPrevMonthDay - 1
        val dateForRecord = date.clone()

        for (day in 0 until 42) {
            val tagValueAfterClick =
                    day in days.cntPrevMonthDay + days.startDay until days.cntPrevMonthDay + days.endDay

            val clickDayView = getDayLayoutView(day)
            clickDayView.perform(click())
            clickDayView.check(matches(withTagValue(`is`(tagValueAfterClick))))

            if (prevClickedDay != day)
                getDayLayoutView(prevClickedDay).check(matches(withTagValue(`is`(!tagValueAfterClick))))
            if (tagValueAfterClick) {
                prevClickedDay = day
                checkRecord(dateForRecord)
                dateForRecord.addDate(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    private fun getDayLayoutView(day: Int) = onView(
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

    private data class RecordData(
            val cntQst: Int,
            val progressRate: Float,
            val correctRate: Float
    )

    private fun checkRecord(date: SimpleDate) {
        val recordView = onView(withId(R.id.tv_record))
        val btnMoveToDetail = onView(withId(R.id.btn_move_to_detail))

        if (date == startDate) {
            recordView.check(matches(withText(context.getString(R.string.calendar_start_day))))
            btnMoveToDetail.check(matches(not(isClickable())))
        } else if (date > startDate && date < startNotNullDate) {
            recordView.check(matches(withText(context.getString(R.string.status_msg_no_test))))
            btnMoveToDetail.check(matches(not(isClickable())))
        } else {
            val recordData = getRecordData(date)
            recordView.check(matches(withText(String.format(
                    context.getString(if (isLandScape) R.string.result_info_format_land else R.string.result_info_format),
                    recordData.cntQst,
                    recordData.progressRate,
                    recordData.correctRate))))
            btnMoveToDetail.check(matches(isClickable()))
        }
    }

    private fun getRecordData(date: SimpleDate): RecordData {
        val recordList = memRepo.getAllRecordFromDate(date)
        val cntQst = recordList.size
        val cntSolved = recordList.count { it.is_correct != null }
        val cntCorrect = recordList.count { it.is_correct == true }

        val progressRate = if (cntQst > 0) cntSolved / cntQst.toFloat() * 100 else 0f
        val correctRate = if (cntSolved > 0) cntCorrect / cntSolved.toFloat() * 100 else 0f

        return RecordData(cntQst, progressRate, correctRate)
    }
}
