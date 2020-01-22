package kr.yangbob.memorization.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.yangbob.memorization.MILLIS_A_DAY
import kr.yangbob.memorization.db.*
import java.text.SimpleDateFormat
import java.util.*


class MemRepository(memDB: MemDatabase)
{
    private val daoQst: DaoQst = memDB.getDaoQst()
    private val daoQstRecord: DaoQstRecord = memDB.getDaoQstRecord()
    private val daoQstCalendar: DaoQstCalendar = memDB.getDaoQstCalendar()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Qst
    fun getAllQstLD(): LiveData<List<Qst>> = daoQst.getAllLD()
    fun getNeedTestLD(): LiveData<List<Qst>> = daoQst.getNeedTestLD( getDateStr(System.currentTimeMillis()) )
//    fun getRegistrationDateCnt(): Int = runBlocking { daoQst.getRegistrationDateCnt() }
    fun getNeedTestCnt(): Int = runBlocking { daoQst.getNeedTestCnt( getDateStr(System.currentTimeMillis()) ) }
    fun insertQst(qst: Qst) = GlobalScope.launch { daoQst.insert(qst) }

    // QstCalendar
    private fun getCalendarMinDate(): String? = runBlocking { daoQstCalendar.getMinDate() }
    fun getTodayCalendar(): QstCalendar? = runBlocking { daoQstCalendar.getTodayRow( getDateStr(System.currentTimeMillis()) ) }
    fun getCompletedTestCnt(): Int = runBlocking { daoQstCalendar.getCompletedDateCnt() }
    fun insertQstCalendar(qstCalendar: QstCalendar) = GlobalScope.launch { daoQstCalendar.insert(qstCalendar) }

    // QstRecord
    fun getTodayRecordCnt(): Int = runBlocking { daoQstRecord.getTodayCnt() }

    // other
    fun getEntireDate(): Int {
        val minDate = getCalendarMinDate()?.let {
            dateFormat.parse(it)?.time
        } ?: 0
        return if(minDate > 0) {
            val curDate = dateFormat.parse( getDateStr( System.currentTimeMillis()))?.time ?: 0
            ((curDate - minDate) / MILLIS_A_DAY).toInt() + 1
        } else {
            0
        }
    }
    fun getDateStr(timeMillis: Long): String = dateFormat.format( Date(timeMillis) )
}