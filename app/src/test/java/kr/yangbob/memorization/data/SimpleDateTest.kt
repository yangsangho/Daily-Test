package kr.yangbob.memorization.data

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import java.text.DateFormat
import java.util.*

class SimpleDateTest {
    @Test
    fun newInstanceTest() {
        val todayObj = SimpleDate.newInstanceToday()
        val fromDateIntObj = SimpleDate.newInstanceFromDateInt(19910528)

        assertThat(todayObj, notNullValue())
        assertThat(fromDateIntObj, notNullValue())
    }

    @Test(expected = IllegalArgumentException::class)
    fun newInstanceFromDateIntLengthExceptionTest() {
        var simpleDate = SimpleDate.newInstanceFromDateInt(222222)
    }

    @Test(expected = IllegalArgumentException::class)
    fun newInstanceFromDateIntYearExceptionTest() {
        val simpleDate = SimpleDate.newInstanceFromDateInt(22220528)
    }

    @Test(expected = IllegalArgumentException::class)
    fun newInstanceFromDateIntMonthExceptionTest() {
        val simpleDate = SimpleDate.newInstanceFromDateInt(20201328)
    }

    @Test(expected = IllegalArgumentException::class)
    fun newInstanceFromDateIntDayExceptionTest() {
        val simpleDate = SimpleDate.newInstanceFromDateInt(20201200)
    }

    @Test
    fun equalsAndCompareTest() {
        val todayObj = SimpleDate.newInstanceToday()
        val pastObj = SimpleDate.newInstanceFromDateInt(19910528)
        val pastObj2 = SimpleDate.newInstanceFromDateInt(19910528)
        val futureObj = SimpleDate.newInstanceFromDateInt(20400528)

        assertThat(pastObj.hashCode(), `is`(pastObj2.hashCode()))
        assertThat(pastObj, `is`(pastObj2))
        assertThat(pastObj, not(equalTo(futureObj)))
        assertThat(todayObj, lessThan(futureObj))
        assertThat(todayObj, greaterThan(pastObj))
    }

    @Test
    fun cloneTest() {
        val todayObj = SimpleDate.newInstanceToday()
        val cloneDate = todayObj.clone()
        assertThat(cloneDate, `is`(todayObj))
    }

    @Test
    fun getFormattedDateTest() {
        val pastObj = SimpleDate.newInstanceFromDateInt(19910528)
        val defaultFormattedDate = pastObj.getFormattedDate()
        val fullFormattedDate = pastObj.getFormattedDate(DateFormat.FULL)
        assertThat(defaultFormattedDate, `is`("1991. 5. 28"))
        assertThat(fullFormattedDate, `is`("1991년 5월 28일 화요일"))
    }

    @Test
    fun getMethodTest() {
        val pastObj = SimpleDate.newInstanceFromDateInt(19910528)
        assertThat(pastObj.getDateInt(), `is`(19910528))
        assertThat(pastObj.getYear(), `is`(1991))
        assertThat(pastObj.getMonth(), `is`(5))
        assertThat(pastObj.getDayOfMonth(), `is`(28))
    }

    @Test
    fun setDateTest() {
        val todayObj = SimpleDate.newInstanceToday()
        val pastObj = SimpleDate.newInstanceFromDateInt(19910528)

        todayObj.setDate(Calendar.YEAR, 1991)
        assertThat(todayObj.getYear(), `is`(pastObj.getYear()))

        todayObj.setDate(Calendar.MONTH, 5)
        assertThat(todayObj.getMonth(), `is`(pastObj.getMonth()))

        todayObj.setDate(Calendar.DAY_OF_MONTH, 28)
        assertThat(todayObj.getDayOfMonth(), `is`(pastObj.getDayOfMonth()))

        assertThat(todayObj, `is`(pastObj))
    }

    @Test
    fun addDateTest() {
        val testDate = SimpleDate.newInstanceFromDateInt(20250531)

        testDate.addDate(Calendar.YEAR, 4)
        assertThat(testDate.getYear(), `is`(2029))
        testDate.addDate(Calendar.YEAR, -4)
        assertThat(testDate.getYear(), `is`(2025))

        testDate.addDate(Calendar.MONTH, 1)
        assertThat(testDate.getMonth(), `is`(6))
        assertThat(testDate.getDayOfMonth(), `is`(30))     // 하루가 사라지는 문제가 있음
        testDate.addDate(Calendar.MONTH, -1)
        assertThat(testDate.getMonth(), `is`(5))
        assertThat(testDate.getDayOfMonth(), `is`(30))

        testDate.addDate(Calendar.DAY_OF_MONTH, 4)
        assertThat(testDate.getMonth(), `is`(6))
        assertThat(testDate.getDayOfMonth(), `is`(3))
        testDate.addDate(Calendar.DAY_OF_MONTH, -4)
        assertThat(testDate.getMonth(), `is`(5))
        assertThat(testDate.getDayOfMonth(), `is`(30))
    }
}