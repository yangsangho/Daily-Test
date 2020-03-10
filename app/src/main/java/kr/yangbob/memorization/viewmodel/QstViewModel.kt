package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.model.MemRepository

class QstViewModel(private val memRepo: MemRepository) : ViewModel() {
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

    val qstData = MutableLiveData<String>("")
    val answerData = MutableLiveData<String>("")

    private lateinit var recordList: List<QstRecord>
    private lateinit var qst: Qst
    private var id = 0

    fun setQstId(id: Int){
        this.id = id
        if(id < 0) throw IllegalArgumentException()

        qst = memRepo.getQstFromId(id)
        recordList = memRepo.getAllRecordFromId(id)

        qstData.value = qst.title
        answerData.value = qst.answer
    }

    fun getRecordList() = recordList
    fun getQst() = qst

    fun isPossibleSave(): Boolean = !qstData.value.isNullOrEmpty() && !answerData.value.isNullOrEmpty()

    fun save() {
        qst.title = qstData.value!!
        qst.answer = answerData.value!!
        memRepo.insertQst(qst)
    }

    fun cancel(){
        qstData.value = qst.title
        answerData.value = qst.answer
    }

    fun delete(){
        recordList.forEach {
            if(memRepo.getCntRecord(it.calendar_id) == 1){
                memRepo.updateCal(it.calendar_id, null)
            } else {
                if(it.is_correct == null){
                    if(memRepo.getCntNotSolved(it.calendar_id) == 1){
                        memRepo.updateCal(it.calendar_id, true)
                    }
                }
            }
            memRepo.deleteQstRecord(it)
        }
        memRepo.deleteQst(qst)
    }
}