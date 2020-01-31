package kr.yangbob.memorization.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kr.yangbob.memorization.db.QstCalendar
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.setTestChkAlarm
import org.koin.core.context.GlobalContext
import kotlin.system.exitProcess

//const val receiversLogTag = "Receivers"
fun workForNextTest(memRepo: MemRepository): Boolean {
    val qstCal = memRepo.getTodayCalendar()
    if (qstCal == null) {
        // <안 푼 문제 기록 삭제>
        memRepo.deleteNoneSolvedRecord()

        // 새로운 날짜 데이터 입력
        val todayDate = memRepo.getDateStr(System.currentTimeMillis())
        val testList = memRepo.getNeedTestList()

        val newQstCal = QstCalendar(todayDate, testList.size, testList.isEmpty())
        memRepo.insertQstCalendar(newQstCal)

        // 시험 Record 추가
        for (qst in testList) {
            Log.i("workForNext", "<INSERT_QST_RECORD> : $qst")
            val challengeStage = qst.cur_stage + 1
            val newQstRecord = QstRecord(qst.id!!, todayDate, challengeStage)
            memRepo.insertQstRecord( newQstRecord )
        }
        return true
    } else return false
}

class CreateCalendarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 명시적 인텐트로 실행해서 action 체크 없이
        val memRepo = GlobalContext.get().koin.get<MemRepository>()
        if (workForNextTest(memRepo)) {
            System.runFinalization()
            exitProcess(0)
        }
    }
}

class AfterBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            setTestChkAlarm(context)
        }
    }
}