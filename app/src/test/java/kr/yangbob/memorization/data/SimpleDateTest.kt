package kr.yangbob.memorization.data

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import java.text.DateFormat
import java.util.*

class SimpleDateTest {
    private val pastDate = SimpleDate.newInstanceFromDateInt(20200131)
    private val todayDate = SimpleDate.newInstanceToday()
    private val futureDate = SimpleDate.newInstanceFromDateInt(20250528)

    // 테스트 실행 전 아래 변수 값 변경하기
    private val todayDateInt = 20200402

    @Test
    fun newInstanceTest() {
        assertThat(pastDate, notNullValue())
        assertThat(todayDate, notNullValue())
        assertThat(futureDate, notNullValue())
    }

    @Test(expected = IllegalArgumentException::class)
    fun newInstanceFromDateIntExceptionTest() {
        var simpleDate = SimpleDate.newInstanceFromDateInt(222222)
        simpleDate = SimpleDate.newInstanceFromDateInt(22220528)
        simpleDate = SimpleDate.newInstanceFromDateInt(20201328)
        simpleDate = SimpleDate.newInstanceFromDateInt(20201200)
    }

    @Test
    fun equalsAndCompareTest() {
        val newTodayDate = SimpleDate.newInstanceFromDateInt(todayDateInt)
        assertThat(newTodayDate.hashCode(), `is`(todayDate.hashCode()))
        assertThat(newTodayDate, `is`(todayDate))
        assertThat(newTodayDate, not(equalTo(futureDate)))
        assertThat(newTodayDate, lessThan(futureDate))
        assertThat(newTodayDate, greaterThan(pastDate))
    }

    @Test
    fun cloneTest() {
        val cloneDate = todayDate.clone()
        assertThat(cloneDate, `is`(todayDate))
    }

    @Test
    fun getFormattedDateTest() {
        val defaultFormattedDate = futureDate.getFormattedDate()
        val fullFormattedDate = futureDate.getFormattedDate(DateFormat.FULL)
        assertThat(defaultFormattedDate, `is`("2025. 5. 28"))
        assertThat(fullFormattedDate, `is`("2025년 5월 28일 수요일"))
    }

    @Test
    fun getMethodTest() {
        assertThat(futureDate.getDateInt(), `is`(20250528))
        assertThat(futureDate.getYear(), `is`(2025))
        assertThat(futureDate.getMonth(), `is`(5))
        assertThat(futureDate.getDay(), `is`(28))
    }

    @Test
    fun setDateTest() {
        val cloneDate = todayDate.clone()

        cloneDate.setDate(Calendar.YEAR, 2025)
        assertThat(cloneDate.getYear(), `is`(futureDate.getYear()))

        cloneDate.setDate(Calendar.MONTH, 5)
        assertThat(cloneDate.getMonth(), `is`(futureDate.getMonth()))

        cloneDate.setDate(Calendar.DAY_OF_MONTH, 28)
        assertThat(cloneDate.getDay(), `is`(futureDate.getDay()))

        assertThat(cloneDate, `is`(futureDate))
    }

    @Test
    fun addDateTest() {
        val cloneDate = futureDate.clone()

        cloneDate.addDate(Calendar.YEAR, 4)
        assertThat(cloneDate.getYear(), `is`(2029))
        cloneDate.addDate(Calendar.YEAR, -4)
        assertThat(cloneDate.getYear(), `is`(2025))

        cloneDate.addDate(Calendar.MONTH, 3)
        assertThat(cloneDate.getMonth(), `is`(8))
        cloneDate.addDate(Calendar.MONTH, -3)
        assertThat(cloneDate.getMonth(), `is`(5))

        cloneDate.addDate(Calendar.DAY_OF_MONTH, 3)
        assertThat(cloneDate.getDay(), `is`(31))
        cloneDate.addDate(Calendar.DAY_OF_MONTH, -3)
        assertThat(cloneDate.getDay(), `is`(28))

        assertThat(cloneDate, `is`(futureDate))
    }
}