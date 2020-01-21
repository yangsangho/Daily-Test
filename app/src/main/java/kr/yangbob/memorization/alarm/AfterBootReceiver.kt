package kr.yangbob.memorization.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

const val TEST_CHK_RECV_ID = 10
fun cancelAlarm(context: Context){
    val pendingIntent = Intent(context, CreateCalendarReceiver::class.java).let {
        PendingIntent.getBroadcast(context, TEST_CHK_RECV_ID, it, PendingIntent.FLAG_NO_CREATE)
    }
    if(pendingIntent != null){
        Log.i("TEST", "PendingIntent is Not Null -> Cancel Alarm!")
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmMgr.cancel(pendingIntent)
    } else {
        Log.i("TEST", "PendingIntent is Null -> Skip Cancel Alarm!")
    }
}
fun setTestChkAlarm(context: Context){
    val testChkIntent = Intent(context, CreateCalendarReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, TEST_CHK_RECV_ID, testChkIntent, PendingIntent.FLAG_NO_CREATE)
    if( pendingIntent == null ) {
        Log.i("TEST", "PendingIntent is Null -> Set Alarm!")
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
        }.timeInMillis

        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime,
            AlarmManager.INTERVAL_DAY,
            PendingIntent.getBroadcast(context, TEST_CHK_RECV_ID, testChkIntent, 0)
        )
    } else {
        Log.i("TEST", "PendingIntent is Not Null -> Skip Set Alarm!")
    }
}

class AfterBootingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Log.i("TEST", "Start AfterBootingReceiver!")
            setTestChkAlarm(context)
        }
    }
}
