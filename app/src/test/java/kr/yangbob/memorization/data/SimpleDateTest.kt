package kr.yangbob.memorization.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.text.DateFormat
import java.util.*

class SimpleDateTest {
    private val pastDate = SimpleDate.newInstanceFromDateInt(20200131)
    private val todayDate = SimpleDate.newInstanceToday()
    private val futureDate = SimpleDate.newInstanceFromDateInt(20250528)
    // 테스트 실행 전 아래 변수 값 변경하기
    private val todayDateInt = 20200330

    @Test
    fun newInstanceTest(){
        assertThat(pastDate).isNotNull()
        assertThat(todayDate).isNotNull()
        assertThat(futureDate).isNotNull()
    }

    @Test
    fun newInstanceFromDateIntExceptionTest(){
        try{
            val simpleDate = SimpleDate.newInstanceFromDateInt(222222)
        } catch (e: IllegalArgumentException){
            println("newInstanceFromDateIntExceptionTest pass : check length exception")
        }

        try{
            val simpleDate = SimpleDate.newInstanceFromDateInt(22220528)
        } catch (e: IllegalArgumentException){
            println("newInstanceFromDateIntExceptionTest pass : check year exception")
        }

        try{
            val simpleDate = SimpleDate.newInstanceFromDateInt(20201328)
        } catch (e: IllegalArgumentException){
            println("newInstanceFromDateIntExceptionTest pass : check year exception")
        }

        try{
            val simpleDate = SimpleDate.newInstanceFromDateInt(20201200)
        } catch (e: IllegalArgumentException){
            println("pass : check year exception")
        }
    }

    @Test
    fun equalsAndCompareTest(){
        val newTodayDate = SimpleDate.newInstanceFromDateInt(todayDateInt)
        assertThat(newTodayDate.hashCode()).isEqualTo(todayDate.hashCode())
        assertThat(newTodayDate).isEquivalentAccordingToCompareTo(todayDate)
        assertThat(newTodayDate).isEqualTo(todayDate)
        assertThat(newTodayDate).isNotEqualTo(futureDate)
        assertThat(newTodayDate).isLessThan(futureDate)
        assertThat(newTodayDate).isGreaterThan(pastDate)
    }

    @Test
    fun cloneTest(){
        val cloneDate = todayDate.clone()
        assertThat(cloneDate).isEqualTo(todayDate)
    }

    @Test
    fun getFormattedDateTest(){
        val defaultFormattedDate = futureDate.getFormattedDate()
        val fullFormattedDate = futureDate.getFormattedDate(DateFormat.FULL)
        assertThat(defaultFormattedDate).isEqualTo("2025. 5. 28")
        assertThat(fullFormattedDate).isEqualTo("2025년 5월 28일 수요일")
    }

    @Test
    fun getMethodTest(){
        assertThat(futureDate.getDateInt()).isEqualTo(20250528)
        assertThat(futureDate.getYear()).isEqualTo(2025)
        assertThat(futureDate.getMonth()).isEqualTo(5)
        assertThat(futureDate.getDay()).isEqualTo(28)
    }

    @Test
    fun setDateTest(){
        val cloneDate = todayDate.clone()

        cloneDate.setDate(Calendar.YEAR, 2025)
        assertThat(cloneDate.getYear()).isEqualTo(futureDate.getYear())

        cloneDate.setDate(Calendar.MONTH, 5)
        assertThat(cloneDate.getMonth()).isEqualTo(futureDate.getMonth())

        cloneDate.setDate(Calendar.DAY_OF_MONTH, 28)
        assertThat(cloneDate.getDay()).isEqualTo(futureDate.getDay())

        assertThat(cloneDate).isEqualTo(futureDate)
    }

    @Test
    fun addDateTest(){
        val cloneDate = futureDate.clone()

        cloneDate.addDate(Calendar.YEAR, 4)
        assertThat(cloneDate.getYear()).isEqualTo(2029)
        cloneDate.addDate(Calendar.YEAR, -4)
        assertThat(cloneDate.getYear()).isEqualTo(2025)

        cloneDate.addDate(Calendar.MONTH, 3)
        assertThat(cloneDate.getMonth()).isEqualTo(8)
        cloneDate.addDate(Calendar.MONTH, -3)
        assertThat(cloneDate.getMonth()).isEqualTo(5)

        cloneDate.addDate(Calendar.DAY_OF_MONTH, 3)
        assertThat(cloneDate.getDay()).isEqualTo(31)
        cloneDate.addDate(Calendar.DAY_OF_MONTH, -3)
        assertThat(cloneDate.getDay()).isEqualTo(28)

        assertThat(cloneDate).isEqualTo(futureDate)
    }
}