package kr.yangbob.memorization.model

import android.app.Application
import kotlinx.coroutines.runBlocking
import kr.yangbob.memorization.db.*

class MemRepository(application: Application)
{
    private val memDB = MemDatabase.getInstance(application)!!
    private val qst: QstDao = memDB.getQnaDao()
    private val test: TestDao = memDB.getTestDao()
    private val track: TrackDao = memDB.getTrackDao()

    fun getAllQnA(): List<Qst> = runBlocking { qst.getAll() }
    fun getAllTest(): List<Test> = runBlocking { test.getAll() }
    fun getAllIncTitle(): List<TestIncTitle> = runBlocking { test.getAllIncTitle() }
}