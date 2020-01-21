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
    fun getAllQst(): LiveData<List<Qst>> = daoQst.getAll()
    fun getRegistrationDateCnt(): Int = runBlocking { daoQst.getRegistrationDateCnt() }
    fun insertQst(qst: Qst) = GlobalScope.launch { daoQst.insert(qst) }

    // CalData
    fun getCalendarCnt(): Int = runBlocking { daoQstCalendar.getCalendarCnt() }
    fun getTestCompletionCnt(): Int = runBlocking { daoQstCalendar.getTestCompletionCnt() }
}