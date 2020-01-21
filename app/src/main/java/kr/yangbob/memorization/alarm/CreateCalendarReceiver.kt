package kr.yangbob.memorization.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kr.yangbob.memorization.db.QstCalendar
import kr.yangbob.memorization.model.MemRepository
import org.koin.core.context.GlobalContext.get
import java.util.*

class CreateCalendarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 명시적 인텐트로 실행해서 action 체크 없이
        Log.i("TEST", "Start Test Chk Receiver!!")
        val memRepo = get().koin.get<MemRepository>()
        val todayDate = get().koin.get<Calendar>().timeInMillis
        val qstCal = memRepo.getCalendarFromId(todayDate)
        if (qstCal == null) {
            Log.i("TEST", "qstCal is Null !! -> insert QstCalendar!")
            val newQstCal = QstCalendar(todayDate, memRepo.getNeedTestCnt(todayDate))
            memRepo.insertQstCalendar(newQstCal)
        } else {
            Log.i("TEST", "qstCal is Not Null !! -> Skip insert QstCalendar!")
        }
    }
}
