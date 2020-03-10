package kr.yangbob.memorization.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.yangbob.memorization.MILLIS_A_DAY
import kr.yangbob.memorization.dateFormat
import kr.yangbob.memorization.db.*
import kr.yangbob.memorization.todayDateStr
import java.text.DateFormat
import java.util.*


class MemRepository(memDB: MemDatabase) {
    private val daoQst: DaoQst = memDB.getDaoQst()
    private val daoQstRecord: DaoQstRecord = memDB.getDaoQstRecord()
    private val daoQstCalendar: DaoQstCalendar = memDB.getDaoQstCalendar()

    ////// Qst
    fun getAllQstLD(): LiveData<List<Qst>> = daoQst.getAllLD()

    fun getQstFromId(id: Int): Qst = runBlocking { daoQst.getFromId(id) }

    fun getNeedTestList(dateStr: String): List<Qst> =
            runBlocking { daoQst.getNeedTesList(dateStr) }

    fun insertQst(qst: Qst) = runBlocking { daoQst.insert(qst) }

    fun getAllDormantQstLD(): LiveData<List<Qst>> = daoQst.getAllDormantLD()

    fun getAllDormantQst(): List<Qst> = runBlocking { daoQst.getAllDormant() }

    fun deleteQst(qst: Qst) = GlobalScope.launch(Dispatchers.IO) {
        daoQst.delete(qst)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////// QstCalendar
    fun getCalTestComplete(calendarId: String): Boolean? = runBlocking { daoQstCalendar.getTestComplete(calendarId) }

    fun getAllCalendarLD(): LiveData<List<QstCalendar>> = daoQstCalendar.getAllLD()

    fun getAllInfoCalendar(): List<InfoCalendar> = runBlocking {
        val list = daoQstCalendar.getAll()
        val infoCalList = list.map { qstCalendar ->
            val dateStr = qstCalendar.id
            val yearMonth = "${dateStr.substring(0, 4)}${dateStr.substring(5, 7)}".toInt()
            val date = dateStr.substring(8).toInt()

            InfoCalendar(
                    dateStr, yearMonth, date,
                    qstCalendar.test_completion
            )
        }
        infoCalList.first().isStartDay = true
        infoCalList
    }

    fun getStartDateStr(): String = runBlocking { daoQstCalendar.getStartDateStr() }

    fun getCalendarMaxDate(): String? = runBlocking { daoQstCalendar.getMaxDate() }

    fun getCompletedDateCnt(): Int = runBlocking { daoQstCalendar.getCompletedDateCnt() }

    fun insertQstCalendar(qstCalendar: QstCalendar) =
            runBlocking { daoQstCalendar.insert(qstCalendar) }

    fun updateCalComplete() =
            runBlocking {
                val qstCalendar = daoQstCalendar.getFromId(todayDateStr)
                if (qstCalendar.test_completion != null) {
                    qstCalendar.test_completion = true
                    daoQstCalendar.insert(qstCalendar)
                }
            }

    fun getCalCntHasTest(): Int = runBlocking { daoQstCalendar.getCntHasTest() }

    fun getCalCnt(): Int = runBlocking { daoQstCalendar.getCnt() }

    fun updateCal(id: String, isComplete: Boolean?) = GlobalScope.launch(Dispatchers.IO) {
        daoQstCalendar.update(id, isComplete)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////// QstRecord
    fun getAllRecordLD(): LiveData<List<QstRecord>> = daoQstRecord.getAllLD()

    fun getAllRecordFromDate(calendarId: String): List<QstRecord> = runBlocking { daoQstRecord.getAllFromDate(calendarId) }

    fun getAllRecordFromId(qstId: Int): List<QstRecord> = runBlocking { daoQstRecord.getAllFromId(qstId) }

    fun getAllRecordWithName(calendarId: String): LiveData<List<QstRecordWithName>> =
            daoQstRecord.getAllWithName(calendarId)

    fun getAllRecordLDFromDate(calendarId: String): LiveData<List<QstRecord>> =
            daoQstRecord.getAllLDFromDate(calendarId)

    fun getNullRecordsFromDate(calendarId: String): List<QstRecord> =
            runBlocking { daoQstRecord.getNullListFromDate(calendarId) }

    fun insertQstRecord(qstRecord: QstRecord) = runBlocking { daoQstRecord.insert(qstRecord) }

    fun deleteQstRecord(qstRecord: QstRecord) = GlobalScope.launch(Dispatchers.IO) {
        daoQstRecord.delete(qstRecord)
    }

    fun getCntRecord(calendarId: String): Int = runBlocking { daoQstRecord.getCnt(calendarId) }

    fun getCntNotSolved(calendarId: String): Int = runBlocking { daoQstRecord.getCntNotSolved(calendarId) }
//    fun deleteNoneSolvedRecord() = runBlocking { daoQstRecord.deleteNoneSolved() }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////// Others
    private val dormantBasisDay = 3

    fun chkDormant(today: String, dateStr: String): Boolean {
        val todayTime = getDateLong(today)
        val targetTime = getDateLong(dateStr)
        return ((todayTime - targetTime) / MILLIS_A_DAY) >= dormantBasisDay
    }

    fun getDateStr(timeMillis: Long): String = dateFormat.format(Date(timeMillis))

    fun getDateLong(dateStr: String): Long = dateFormat.parse(dateStr)?.time ?: 0

    // type = DateFormat.FULL 등의 상수
    fun getFormattedDate(dateStr: String, style: Int): String {
        val time = getDateLong(dateStr)
        val formatter = DateFormat.getDateInstance(style)
        //        formatter.timeZone = 나중에 추가가 필요할 수도
        return formatter.format(time)
    }
}