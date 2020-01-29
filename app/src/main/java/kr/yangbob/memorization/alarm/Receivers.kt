package kr.yangbob.memorization.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kr.yangbob.memorization.Stage
import kr.yangbob.memorization.db.QstCalendar
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.setTestChkAlarm
import org.koin.core.context.GlobalContext
import kotlin.system.exitProcess

const val receiversLogTag = "Receivers"
fun workForNextTest(memRepo: MemRepository) {
    val qstCal = memRepo.getTodayCalendar()
    if (qstCal == null) {
        Log.i(receiversLogTag, "qstCal is Null !! -> insert QstCalendar!")

        // <안 푼 문제 기록 삭제>
        memRepo.deleteNoneSolvedRecord()

        // 새로운 날짜 데이터 입력
        val todayDate = memRepo.getDateStr(System.currentTimeMillis())
        val testList = memRepo.getNeedTestList()
        Log.i(receiversLogTag, "todayDate = $todayDate, testList.size = ${testList.size}, testList.isEmpty = ${testList.isEmpty()}")
        val newQstCal = QstCalendar(todayDate, testList.size, testList.isEmpty())
        memRepo.insertQstCalendar(newQstCal)

        // 시험 Record 추가
        for(qst in testList){
            val challengeStage = if(qst.cur_stage != Stage.AFTER_MONTH.ordinal) qst.cur_stage + 1 else qst.cur_stage
            Log.i(receiversLogTag, "qst.id = ${qst.id}, challengeStage = $challengeStage")
            memRepo.insertQstRecord( QstRecord(qst.id!!, todayDate, challengeStage) )
        }
    } else {
        Log.i(receiversLogTag, "qstCal is Not Null -> Skip insert QstCalendar -> id = ${qstCal.id}")
    }
}

class CreateCalendarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 명시적 인텐트로 실행해서 action 체크 없이
        Log.i(receiversLogTag, "Start Test Chk Receiver!!")
        val memRepo = GlobalContext.get().koin.get<MemRepository>()
        workForNextTest(memRepo)
        System.runFinalization()
        exitProcess(0)
    }
}

class AfterBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i(receiversLogTag, "Start AfterBootingReceiver!")
            setTestChkAlarm(context)
        }
    }
}