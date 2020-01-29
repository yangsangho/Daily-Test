package kr.yangbob.memorization.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kr.yangbob.memorization.R
import kr.yangbob.memorization.alarm.workForNextTest
import kr.yangbob.memorization.model.MemRepository

class MainViewModel(private val memRepo: MemRepository, application: Application) : AndroidViewModel(application) {
    private val qstListLD = memRepo.getAllQstLD()
    private val todayQstRecordLD = memRepo.getLDListFromDate( memRepo.getDateStr( System.currentTimeMillis() ) )

    val todayCard1 = MutableLiveData<String>()      // 오늘의 시험 문항수
    val todayCard2 = MutableLiveData<String>()      // 시험 진행 상태
    val todayCard3 = MutableLiveData<String>()      // 정답률
    val entireCard1 = MutableLiveData<String>()     // 전체 문항수
    val entireCard2 = MutableLiveData<String>()     // 시험 완료율
    val entireCard3 = MutableLiveData<String>()     // 일일 평균 등록 개수

    init {
        workForNextTest(memRepo)
    }

    fun getQstList() = qstListLD
    fun getQstRecordList() = todayQstRecordLD

    // 문제 추가될 때마다 실행(LiveData) - 전체 문항수, 일일 평균 등록 개수
    fun setEntireCardData() {
        val size = qstListLD.value?.size ?: 0
        entireCard1.value = "$size"

        entireCard3.value = if (size > 0) {
            val entireDate = memRepo.getEntireDate()
            String.format("%.2f", size / entireDate.toFloat())
        } else "0"
    }

    // 재실행 필요 X - 1회만
    fun setTodayTestCount(){
        if(todayCard1.value == null){
            todayCard1.value = "${todayQstRecordLD.value!!.size}"
        }
    }
    // 시험 보기 intent result 받고 재 실행 필요 - 시험 진행상태, 정답률
    // Return = 시험 완료 여부 (needTestBtnDisable)
    fun setTodayCardData() : Boolean{
        var needTestBtnDisable = false
        val qstRecordList = todayQstRecordLD.value!!
        val cntList = qstRecordList.size
        val cntSolved = qstRecordList.filter { it.is_correct != null }.count()
        val cntCorrect = qstRecordList.filter { it.is_correct == true }.count()

        todayCard2.value = getApplication<Application>().resources.getString(
            when {
                cntList == 0 -> {
                    needTestBtnDisable = true
                    R.string.status_msg_no_test
                }
                cntSolved == 0 -> {
                    R.string.status_msg_no_start
                }
                cntList == cntSolved -> {
                    needTestBtnDisable = true
                    R.string.status_msg_complete
                }
                else -> {
                    R.string.status_msg_ongoing
                }
            }
        )

        todayCard3.value = if (cntSolved > 0) {
            String.format("%.1f%%", cntCorrect / cntSolved.toFloat() * 100)
        } else "-"

        return needTestBtnDisable
    }

    // 시험 보기 intent result 받고 재 실행 필요 - 시험 완료율
    // completedCnt과 entireDate에는 시작일이 포함되어 있음
    fun setTestCompletionRate() {
        val completedCnt = memRepo.getCompletedDateCnt()
        val entireDate = memRepo.getEntireDate()

        entireCard2.value = if (entireDate > 0) {
            String.format("%.1f%%", completedCnt / entireDate.toFloat() * 100)
        } else "-"
    }
}
