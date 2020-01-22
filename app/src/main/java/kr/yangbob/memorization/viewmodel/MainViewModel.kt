package kr.yangbob.memorization.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kr.yangbob.memorization.R
import kr.yangbob.memorization.alarm.workForNextTest
import kr.yangbob.memorization.model.MemRepository

class MainViewModel(private val memRepo: MemRepository, application: Application) : AndroidViewModel(application) {
    private val qstListLD = memRepo.getAllQstLD()
    private val testList = memRepo.getNeedTestList()

    val todayCard1 = MutableLiveData<String>("${testList.size}")      // 오늘의 시험 문항수
    val todayCard2 = MutableLiveData<String>()      // 시험 진행 상태
    val todayCard3 = MutableLiveData<String>()      // 정답률
    val entireCard1 = MutableLiveData<String>()     // 전체 문항수
    val entireCard2 = MutableLiveData<String>()     // 시험 완료율
    val entireCard3 = MutableLiveData<String>()     // 일일 평균 등록 개수

    fun getQstList() = qstListLD

    // 문제 추가될 때마다 실행(LiveData) - 전체 문항수, 일일 평균 등록 개수
    fun setEntireCntAverageRegistration() {
        Log.i("yangtest", "setEntireCntAverageRegistryCnt()")

        val size = qstListLD.value?.size ?: 0
        entireCard1.value = "$size"

        entireCard3.value = if (size > 0) {
            val entireDate = memRepo.getEntireDate()
            String.format("%.2f", size / entireDate.toFloat())
        } else "0"
    }

    // 시험 보기 intent result 받고 재 실행 필요 - 시험 진행상태, 정답률
    fun setTodayData() {
        val dateStr = memRepo.getDateStr(System.currentTimeMillis())
        val todayCompleteTestCnt = memRepo.getRecordCntFromDate(dateStr)
        val todayCorrectCtn = memRepo.getCorrectCntFromDate(dateStr)

        todayCard2.value = getApplication<Application>().resources.getString(
            when {
                testList.isEmpty() -> {
                    R.string.status_msg_no_test
                }
                todayCompleteTestCnt == 0 -> {
                    R.string.status_msg_no_start
                }
                todayCompleteTestCnt == testList.size -> {
                    R.string.status_msg_complete
                }
                else -> {
                    R.string.status_msg_ongoing
                }
            }
        )
        todayCard3.value = if (todayCompleteTestCnt > 0) {
            String.format("%.1f%%", todayCorrectCtn / todayCompleteTestCnt.toFloat() * 100)
        } else "-"
    }

    // 시험 보기 intent result 받고 재 실행 필요 - 시험 완료율
    // completedCnt과 entireDate에는 시작일이 포함되어 있음
    fun setTestCompletionRate() {
        val completedCnt = memRepo.getCompletedDateCnt() - 1
        val entireDate = memRepo.getEntireDate() - 1
        Log.i(
            "yangtest",
            "setTestCompletionRate() : completedCnt = $completedCnt, entireDate = $entireDate"
        )

        entireCard2.value = if (entireDate > 0) {
            String.format("%.1f%%", completedCnt / entireDate.toFloat() * 100)
        } else "-"
    }

    init {
        workForNextTest(memRepo)
    }
}
