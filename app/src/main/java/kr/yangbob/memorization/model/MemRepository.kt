package kr.yangbob.memorization.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.yangbob.memorization.MILLIS_A_DAY
import kr.yangbob.memorization.db.*
import java.text.SimpleDateFormat
import java.util.*


class MemRepository(memDB: MemDatabase) {
    private val daoQst: DaoQst = memDB.getDaoQst()
    private val daoQstRecord: DaoQstRecord = memDB.getDaoQstRecord()
    private val daoQstCalendar: DaoQstCalendar = memDB.getDaoQstCalendar()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    ////// Qst
    fun getAllQstLD(): LiveData<List<Qst>> = daoQst.getAllLD()

    fun getQstFromId(id: Int): Qst = runBlocking { daoQst.getFromId(id) }

    fun getNeedTestList(): List<Qst> =
        runBlocking { daoQst.getNeedTesList(getDateStr(System.currentTimeMillis())) }

    suspend fun insertQst(qst: Qst) = daoQst.insert(qst)

    ////// QstCalendar
    private fun getCalendarMinDate(): String? = runBlocking { daoQstCalendar.getMinDate() }

    fun getTodayCalendar(): QstCalendar? =
        runBlocking { daoQstCalendar.getTodayRow(getDateStr(System.currentTimeMillis())) }

    fun getCompletedDateCnt(): Int = runBlocking { daoQstCalendar.getCompletedDateCnt() }
    fun insertQstCalendar(qstCalendar: QstCalendar) =
        GlobalScope.launch { daoQstCalendar.insert(qstCalendar) }

    fun updateCalComplete() =
        runBlocking { daoQstCalendar.updateComplete(getDateStr(System.currentTimeMillis())) }

    ////// QstRecord
    fun getAllRecord(): List<QstRecord> = runBlocking { daoQstRecord.getAll() }

    fun getAllRecordLDFromDate(dateStr: String): LiveData<List<QstRecord>> =
        daoQstRecord.getAllFromDate(dateStr)

    fun getNullRecordsFromDate(dateStr: String): List<QstRecord> =
        runBlocking { daoQstRecord.getNullListFromDate(dateStr) }

    suspend fun insertQstRecord(qstRecord: QstRecord) = daoQstRecord.insert(qstRecord)

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
    fun getDateLong(dateStr: String): Long = dateFormat.parse(dateStr)?.time ?: 0
}