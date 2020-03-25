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
import kr.yangbob.memorization.db.MyDate
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.todayDate
import kr.yangbob.memorization.view.CalendarActivity
import kr.yangbob.memorization.view.ResultActivity
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewModel(private val memRepo: MemRepository) : ViewModel() {
    private var isPossibleClick = false
    var isPortrait = true
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
    private val _isDetailBtnActivate = MutableLiveData<Boolean>()
    val month: LiveData<Int> = _month
    val year: LiveData<Int> = _year
    val record: LiveData<String> = _record
    val isDetailBtnActivate: LiveData<Boolean> = _isDetailBtnActivate
    private val infoCalendarList: List<InfoCalendar> = memRepo.getAllInfoCalendar()
    private lateinit var currentCalendarID: MyDate

    fun updateInfoCal(deleteSet: HashSet<Int>?) {
        deleteSet?.also { set ->
            infoCalendarList.forEach { list ->
                if (set.contains(list.date.dateInt)) {
                    val newValue = getCalTestComplete(list.date)
                    if (list.isCompleted != newValue) {
                        list.isCompleted = newValue
                    }
                }
            }
        }
    }

    fun getCalTestComplete(calendarId: MyDate) = memRepo.getCalTestComplete(calendarId)

    fun getInfoCalendarList(date: MyDate) = infoCalendarList.filter {
        it.date.year == date.year && it.date.month == date.month
    }

    fun getDateList(): List<MyDate> {
        val dateList = ArrayList<MyDate>()
        val startDate = memRepo.getStartDate()
        val todayDate = todayDate.clone()
        startDate.setDate(Calendar.DAY_OF_MONTH, 1)
        todayDate.setDate(Calendar.DAY_OF_MONTH, 1)

        dateList.add(startDate.clone())
        while(todayDate != startDate){
            startDate.addDate(Calendar.MONTH, 1)
            dateList.add(startDate.clone())
        }
        return dateList.toList()
    }

    fun setCalendar(date: MyDate) {
        _month.value = date.month - 1
        _year.value = date.year
    }

    fun setCurrentCalendar(infoCalendar: InfoCalendar?, resources: Resources) {
        infoCalendar?.also { infoCal ->
            currentCalendarID = infoCal.date
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
                _isDetailBtnActivate.value = false
            } else {
                _record.value = if (cntQst <= 0) {
                    _isDetailBtnActivate.value = false
                    resources.getString(R.string.status_msg_no_test)
                } else {
                    _isDetailBtnActivate.value = true
                    String.format(
                        resources.getString(if (isPortrait) R.string.result_info_format else R.string.result_info_format_land),
                        cntQst,
                        progressRate,
                        correctRate
                    )
                }
            }
        }
    }

    fun detailBtnClick(view: View) {
        val calActivity = view.context as CalendarActivity
        calActivity.startActivityForResult(Intent(view.context, ResultActivity::class.java).apply {
            putExtra(EXTRA_TO_RESULT_DATESTR, currentCalendarID.dateInt)
        }, 123)
    }
}