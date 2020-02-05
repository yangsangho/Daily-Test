package kr.yangbob.memorization.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.runBlocking
import kr.yangbob.memorization.MILLIS_A_DAY
import kr.yangbob.memorization.dateFormat
import kr.yangbob.memorization.db.*
import java.text.DateFormat
import java.util.*


class MemRepository(memDB: MemDatabase) {
    private val daoQst: DaoQst = memDB.getDaoQst()
    private val daoQstRecord: DaoQstRecord = memDB.getDaoQstRecord()
    private val daoQstCalendar: DaoQstCalendar = memDB.getDaoQstCalendar()

    ////// Qst
    fun getAllQstLD(): LiveData<List<Qst>> = daoQst.getAllLD()

    fun getQstFromId(id: Int): Qst = runBlocking { daoQst.getFromId(id) }

    fun getNeedTestList(): List<Qst> =
        runBlocking { daoQst.getNeedTesList(getDateStr(System.currentTimeMillis())) }

    fun insertQst(qst: Qst) = runBlocking { daoQst.insert(qst) }

    ////// QstCalendar
//    fun getAllCalendar(): List<QstCalendar> = runBlocking { daoQstCalendar.getAll() }

    private fun getCalendarMinDate(): String? = runBlocking { daoQstCalendar.getMinDate() }

    fun getTodayCalendar(): QstCalendar? =
        runBlocking { daoQstCalendar.getTodayRow(getDateStr(System.currentTimeMillis())) }

    fun getCompletedDateCnt(): Int = runBlocking { daoQstCalendar.getCompletedDateCnt() }

    fun insertQstCalendar(qstCalendar: QstCalendar) =
        runBlocking { daoQstCalendar.insert(qstCalendar) }

    fun updateCalComplete() =
        runBlocking { daoQstCalendar.updateComplete(getDateStr(System.currentTimeMillis())) }

    ////// QstRecord
//    fun getAllRecord(): List<QstRecord> = runBlocking { daoQstRecord.getAll() }
    fun getAllRecordFromId(id: Int): List<QstRecord> = runBlocking { daoQstRecord.getAllFromId(id) }

    fun getAllRecordWithName(dateStr: String): LiveData<List<QstRecordWithName>> =
        daoQstRecord.getAllWithName(dateStr)

    fun getAllRecordLDFromDate(dateStr: String): LiveData<List<QstRecord>> =
        daoQstRecord.getAllFromDate(dateStr)

    fun getNullRecordsFromDate(dateStr: String): List<QstRecord> =
        runBlocking { daoQstRecord.getNullListFromDate(dateStr) }

    fun insertQstRecord(qstRecord: QstRecord) = runBlocking { daoQstRecord.insert(qstRecord) }

    fun deleteNoneSolvedRecord() = runBlocking { daoQstRecord.deleteNoneSolved() }

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

    // type = DateFormat.FULL 등의 상수
    fun getFormattedDate(dateStr: String, style: Int): String{
        val time = getDateLong(dateStr)
        val formatter = DateFormat.getDateInstance(style)
        //        formatter.timeZone = 나중에 추가가 필요할 수도
        return formatter.format(time)
    }
}