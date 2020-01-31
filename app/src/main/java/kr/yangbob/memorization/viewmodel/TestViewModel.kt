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
    fun getTodayNullRecords() =
        memRepo.getNullRecordsFromDate(memRepo.getDateStr(System.currentTimeMillis()))

    fun getQstFromId(id: Int) = memRepo.getQstFromId(id)

    fun update(qst: Qst, qstRecord: QstRecord, isCorrect: Boolean) {
        val challengeStage = STAGE_LIST[qstRecord.challenge_stage]
        val currentNextDate = memRepo.getDateLong(qst.next_test_date)
        val curStage = STAGE_LIST[qst.cur_stage]
        val cntAfterDay: Int

        if (qstRecord.is_correct == null) {
            if (isCorrect || challengeStage <= Stage.BEGIN_TWO) {
                cntAfterDay = challengeStage.nextTest
                if (challengeStage != Stage.REVIEW) qst.cur_stage = qstRecord.challenge_stage
            } else {
                cntAfterDay = curStage.nextTest
            }
        } else {
            when {
                challengeStage <= Stage.BEGIN_TWO -> {
                    qstRecord.is_correct = isCorrect
                    memRepo.insertQstRecord(qstRecord)
                    Log.i("TestViewModel", "$qstRecord")
                    return
                }
                isCorrect -> {
                    cntAfterDay = challengeStage.nextTest - curStage.nextTest
                    if (challengeStage != Stage.REVIEW) qst.cur_stage = qstRecord.challenge_stage
                }
                else -> {
                    cntAfterDay = curStage.nextTest - challengeStage.nextTest
                    if (challengeStage != Stage.REVIEW) qst.cur_stage--
                }
            }
        }
        qstRecord.is_correct = isCorrect
        val newNextDate = currentNextDate + MILLIS_A_DAY * cntAfterDay
        qst.next_test_date = memRepo.getDateStr(newNextDate)

        Log.i("TestViewModel", "$qst")
        Log.i("TestViewModel", "$qstRecord")
        memRepo.insertQst(qst)
        memRepo.insertQstRecord(qstRecord)      // qstRecord를 먼저 insert하니까 사라지네
    }
}