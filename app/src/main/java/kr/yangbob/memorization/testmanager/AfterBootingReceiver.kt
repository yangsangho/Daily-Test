package kr.yangbob.memorization.testmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

fun setTestChkAlarm(context: Context){
    val testChkIntent = Intent(context, TestChkReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, TEST_CHK_RECV_ID, testChkIntent, PendingIntent.FLAG_NO_CREATE)
    if( pendingIntent == null ) {
        Log.i("TEST", "PendingIntent is Null -> Set Alarm!")
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val startTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 10)
        }.timeInMillis
        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    } else {
        Log.i("TEST", "PendingIntent is Not Null -> Skip Set Alarm!")
    }
}

class AfterBootingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
//            setTestChkAlarm(context)
        }
    }
}
