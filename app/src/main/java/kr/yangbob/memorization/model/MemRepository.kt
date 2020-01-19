package kr.yangbob.memorization.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.yangbob.memorization.db.*

class MemRepository(memDB: MemDatabase)
{
    private val qstDao: QstDao = memDB.getQstDao()
    private val testDao: TestDao = memDB.getTestDao()
    private val calDataDao: CalDataDao = memDB.getCalDataDao()

    // Qst
    fun getAllQst(): LiveData<List<Qst>> = qstDao.getAll()
    fun getRegistrationDateCnt(): Int = runBlocking { qstDao.getRegistrationDateCnt() }
    fun insertQst(qst: Qst) = GlobalScope.launch { qstDao.insert(qst) }

    // CalData
    fun getTestCnt(): Int = runBlocking { calDataDao.getTestCnt() }
    fun getTestCompletionCnt(): Int = runBlocking { calDataDao.getTestCompletionCnt() }
}