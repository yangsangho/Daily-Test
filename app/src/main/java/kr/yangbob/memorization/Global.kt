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
const val TEST_CHK_RECV_ID = 10

const val EXTRA_TO_RESULT_DATESTR = "dateStr"
const val EXTRA_TO_QST_ID = "qstID"

const val SETTING_ENTIRE_SORT_ITEM = "entireSortItem"
const val SETTING_ENTIRE_SORT_ORDER = "entireSortOrder"

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
enum class IconSetting {
    NONE, UP, DOWN
}
data class SortInfo(
        var sortedItemIdx: Int,
        var isAscending: Boolean
)

// Receiver 관련
fun cancelAlarm(context: Context) {
    val pendingIntent = Intent(context, CreateCalendarReceiver::class.java).let {
        PendingIntent.getBroadcast(context, TEST_CHK_RECV_ID, it, PendingIntent.FLAG_NO_CREATE)
    }
    if (pendingIntent != null) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(pendingIntent)
    }
}

fun setTestChkAlarm(context: Context) {
    val testChkIntent = Intent(context, CreateCalendarReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        TEST_CHK_RECV_ID,
        testChkIntent,
        PendingIntent.FLAG_NO_CREATE
    )
    if (pendingIntent == null) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }.timeInMillis

        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime,
            AlarmManager.INTERVAL_DAY,
            PendingIntent.getBroadcast(context, TEST_CHK_RECV_ID, testChkIntent, 0)
        )
    }
}