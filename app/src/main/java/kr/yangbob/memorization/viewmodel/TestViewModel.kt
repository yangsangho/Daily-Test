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


    fun getQstFromId(id: Int) = memRepo.getQstFromId(id)
    fun getAllDormantQst() = memRepo.getAllDormantQst()

    fun getDateStr(time: Long): String = memRepo.getDateStr(time)

    fun update(qst: Qst, qstRecord: QstRecord, isCorrect: Boolean): Boolean {
        val challengeStage = STAGE_LIST[qstRecord.challenge_stage]
        val currentNextDate = memRepo.getDateLong(qst.next_test_date)
        val curStage = STAGE_LIST[qst.cur_stage]
        val cntAfterDay: Int
        var goMove = true

        if (qstRecord.is_correct == null) {
            if (isCorrect || challengeStage <= Stage.BEGIN_THREE) {
                cntAfterDay = challengeStage.nextTest
                if (challengeStage != Stage.REVIEW) qst.cur_stage++
            } else {
                cntAfterDay = curStage.nextTest
            }
        } else {
            when {
                qstRecord.is_correct == isCorrect -> {          // 취소 로직 (같은 거 눌렀을 때)
                    cntAfterDay = if (qstRecord.is_correct!!) {
                        if (challengeStage != Stage.REVIEW) qst.cur_stage--
                        -challengeStage.nextTest
                    } else {
                        if (challengeStage <= Stage.BEGIN_THREE) qst.cur_stage--
                        -curStage.nextTest
                    }
                    qstRecord.is_correct = null
                    goMove = false
                }
                challengeStage <= Stage.BEGIN_THREE -> {
                    qstRecord.is_correct = isCorrect
                    memRepo.insertQstRecord(qstRecord)
                    return true
                }
                isCorrect -> {
                    cntAfterDay = challengeStage.nextTest - curStage.nextTest
                    if (challengeStage != Stage.REVIEW) qst.cur_stage++
                }
                else -> {
                    cntAfterDay = curStage.nextTest - challengeStage.nextTest
                    if (challengeStage != Stage.REVIEW) qst.cur_stage--
                }
            }
        }
        if (goMove) qstRecord.is_correct = isCorrect
        val newNextDate = currentNextDate + MILLIS_A_DAY * cntAfterDay
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