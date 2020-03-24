package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.*
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.model.MemRepository

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

    fun getTodayNullRecords() =
            memRepo.getNullRecordsFromDate(todayDateStr)

    fun insertQst(qst: Qst) = memRepo.insertQst(qst)
    fun insertQstRecord(qstRecord: QstRecord) = memRepo.insertQstRecord(qstRecord)
    fun getQstFromId(id: Int) = memRepo.getQstFromId(id)
    fun getAllDormantQst() = memRepo.getAllDormantQst()

    fun getDateStr(time: Long): String = memRepo.getDateStr(time)

    fun update(qst: Qst, qstRecord: QstRecord, isCorrect: Boolean): Boolean {
        val challengeStage = STAGE_LIST[qstRecord.challenge_stage]
        val curStage = STAGE_LIST[qst.cur_stage]
        val newNextDate: Long
        var goMove = true

        if (qstRecord.is_correct == null) {
            if (isCorrect || challengeStage <= Stage.BEGIN_THREE) {
                newNextDate = todayTime + (MILLIS_A_DAY * challengeStage.nextTest)
                if (challengeStage != Stage.REVIEW) qst.cur_stage++
            } else {
                newNextDate = todayTime + (MILLIS_A_DAY * curStage.nextTest)
            }
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
                    newNextDate = todayTime + (MILLIS_A_DAY * challengeStage.nextTest)
                    if (challengeStage != Stage.REVIEW) qst.cur_stage++
                }
                else -> {
                    newNextDate = todayTime + (MILLIS_A_DAY * curStage.nextTest)
                    if (challengeStage != Stage.REVIEW) qst.cur_stage--
                }
            }
        }
        if (goMove) qstRecord.is_correct = isCorrect
        qst.next_test_date = memRepo.getDateStr(newNextDate)
        memRepo.insertQst(qst)
        memRepo.insertQstRecord(qstRecord)      // qstRecord를 먼저 insert하니까 사라지네
        return goMove
    }

    fun updateDormant(qst: Qst, qstRecord: QstRecord, isCorrect: Boolean): Boolean {
        var goMove = true
        if (qstRecord.is_correct == null) {
            qstRecord.is_correct = isCorrect
            qst.is_dormant = false
            if (!isCorrect) {
                qst.cur_stage--
            }
            qst.next_test_date = memRepo.getDateStr(todayTime + (STAGE_LIST[qst.cur_stage].nextTest * MILLIS_A_DAY))
        } else {
            if (qstRecord.is_correct == isCorrect) {
                qstRecord.is_correct = null
                qst.is_dormant = true
                if (!isCorrect) {
                    qst.cur_stage++
                }
                goMove = false
            } else {
                qstRecord.is_correct = isCorrect
                if (isCorrect) qst.cur_stage++
                else qst.cur_stage--
                qst.next_test_date = memRepo.getDateStr(todayTime + (STAGE_LIST[qst.cur_stage].nextTest * MILLIS_A_DAY))
            }
        }
        insertQst(qst)
        return goMove
    }

    fun isFirst(): Boolean = if(memRepo.getIsFirst(SETTING_IS_FIRST_TEST)){
        memRepo.setFirstValueFalse(SETTING_IS_FIRST_TEST)
        true
    } else false
}