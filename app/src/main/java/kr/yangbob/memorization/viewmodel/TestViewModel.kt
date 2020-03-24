package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.SETTING_IS_FIRST_TEST
import kr.yangbob.memorization.STAGE_LIST
import kr.yangbob.memorization.Stage
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.todayDate
import java.util.*

class TestViewModel(private val memRepo: MemRepository) : ViewModel() {
    private var isPossibleClick = false
    fun resetIsPossibleClick(){
        isPossibleClick = false
    }
    fun checkIsPossibleClick(): Boolean{
        return if(isPossibleClick){
            false
        } else {
            isPossibleClick = true
            true
        }
    }

    var isDormant: Boolean = false

    fun getTodayNullRecords() = memRepo.getNullRecordsFromDate(todayDate)

    fun insertQst(qst: Qst) = memRepo.insertQst(qst)
    fun insertQstRecord(qstRecord: QstRecord) = memRepo.insertQstRecord(qstRecord)
    fun getQstFromId(id: Int) = memRepo.getQstFromId(id)
    fun getAllDormantQst() = memRepo.getAllDormantQst()

    fun update(qst: Qst, qstRecord: QstRecord, isCorrect: Boolean): Boolean {
        val challengeStage = STAGE_LIST[qstRecord.challenge_stage]
        val curStage = STAGE_LIST[qst.cur_stage]
        val date = todayDate.clone()
        val addDay: Int

        if (qstRecord.is_correct == null) {
            if (isCorrect || challengeStage <= Stage.BEGIN_THREE) {
                addDay = challengeStage.nextTest
                if (challengeStage != Stage.REVIEW) qst.cur_stage++
            } else addDay = curStage.nextTest
            qst.dormant_cnt = 0
        } else {
            when {
                qstRecord.is_correct == isCorrect -> return false
                challengeStage <= Stage.BEGIN_THREE -> {
                    qstRecord.is_correct = isCorrect
                    memRepo.insertQstRecord(qstRecord)
                    return true
                }
                isCorrect -> {
                    addDay = challengeStage.nextTest
                    if (challengeStage != Stage.REVIEW) qst.cur_stage++
                }
                else -> {
                    addDay = curStage.nextTest
                    if (challengeStage != Stage.REVIEW) qst.cur_stage--
                }
            }
        }
        qstRecord.is_correct = isCorrect
        qst.next_test_date = date.apply {
            addDate(Calendar.DAY_OF_MONTH, addDay)
        }
        memRepo.insertQst(qst)
        memRepo.insertQstRecord(qstRecord)      // qstRecord를 먼저 insert하니까 사라지네
        return true
    }

    fun updateDormant(qst: Qst, qstRecord: QstRecord, isCorrect: Boolean): Boolean {
        val date = todayDate.clone()
        if (qstRecord.is_correct == null) {
            qst.is_dormant = false
            if (!isCorrect) qst.cur_stage--
        } else {
            if (qstRecord.is_correct == isCorrect) return false
            else {
                if (isCorrect) qst.cur_stage++
                else qst.cur_stage--
            }
        }
        qstRecord.is_correct = isCorrect
        qst.next_test_date = date.apply {
            addDate(Calendar.DAY_OF_MONTH, STAGE_LIST[qst.cur_stage].nextTest)
        }
        insertQst(qst)
        return true
    }

    fun isFirst(): Boolean = if(memRepo.getIsFirst(SETTING_IS_FIRST_TEST)){
        memRepo.setFirstValueFalse(SETTING_IS_FIRST_TEST)
        true
    } else false
}