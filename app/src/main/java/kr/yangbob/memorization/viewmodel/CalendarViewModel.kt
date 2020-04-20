package kr.yangbob.memorization.viewmodel

import android.content.Intent
import android.content.res.Resources
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kr.yangbob.memorization.EXTRA_TO_RESULT_DATESTR
import kr.yangbob.memorization.R
import kr.yangbob.memorization.data.InfoCalendar
import kr.yangbob.memorization.data.SimpleDate
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.todayDate
import kr.yangbob.memorization.view.CalendarActivity
import kr.yangbob.memorization.view.ResultActivity
import java.util.*
import kotlin.collections.ArrayList

//todo : Need to Add Unit Test
class CalendarViewModel(private val memRepo: MemRepository) : BaseViewModel() {
    private var isPortrait = true
    private val infoCalendarList: List<InfoCalendar> = memRepo.getAllInfoCalendar()
    private lateinit var currentDate: SimpleDate

    private val _month = MutableLiveData<Int>()
    private val _year = MutableLiveData<Int>()
    private val _record = MutableLiveData<String>()
    private val _isDetailBtnActivate = MutableLiveData<Boolean>()
    val month: LiveData<Int> = _month
    val year: LiveData<Int> = _year
    val record: LiveData<String> = _record
    val isDetailBtnActivate: LiveData<Boolean> = _isDetailBtnActivate

    fun setPortrait(value: Boolean) {
        isPortrait = value
    }

    fun updateInfoCal(deleteSet: HashSet<Int>) {
        deleteSet?.also { set ->
            infoCalendarList.forEach { list ->
                if (set.contains(list.date.getDateInt())) {
                    val newValue = getTestCompletionOnDate(list.date)
                    if (list.isCompleted != newValue) {
                        list.isCompleted = newValue
                    }
                }
            }
        }
    }

    fun getTestCompletionOnDate(calendarId: SimpleDate) = memRepo.getTestCompletionOnDate(calendarId)

    fun getInfoCalendarList(date: SimpleDate) = infoCalendarList.filter {
        it.date.getYear() == date.getYear() && it.date.getMonth() == date.getMonth()
    }

    fun getDateList(): List<SimpleDate> {
        val dateList = ArrayList<SimpleDate>()
        val startDate = memRepo.getStartDate()
        val todayDate = todayDate.clone()
        startDate.setDate(Calendar.DAY_OF_MONTH, 1)
        todayDate.setDate(Calendar.DAY_OF_MONTH, 1)

        dateList.add(startDate.clone())
        while (todayDate != startDate) {
            startDate.addDate(Calendar.MONTH, 1)
            dateList.add(startDate.clone())
        }
        return dateList.toList()
    }

    fun setYearMonthText(date: SimpleDate) {
        _month.value = date.getMonth() - 1
        _year.value = date.getYear()
    }

    fun setRecordText(infoCalendar: InfoCalendar?, resources: Resources) {
        infoCalendar?.also { infoCal ->
            currentDate = infoCal.date.clone()
            val recordData = getRecordData()

            if (infoCal.isStartDay) {
                _isDetailBtnActivate.value = false
                _record.value = resources.getString(R.string.calendar_start_day)
            } else {
                if (recordData.cntQst <= 0) {
                    _isDetailBtnActivate.value = false
                    _record.value = resources.getString(R.string.status_msg_no_test)
                } else {
                    _isDetailBtnActivate.value = true
                    _record.value = String.format(
                            resources.getString(if (isPortrait) R.string.result_info_format else R.string.result_info_format_land),
                            recordData.cntQst,
                            recordData.progressRate,
                            recordData.correctRate
                    )
                }
            }
        }
    }

    private data class RecordData(
            val cntQst: Int,
            val progressRate: Float,
            val correctRate: Float
    )

    private fun getRecordData(): RecordData {
        val recordList = memRepo.getAllRecordFromDate(currentDate)
        val cntQst = recordList.size
        val cntSolved = recordList.count { it.is_correct != null }
        val cntCorrect = recordList.count { it.is_correct == true }

        val progressRate = if (cntQst > 0) cntSolved / cntQst.toFloat() * 100 else 0f
        val correctRate = if (cntSolved > 0) cntCorrect / cntSolved.toFloat() * 100 else 0f

        return RecordData(cntQst, progressRate, correctRate)
    }

    fun detailBtnClick(view: View) {
        val calActivity = view.context as CalendarActivity
        calActivity.startActivityForResult(Intent(view.context, ResultActivity::class.java).apply {
            putExtra(EXTRA_TO_RESULT_DATESTR, currentDate.getDateInt())
        }, 123)
    }
}