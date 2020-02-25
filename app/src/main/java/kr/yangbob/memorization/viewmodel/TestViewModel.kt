package kr.yangbob.memorization.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.MILLIS_A_DAY
import kr.yangbob.memorization.STAGE_LIST
import kr.yangbob.memorization.Stage
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.model.MemRepository

class TestViewModel(private val memRepo: MemRepository) : ViewModel() {
    var isDormant: Boolean = false

    fun getTodayNullRecords() =
        memRepo.getNullRecordsFromDate(memRepo.getDateStr(System.currentTimeMillis()))
    fun insertQst(qst: Qst) = memRepo.insertQst(qst)


    fun getQstFromId(id: Int) = memRepo.getQstFromId(id)
    fun getAllDormantQst() = memRepo.getAllDormantQst()

    fun update(qst: Qst, qstRecord: QstRecord, isCorrect: Boolean): Boolean {
        val challengeStage = STAGE_LIST[qstRecord.challenge_stage]
        val currentNextDate = memRepo.getDateLong(qst.next_test_date)
        val curStage = STAGE_LIST[qst.cur_stage]
        val cntAfterDay: Int
        var goMove = true

        if (qstRecord.is_correct == null) {
            if (isCorrect || challengeStage <= Stage.BEGIN_TWO) {
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
                        if(challengeStage <= Stage.BEGIN_TWO) qst.cur_stage--
                        -curStage.nextTest
                    }
                    qstRecord.is_correct = null
                    goMove = false
                }
                challengeStage <= Stage.BEGIN_TWO -> {
                    qstRecord.is_correct = isCorrect
                    memRepo.insertQstRecord(qstRecord)
                    Log.i("TestViewModel", "$qstRecord")
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
        if(goMove) qstRecord.is_correct = isCorrect
        val newNextDate = currentNextDate + MILLIS_A_DAY * cntAfterDay
        qst.next_test_date = memRepo.getDateStr(newNextDate)

        Log.i("TestViewModel", "$qst")
        Log.i("TestViewModel", "$qstRecord")
        memRepo.insertQst(qst)
        memRepo.insertQstRecord(qstRecord)      // qstRecord를 먼저 insert하니까 사라지네
        return goMove
    }

    fun updateDormant(qst: Qst, qstRecord: QstRecord, isCorrect: Boolean): Boolean{
        var goMove = true
        if (qstRecord.is_correct == null){
            qstRecord.is_correct = isCorrect
            qst.is_dormant = false
            if(!isCorrect){
                qst.cur_stage--
            }
            insertQst(qst)
        } else {
            if(qstRecord.is_correct == isCorrect){
                qstRecord.is_correct = null
                qst.is_dormant = true
                if(!isCorrect){
                    qst.cur_stage++
                }
                insertQst(qst)
            } else {

            }
        }
        return goMove
    }
}