package kr.yangbob.memorization

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.QstCalendar
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.view.MainActivity
import org.koin.core.context.GlobalContext
import kotlin.system.exitProcess

//const val receiversLogTag = "Receivers"
fun workForNextTest(memRepo: MemRepository): Boolean {
    val todayDate = memRepo.getDateStr(System.currentTimeMillis())
    val maxDateStr = memRepo.getCalendarMaxDate()

    if(maxDateStr == null){ // null이면 처음인 것
        val newQstCal = QstCalendar(todayDate)
        memRepo.insertQstCalendar(newQstCal)
        return true
    }

    if (todayDate != maxDateStr) {
        var dateStr: String = maxDateStr
        while(todayDate != dateStr){
            val time = memRepo.getDateLong(dateStr) + MILLIS_A_DAY
            dateStr = memRepo.getDateStr(time)

            val testList: List<Qst> = memRepo.getNeedTestList(dateStr)
            val pair = testList.partition { it.next_test_date == dateStr }
            val noChkList = pair.first.toMutableList()
            val needDormantChkList = pair.second

            for(qst in needDormantChkList){
                if(memRepo.chkDormant(dateStr, qst.next_test_date)){
                    qst.is_dormant = true
                    memRepo.insertQst(qst)
                    continue
                }
                noChkList.add(qst)
            }

            val newQstCal = QstCalendar(dateStr, if(noChkList.isEmpty()) null else false)
            memRepo.insertQstCalendar(newQstCal)

            for (qst in noChkList) {
                val challengeStage = qst.cur_stage + 1
                val newQstRecord = QstRecord(qst.id!!, dateStr, challengeStage)
                memRepo.insertQstRecord( newQstRecord )
            }
        }
        return true
    } else return false
}

class PushAlarmReceiver : BroadcastReceiver(){
    private val notificationId = 22
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context != null){
            val memRepo = GlobalContext.get().koin.get<MemRepository>()
            val notSolvedQstCnt = memRepo.getCntNotSolved(memRepo.getDateStr(System.currentTimeMillis()))
            if(notSolvedQstCnt > 0){
                val mainIntent = Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                val pendIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val notiBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_note_icon)
                    .setContentTitle(context.getString(R.string.today_test))
                    .setContentText(context.resources.getQuantityString(R.plurals.notification_text, notSolvedQstCnt, notSolvedQstCnt))
                    .setWhen(System.currentTimeMillis())
                    .setNumber(notSolvedQstCnt)
//                    .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(pendIntent)

                val notiMgr = NotificationManagerCompat.from(context)
                notiMgr.notify(notificationId, notiBuilder.build())
            }
        }
    }
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
            setTimer(context)
        }
    }
}