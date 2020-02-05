package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.model.MemRepository

class QstViewModel(private val memRepo: MemRepository) : ViewModel() {
    val qstData = MutableLiveData<String>().apply { value = "" }
    val answerData = MutableLiveData<String>().apply { value = "" }

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
}