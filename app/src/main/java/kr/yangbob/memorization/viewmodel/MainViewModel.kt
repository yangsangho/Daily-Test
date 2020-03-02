package kr.yangbob.memorization.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kr.yangbob.memorization.R
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.workForNextTest

class MainViewModel(private val memRepo: MemRepository, application: Application) :
        AndroidViewModel(application) {
    private val logTag = "MainViewModel"

    private var isPossibleClick = false
    fun resetIsPossibleClick() {
        isPossibleClick = false
    }

    fun checkIsPossibleClick(): Boolean =
            if (isPossibleClick) false
            else {
                isPossibleClick = true
                true
            }

    private val qstListLD = memRepo.getAllQstLD()
    private val dormantQstListLD = memRepo.getAllDormantQstLD()
    private val todayQstRecordLD = memRepo.getAllRecordLDFromDate(memRepo.getDateStr(System.currentTimeMillis()))

    val todayCard1 = MutableLiveData<String>()      // 오늘의 시험 문항수
    val todayCard2 = MutableLiveData<String>()      // 시험 진행 상태
    val todayCard3 = MutableLiveData<String>()      // 정답률
    val entireCard1 = MutableLiveData<String>()     // 전체 문항수
    val entireCard2 = MutableLiveData<String>()     // 시험 완료율
    val entireCard3 = MutableLiveData<String>()     // 일일 평균 등록 개수

    fun getQstList() = qstListLD
    fun getDormantQstList() = dormantQstListLD
    fun getQstRecordList() = todayQstRecordLD

    // 문제 추가될 때마다 실행(LiveData) - 전체 문항수, 일일 평균 등록 개수
    fun setEntireCardData() {
        val size = qstListLD.value?.size ?: 0
        entireCard1.value = "$size"

        entireCard3.value = if (size > 0) {
            val entireDate = memRepo.getCalCnt()
            String.format("%.2f", size / entireDate.toFloat())
        } else "0"
    }

    // 재실행 필요 X - 1회만
    fun setTodayTestCount() {
        if (todayCard1.value == null) {
            todayCard1.value = "${todayQstRecordLD.value!!.size}"
        }
    }

    // 시험 보기 intent result 받고 재 실행 필요 - 시험 진행상태, 정답률
// Return = 시험 완료 여부 (needTestBtnDisable)
    fun setTodayCardData(): Boolean {
        var needTestBtnDisable = false
        var needAddPostfix = false
        val qstRecordList = todayQstRecordLD.value!!
        val cntList = qstRecordList.size
        val cntSolved = qstRecordList.filter { it.is_correct != null }.count()
        val cntCorrect = qstRecordList.filter { it.is_correct == true }.count()

        Log.i(logTag, "listSize = $cntList, cntSolved = $cntSolved, cntCorrect = $cntCorrect")

        val todayCard2Text = getApplication<Application>().resources.getString(
                when {
                    cntList == 0 -> {
                        needTestBtnDisable = true
                        R.string.status_msg_no_test
                    }
                    cntSolved == 0 -> {
                        R.string.status_msg_no_start
                    }
                    cntList != cntSolved -> {
                        needAddPostfix = true
                        R.string.status_msg_ongoing
                    }
                    else -> {
                        needTestBtnDisable = true
                        R.string.status_msg_complete
                    }
                }
        )
        todayCard2.value = if (needAddPostfix) "$todayCard2Text\n($cntSolved/$cntList)" else todayCard2Text

        todayCard3.value = if (cntSolved > 0) {
            String.format("%.1f%%", cntCorrect / cntSolved.toFloat() * 100)
        } else "-"

        if (needTestBtnDisable) {
            memRepo.updateCalComplete()
        }

        return needTestBtnDisable
    }

    // 시험 보기 intent result 받고 재 실행 필요 - 시험 완료율
// completedCnt과 entireDate에는 시작일이 포함되어 있음
    fun setTestCompletionRate() {
        val completedCnt = memRepo.getCompletedDateCnt()
        val cntHasTest = memRepo.getCalCntHasTest()

        entireCard2.value = if (cntHasTest > 0) {
            String.format("%.1f%%", completedCnt / cntHasTest.toFloat() * 100)
        } else "-"
    }



    init {
        workForNextTest(memRepo)
    }
}
