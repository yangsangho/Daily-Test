package kr.yangbob.memorization.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.model.MemRepository

class MainViewModel(private val memRepo: MemRepository): ViewModel()
{
    private val qstList = memRepo.getAllQst()

    val todayCard1 = MutableLiveData<String>()      // 오늘의 시험 문항수
    val todayCard2 = MutableLiveData<String>()      // 시험 진행 상태
    val todayCard3 = MutableLiveData<String>()      // 정답률
    val entireCard1 = MutableLiveData<String>()     // 전체 문항수
    val entireCard2 = MutableLiveData<String>()     // 시험 완료율
    val entireCard3 = MutableLiveData<String>()     // 일일 평균 등록 개수

    fun getQstList() = qstList
    fun setTodayData(){
        todayCard1.value = "0"
        todayCard2.value = "0"
        todayCard3.value = "0"
    }
    fun setEntireCntAverageRegistryCnt(){
        Log.i("TEST", "setEntireCntAverageRegistryCnt()")
        val size = qstList.value?.size ?: 0
        entireCard1.value = "$size"
        if(size > 0){
            val registryCnt = memRepo.getRegistrationDateCnt()
            entireCard3.value = String.format("%.1f", size/registryCnt.toFloat())
        } else {
            entireCard3.value = "0"
        }
    }
    fun setTestCompletionRate(){
        val testCnt = memRepo.getTestCnt()
        val completionCnt = memRepo.getTestCompletionCnt()

        if(testCnt > 0){
            entireCard2.value = String.format("%.1f%%", completionCnt/testCnt.toFloat() * 100)
        } else {
            entireCard2.value = "-"
        }
    }
}
