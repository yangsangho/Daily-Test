package kr.yangbob.memorization.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.QstCalendar
import kr.yangbob.memorization.model.MemRepository

class MainViewModel(private val memRepo: MemRepository): ViewModel()
{
    private val qstListLD = memRepo.getAllQstLD()

    val todayCard1 = MutableLiveData<String>()      // 오늘의 시험 문항수
    val todayCard2 = MutableLiveData<String>()      // 시험 진행 상태
    val todayCard3 = MutableLiveData<String>()      // 정답률
    val entireCard1 = MutableLiveData<String>()     // 전체 문항수
    val entireCard2 = MutableLiveData<String>()     // 시험 완료율
    val entireCard3 = MutableLiveData<String>()     // 일일 평균 등록 개수

    fun getQstList() = qstListLD
    fun setTodayData(){
        val test = qstListLD.value
        if (test != null) {
            for(qst in test){
                Log.i("yangtest", "qstlist : qst.regi = ${qst.registration_date}, qst.next = ${qst.next_test_date}")
            }
        }
        val cntTestQst = memRepo.getNeedTestCnt() + memRepo.getTodayRecordCnt()
        todayCard1.value = "$cntTestQst"
        todayCard2.value = "0"
        todayCard3.value = "0"
    }

    // 전체 문항수, 일일 평균 등록 개수
    fun setEntireCntAverageRegistration(){
        Log.i("yangtest", "setEntireCntAverageRegistryCnt()")
        val size = qstListLD.value?.size ?: 0
        entireCard1.value = "$size"
        if(size > 0){
            val entireDate = memRepo.getEntireDate()
            Log.i("yangtest", "entireDate = $entireDate")
            entireCard3.value = String.format("%.1f", size/entireDate.toFloat())
        } else {
            entireCard3.value = "0"
        }
    }

    // 시험보기 끝났을 때 다시 실행해줄 수 있도록 - 시험 완료율
    // completedCnt과 entireDate에는 시작일이 포함되어 있음
    fun setTestCompletionRate(){
        val completedCnt = memRepo.getCompletedTestCnt() - 1
        val entireDate = memRepo.getEntireDate() - 1
        Log.i("yangtest", "setTestCompletionRate() : completedCnt = $completedCnt, entireDate = $entireDate")
        if(entireDate > 0){
            entireCard2.value = String.format("%.1f%%", completedCnt/entireDate.toFloat() * 100)
        } else {
            entireCard2.value = "-"
        }
    }

    init {
        val qstCal = memRepo.getTodayCalendar()
        if (qstCal == null) {
            val todayDate = memRepo.getDateStr( System.currentTimeMillis() )
            val newQstCal = QstCalendar(todayDate, memRepo.getNeedTestCnt())
            memRepo.insertQstCalendar(newQstCal)
            Log.i("yangtest", "(MainViewModel)qstCal is Null !! -> insert QstCalendar!")
        } else {
            Log.i("yangtest", "(MainViewModel)qstCal is Not Null -> Skip insert QstCalendar -> id = ${qstCal.id}")
        }
    }
}
