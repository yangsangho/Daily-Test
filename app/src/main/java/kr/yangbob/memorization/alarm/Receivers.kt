package kr.yangbob.memorization.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kr.yangbob.memorization.db.QstCalendar
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.setTestChkAlarm
import org.koin.core.context.GlobalContext
import kotlin.system.exitProcess


class CreateCalendarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 명시적 인텐트로 실행해서 action 체크 없이
        Log.i("yangtest", "Start Test Chk Receiver!!")
        val memRepo = GlobalContext.get().koin.get<MemRepository>()

        val qstCal = memRepo.getTodayCalendar()
        if (qstCal == null) {
            Log.i("yangtest", "qstCal is Null !! -> insert QstCalendar!")
            val todayDate = memRepo.getDateStr( System.currentTimeMillis() )
            val newQstCal = QstCalendar(todayDate, memRepo.getNeedTestCnt())
            memRepo.insertQstCalendar(newQstCal)
            System.runFinalization()
            exitProcess(0)
        } else {
            Log.i("yangtest", "qstCal is Not Null -> Skip insert QstCalendar -> id = ${qstCal.id}")
        }
    }
}

class AfterBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("yangtest", "Start AfterBootingReceiver!")
            setTestChkAlarm(context)
        }
    }
}