package kr.yangbob.memorization.data

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DayInfoListBuilderTest {
    @Mock
    private lateinit var simpleDate: SimpleDate

    @Mock
    private lateinit var infoCalA: InfoCalendar

    @Mock
    private lateinit var dateA: SimpleDate

    @Mock
    private lateinit var infoCalB: InfoCalendar

    @Mock
    private lateinit var dateB: SimpleDate

    @Mock
    private lateinit var infoCalC: InfoCalendar

    @Mock
    private lateinit var dateC: SimpleDate

    @Spy
    private val infoCalList = mutableListOf<InfoCalendar>()

    private var isInit = false
    private var dayA = 0
    private var dayB = 0
    private var dayC = 0
    private val dayInfoListSize = 42
    private var expectedCntPrevMonthDay = 0
    private var expectedStartDayOfPrevMonth = 0
    private var expectedMaxDayOfThisMonth = 0

    @Before
    fun before() {
        if (isInit) return

        infoCalList.add(infoCalA)
        infoCalList.add(infoCalB)
        infoCalList.add(infoCalC)

        `when`(infoCalA.date).thenReturn(dateA)
        `when`(infoCalB.date).thenReturn(dateB)
        `when`(infoCalC.date).thenReturn(dateC)

        isInit = true
    }

    /* 2020년 4월 케이스 */
    @Test
    fun testCase1() {
        // given
        givenYearMonth(2020, 4)
        givenMonthExpectedInfo(3, 29, 30)
        givenMockInfoCalDay(26, 27, 28)

        // when
        val builder = DayInfoListBuilder(simpleDate, infoCalList)
        val cntPrevMonthDay = builder.getCntPrevMonthDay()
        val dayInfoList = builder.build()

        // then
        assertThat(dayInfoList.size, `is`(dayInfoListSize))
        assertThat(cntPrevMonthDay, `is`(expectedCntPrevMonthDay))
        thenCheckDayInfoList(dayInfoList)
    }

    /* 2020년 3월 케이스 */
    @Test
    fun testCase2() {
        // given
        givenYearMonth(2020, 3)
        givenMonthExpectedInfo(0, 0, 31)
        givenMockInfoCalDay(15, 16, 17)

        // when
        val builder = DayInfoListBuilder(simpleDate, infoCalList)
        val cntPrevMonthDay = builder.getCntPrevMonthDay()
        val dayInfoList = builder.build()

        // then
        assertThat(dayInfoList.size, `is`(dayInfoListSize))
        assertThat(cntPrevMonthDay, `is`(expectedCntPrevMonthDay))
        thenCheckDayInfoList(dayInfoList)
    }

    /* 2026년 6월 케이스 */
    @Test
    fun testCase3() {
        // given
        givenYearMonth(2026, 6)
        givenMonthExpectedInfo(1, 31, 30)
        givenMockInfoCalDay(26, 27, 28)

        // when
        val builder = DayInfoListBuilder(simpleDate, infoCalList)
        val cntPrevMonthDay = builder.getCntPrevMonthDay()
        val dayInfoList = builder.build()

        // then
        assertThat(dayInfoList.size, `is`(dayInfoListSize))
        assertThat(cntPrevMonthDay, `is`(expectedCntPrevMonthDay))
        thenCheckDayInfoList(dayInfoList)
    }

    /* 2021년 5월 케이스 */
    @Test
    fun testCase4() {
        // given
        givenYearMonth(2021, 5)
        givenMonthExpectedInfo(6, 25, 31)
        givenMockInfoCalDay(26, 27, 28)

        // when
        val builder = DayInfoListBuilder(simpleDate, infoCalList)
        val cntPrevMonthDay = builder.getCntPrevMonthDay()
        val dayInfoList = builder.build()

        // then
        assertThat(dayInfoList.size, `is`(dayInfoListSize))
        assertThat(cntPrevMonthDay, `is`(expectedCntPrevMonthDay))
        thenCheckDayInfoList(dayInfoList)
    }

    private fun givenYearMonth(year: Int, month: Int) {
        `when`(simpleDate.getYear()).thenReturn(year)
        `when`(simpleDate.getMonth()).thenReturn(month)
    }

    private fun givenMonthExpectedInfo(
            expectedCntPrevMonthDay: Int,
            expectedStartDayOfPrevMonth: Int,
            expectedMaxDayOfThisMonth: Int) {
        this.expectedCntPrevMonthDay = expectedCntPrevMonthDay
        this.expectedStartDayOfPrevMonth = expectedStartDayOfPrevMonth
        this.expectedMaxDayOfThisMonth = expectedMaxDayOfThisMonth
    }

    private fun givenMockInfoCalDay(dayA: Int, dayB: Int, dayC: Int) {
        `when`(dateA.getDayOfMonth()).thenReturn(dayA)
        `when`(dateB.getDayOfMonth()).thenReturn(dayB)
        `when`(dateC.getDayOfMonth()).thenReturn(dayC)
        this.dayA = dayA
        this.dayB = dayB
        this.dayC = dayC
    }

    private fun thenCheckDayInfoList(dayInfoList: List<DayInfo>) {
        var nextMonthDay = 1
        var thisMonthDay = 1
        dayInfoList.forEachIndexed { index, dayInfo ->
            when {
                index < expectedCntPrevMonthDay -> {
                    assertThat(dayInfo.day, `is`(expectedStartDayOfPrevMonth + index))
                    assertThat(dayInfo.infoCalendar, nullValue())
                    assertThat(dayInfo.isInOut, `is`(true))
                }
                index < expectedCntPrevMonthDay + expectedMaxDayOfThisMonth -> {
                    assertThat(dayInfo.day, `is`(thisMonthDay++))
                    when (dayInfo.day) {
                        dayA, dayB, dayC -> {
                            assertThat(dayInfo.infoCalendar, notNullValue())
                        }
                        else -> {
                            assertThat(dayInfo.infoCalendar, nullValue())
                        }
                    }
                    assertThat(dayInfo.isInOut, `is`(false))
                }
                else -> {
                    assertThat(dayInfo.day, `is`(nextMonthDay++))
                    assertThat(dayInfo.infoCalendar, nullValue())
                    assertThat(dayInfo.isInOut, `is`(true))
                }
            }
        }
    }
}