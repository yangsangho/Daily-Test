package kr.yangbob.memorization.viewmodel

import android.content.Intent
import android.content.res.Resources
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.EXTRA_TO_RESULT_DATESTR
import kr.yangbob.memorization.R
import kr.yangbob.memorization.db.InfoCalendar
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.view.ResultActivity
import java.util.*

class CalendarViewModel(private val memRepo: MemRepository) : ViewModel() {
    private var isPossibleClick = false
    fun resetIsPossibleClick() {
        isPossibleClick = false
    }

    fun checkIsPossibleClick(): Boolean {
        return if (isPossibleClick) false
        else {
            isPossibleClick = true
            true
        }
    }

    private val _month = MutableLiveData<Int>()
    private val _year = MutableLiveData<Int>()
    private val _record = MutableLiveData<String>()
    val month: LiveData<Int> = _month
    val year: LiveData<Int> = _year
    val record: LiveData<String> = _record
    private val infoCalendarList: List<InfoCalendar> = memRepo.getAllInfoCalendar()
    private lateinit var currentCalendarID: String

    fun getQstCalendarList(yearMonth: Int) = infoCalendarList.filter {
        it.yearMonth == yearMonth
    }

    fun yearMonthList(): List<String> {
        val todayCal: Calendar = Calendar.getInstance()
        val minTime = memRepo.getStartDateStr().let {
            memRepo.getDateLong(it)
        }
        val minCal = (todayCal.clone() as Calendar).apply {
            timeInMillis = minTime
        }

        return makeCalList(todayCal, minCal)
                .map { String.format("%d%02d", it.get(Calendar.YEAR), it.get(Calendar.MONTH) + 1) }
    }

    fun setCalendar(yearMonth: String) {
        _month.value = yearMonth.substring(4).toInt() - 1
        _year.value = yearMonth.substring(0, 4).toInt()
    }

    fun setCurrentCalendar(infoCalendar: InfoCalendar?, resources: Resources) {
        infoCalendar?.also { infoCal ->
            currentCalendarID = infoCal.id

            val recordList = memRepo.getAllRecordFromDate(currentCalendarID)
            val cntQst = recordList.size
            val cntSolved = recordList.count { it.is_correct != null }
            val cntCorrect = recordList.count { it.is_correct == true }

            val progressRate = if (cntQst > 0) cntSolved / cntQst.toFloat() * 100
            else 0f
            val correctRate = if (cntSolved > 0) cntCorrect / cntSolved.toFloat() * 100
            else 0f

            if (infoCal.isStartDay) {
                _record.value = resources.getString(R.string.calendar_start_day)
            } else {
                _record.value = if (cntQst <= 0) resources.getString(R.string.status_msg_no_test)
                else String.format(
                        resources.getString(R.string.result_info_format),
                        cntQst,
                        progressRate,
                        correctRate
                )
            }
        }
    }

    fun detailBtnClick(view: View){
        view.context.startActivity(Intent(view.context, ResultActivity::class.java).apply {
            putExtra(EXTRA_TO_RESULT_DATESTR, currentCalendarID)
        })
    }

    private tailrec fun makeCalList(
            cal: Calendar,
            minCal: Calendar,
            acc: List<Calendar> = listOf()
    ): List<Calendar> = when {
        cal.get(Calendar.YEAR) <= minCal.get(Calendar.YEAR)
                && cal.get(Calendar.MONTH) < minCal.get(Calendar.MONTH) -> acc
        else -> {
            val cal2: Calendar = (cal.clone() as Calendar).apply {
                if (get(Calendar.MONTH) == 0) {
                    set(Calendar.YEAR, get(Calendar.YEAR) - 1)
                    set(Calendar.MONTH, Calendar.DECEMBER)
                } else {
                    set(Calendar.MONTH, get(Calendar.MONTH) - 1)
                }
            }
            makeCalList(cal2, minCal, listOf(cal) + acc)
        }
    }


}