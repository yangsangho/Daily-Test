package kr.yangbob.memorization.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.yangbob.memorization.MILLIS_A_DAY
import kr.yangbob.memorization.Stage
import kr.yangbob.memorization.db.*
import java.text.SimpleDateFormat
import java.util.*


class MemRepository(memDB: MemDatabase) {
    private val daoQst: DaoQst = memDB.getDaoQst()
    private val daoQstRecord: DaoQstRecord = memDB.getDaoQstRecord()
    private val daoQstCalendar: DaoQstCalendar = memDB.getDaoQstCalendar()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val stageList = Stage.values()

////// Qst
    fun getAllQstLD(): LiveData<List<Qst>> = daoQst.getAllLD()

//    private fun getFromId(id: Int): Qst = runBlocking { daoQst.getFromId(id) }
//    fun getNeedTestCnt(): Int =
//        runBlocking { daoQst.getNeedTestCnt(getDateStr(System.currentTimeMillis())) }

    fun getNeedTestList(): List<Qst> =
        runBlocking { daoQst.getNeedTesList(getDateStr(System.currentTimeMillis())) }

    fun insertQst(qst: Qst) = GlobalScope.launch { daoQst.insert(qst) }

////// QstCalendar
    private fun getCalendarMinDate(): String? = runBlocking { daoQstCalendar.getMinDate() }
    fun getCntCalendar(): Int = runBlocking { daoQstCalendar.getCnt() }
    fun getTodayCalendar(): QstCalendar? =
        runBlocking { daoQstCalendar.getTodayRow(getDateStr(System.currentTimeMillis())) }

    fun getCompletedDateCnt(): Int = runBlocking { daoQstCalendar.getCompletedDateCnt() }
    fun insertQstCalendar(qstCalendar: QstCalendar) =
        GlobalScope.launch { daoQstCalendar.insert(qstCalendar) }

////// QstRecord
    fun getRecordCntFromDate(dateStr: String): Int = runBlocking { daoQstRecord.getCntFromDate(dateStr) }
    fun getCorrectCntFromDate(dateStr: String): Int = runBlocking { daoQstRecord.getCorrectCntFromDate(dateStr) }
    fun getLDListFromDate(dateStr: String): LiveData<List<QstRecord>> = daoQstRecord.getLDListFromDate(dateStr)
    fun insertQstRecord(qstRecord: QstRecord) = GlobalScope.launch { daoQstRecord.insert(qstRecord) }
    fun deleteNoneSolvedRecord() = GlobalScope.launch { daoQstRecord.deleteNoneSolved() }

////// Others
    fun getEntireDate(): Int {
        val minDate = getCalendarMinDate()?.let {
            dateFormat.parse(it)?.time
        } ?: 0
        return if (minDate > 0) {
            val curDate = dateFormat.parse(getDateStr(System.currentTimeMillis()))?.time ?: 0
            ((curDate - minDate) / MILLIS_A_DAY).toInt() + 1
        } else {
            0
        }
    }
    fun getDateStr(timeMillis: Long): String = dateFormat.format(Date(timeMillis))
}