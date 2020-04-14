package kr.yangbob.memorization.model

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.yangbob.memorization.data.SimpleDate
import kr.yangbob.memorization.db.*
import kr.yangbob.memorization.todayDate

class MemRepository(memDB: MemDatabase, private val settings: SharedPreferences) {
    private val daoQst: DaoQst = memDB.getDaoQst()
    private val daoQstRecord: DaoQstRecord = memDB.getDaoQstRecord()
    private val daoQstCalendar: DaoQstCalendar = memDB.getDaoQstCalendar()

    ////// Qst
    fun chkDuplication(title: String): Boolean = runBlocking {
        daoQst.getFromTitle(title) != null
    }

    fun getAllQstLD(): LiveData<List<Qst>> = daoQst.getAllLD()

    fun getQstFromId(id: Int): Qst = runBlocking { daoQst.getFromId(id) }

    fun getNeedTestList(date: SimpleDate): List<Qst> =
            runBlocking { daoQst.getNeedTesList(date) }

    fun insertQst(qst: Qst) = runBlocking { daoQst.insert(qst) }

    fun getAllDormantQstLD(): LiveData<List<Qst>> = daoQst.getAllDormantLD()

    fun getAllDormantQst(): List<Qst> = runBlocking { daoQst.getAllDormant() }

    fun deleteQst(qst: Qst) = GlobalScope.launch(Dispatchers.IO) {
        daoQst.delete(qst)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////// QstCalendar
    fun getTestCompletionOnDate(calendarId: SimpleDate): Boolean? = runBlocking { daoQstCalendar.getTestCompletionOnDate(calendarId) }

    fun getAllInfoCalendar(): List<InfoCalendar> = runBlocking {
        val list = daoQstCalendar.getAll()
        val infoCalList = list.map { qstCalendar ->
            InfoCalendar(qstCalendar.id, qstCalendar.test_completion)
        }
        infoCalList.first().isStartDay = true
        infoCalList
    }

    fun getStartDate(): SimpleDate = runBlocking { daoQstCalendar.getStartDate() }

    fun getCalendarMaxDate(): SimpleDate? = runBlocking { daoQstCalendar.getMaxDate() }

    fun getCompletedDateCnt(): Int = runBlocking { daoQstCalendar.getCompletedDateCnt() }

    fun insertQstCalendar(qstCalendar: QstCalendar) =
            runBlocking { daoQstCalendar.insert(qstCalendar) }

    fun updateCalComplete(isComplete: Boolean?) = runBlocking {
        val qstCalendar = daoQstCalendar.getFromId(todayDate)
        qstCalendar.test_completion = isComplete
        daoQstCalendar.insert(qstCalendar)
    }

    fun getCalCntHasTest(): Int = runBlocking { daoQstCalendar.getCntHasTest() }

    fun getCalCnt(): Int = runBlocking { daoQstCalendar.getCnt() }

    fun updateCal(id: SimpleDate, isComplete: Boolean?) = GlobalScope.launch(Dispatchers.IO) {
        daoQstCalendar.update(id, isComplete)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////// QstRecord
    fun getAllRecordFromDate(calendarId: SimpleDate): List<QstRecord> = runBlocking { daoQstRecord.getAllFromDate(calendarId) }

    fun getAllRecordFromId(qstId: Int): List<QstRecord> = runBlocking { daoQstRecord.getAllFromId(qstId) }

    fun getAllRecordWithName(calendarId: SimpleDate): LiveData<List<QstRecordWithName>> =
            daoQstRecord.getAllWithName(calendarId)

    fun getAllRecordLDFromDate(calendarId: SimpleDate): LiveData<List<QstRecord>> =
            daoQstRecord.getAllLDFromDate(calendarId)

    fun getNullRecordsFromDate(calendarId: SimpleDate): List<QstRecord> =
            runBlocking { daoQstRecord.getNullListFromDate(calendarId) }

    fun insertQstRecord(qstRecord: QstRecord) = runBlocking { daoQstRecord.insert(qstRecord) }

    fun deleteQstRecord(qstRecord: QstRecord) = GlobalScope.launch(Dispatchers.IO) {
        daoQstRecord.delete(qstRecord)
    }

    fun getCntRecord(calendarId: SimpleDate): Int = runBlocking { daoQstRecord.getCnt(calendarId) }

    fun getCntNotSolved(calendarId: SimpleDate): Int = runBlocking { daoQstRecord.getCntNotSolved(calendarId) }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////// settings
    fun getIsFirst(what: String): Boolean = settings.getBoolean(what, true)
    fun setFirstValueFalse(what: String) {
        val editor = settings.edit()
        editor.putBoolean(what, false)
        editor.apply()
    }
}