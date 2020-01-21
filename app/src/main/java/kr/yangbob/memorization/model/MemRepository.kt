package kr.yangbob.memorization.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.yangbob.memorization.db.*

class MemRepository(memDB: MemDatabase)
{
    private val daoQst: DaoQst = memDB.getDaoQst()
    private val daoQstRecord: DaoQstRecord = memDB.getDaoQstRecord()
    private val daoQstCalendar: DaoQstCalendar = memDB.getDaoQstCalendar()

    // Qst
    fun getAllQstLD(): LiveData<List<Qst>> = daoQst.getAllLD()
    fun getRegistrationDateCnt(): Int = runBlocking { daoQst.getRegistrationDateCnt() }
    fun getNeedTestCnt(todayDate: Long): Int = runBlocking { daoQst.getNeedTestCnt(todayDate) }
    fun insertQst(qst: Qst) = GlobalScope.launch { daoQst.insert(qst) }

    // QstCalendar
    fun getCalendarFromId(id: Long) = runBlocking { daoQstCalendar.getFromId(id) }
    fun getCalendarCnt(): Int = runBlocking { daoQstCalendar.getCnt() }
    fun getCalendarMinDate(): Long? = runBlocking { daoQstCalendar.getMinDate() }
    fun insertQstCalendar(qstCalendar: QstCalendar) = GlobalScope.launch { daoQstCalendar.insert(qstCalendar) }
}