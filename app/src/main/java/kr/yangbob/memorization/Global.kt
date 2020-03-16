package kr.yangbob.memorization

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*

const val globalLogTag = "Global"
const val MILLIS_A_DAY = 24 * 60 * 60 * 1000
const val ANIMATION_HALF_TIME: Long = 400
const val ANIMATION_FULL_TIME: Long = 800
const val CREATE_CAL_RECV_ID = 10
const val PUSH_ALARM_RECV_ID = 11

const val EXTRA_TO_RESULT_DATESTR = "dateStr"
const val EXTRA_TO_QST_ID = "qstID"
const val NOTIFICATION_CHANNEL_ID = "noti_channel_id"
const val NOTIFICATION_CHANNEL_NAME = "noti_channel_name"

const val SETTING_ENTIRE_SORT_ITEM = "entireSortItem"
const val SETTING_ENTIRE_SORT_ORDER = "entireSortOrder"
const val SETTING_RESULT_SORT_ITEM = "resultSortItem"
const val SETTING_RESULT_SORT_ORDER = "resultSortOrder"
const val SETTING_IS_FIRST_MAIN = "firstMain"

val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
val todayDateStr: String = dateFormat.format(Date(System.currentTimeMillis()))
val tomorrowDateStr: String = dateFormat.format(Date(System.currentTimeMillis() + MILLIS_A_DAY))
val todayTime: Long = dateFormat.parse(todayDateStr)?.time ?: 0

val STAGE_LIST = Stage.values()

enum class Stage(val nextTest: Int) {
    INIT(1),
    BEGIN_ONE(1), BEGIN_TWO(1), BEGIN_THREE(3), AFTER_THREE(7),
    AFTER_WEEK(15), AFTER_HALF(30), AFTER_MONTH(30), REVIEW(30)
}

data class SortInfo(
    var sortedItemIdx: Int,
    var isAscending: Boolean
)

// Receiver 관련
fun cancelAlarm(context: Context) {
    val pendingIntent = Intent(context, CreateCalendarReceiver::class.java).let {
        PendingIntent.getBroadcast(context, CREATE_CAL_RECV_ID, it, PendingIntent.FLAG_NO_CREATE)
    }
    if (pendingIntent != null) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(pendingIntent)
    }
}

fun setTimer(context: Context) {
    val createCalIntent = Intent(context, CreateCalendarReceiver::class.java)
    val pushAlarmIntent = Intent(context, PushAlarmReceiver::class.java)
    val ccPendingIntent = PendingIntent.getBroadcast(
        context,
        CREATE_CAL_RECV_ID,
        createCalIntent,
        PendingIntent.FLAG_NO_CREATE
    )
    val paPendingIntent = PendingIntent.getBroadcast(
        context,
        PUSH_ALARM_RECV_ID,
        pushAlarmIntent,
        PendingIntent.FLAG_NO_CREATE
    )

    if(paPendingIntent == null){
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
        }.timeInMillis

        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime,
            AlarmManager.INTERVAL_HALF_DAY,
            PendingIntent.getBroadcast(context, PUSH_ALARM_RECV_ID, pushAlarmIntent, 0)
        )
    }

    if (ccPendingIntent == null) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }.timeInMillis

        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime,
            AlarmManager.INTERVAL_DAY,
            PendingIntent.getBroadcast(context, CREATE_CAL_RECV_ID, createCalIntent, 0)
        )
    }
}