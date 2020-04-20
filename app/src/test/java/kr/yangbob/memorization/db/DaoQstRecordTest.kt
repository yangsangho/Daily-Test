package kr.yangbob.memorization.db

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import kr.yangbob.memorization.data.SimpleDate
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class DaoQstRecordTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var db: MemDatabase
    private lateinit var dao: DaoQstRecord

    private val date1 = SimpleDate.newInstanceFromDateInt(20200418)
    private val date2 = SimpleDate.newInstanceFromDateInt(20200419)
    private val date3 = SimpleDate.newInstanceFromDateInt(20200420)
    private val date4 = SimpleDate.newInstanceFromDateInt(20200421)
    private val inputCalendarList = listOf(
            QstCalendar(date1),
            QstCalendar(date2),
            QstCalendar(date3)
    )
    private val inputQstList = listOf(
            Qst("문제1", "정답1", date1, date3, 1, id = 1),
            Qst("문제2", "정답2", date1, date4, 2, id = 2),
            Qst("문제3", "정답3", date2, date3, 0, id = 3),
            Qst("문제4", "정답4", date2, date4, 1, id = 4)
    )
    private val inputRecordList = listOf(
            QstRecord(1, date2, 1, true, id = 1),
            QstRecord(2, date2, 1, true, id = 3),
            QstRecord(1, date3, 2, id = 2),
            QstRecord(2, date3, 2, true, id = 4),
            QstRecord(3, date3, 1, id = 5),
            QstRecord(4, date3, 1, true, id = 6)
    )

    @Before
    fun before() {
        stopKoin()
        db = Room.inMemoryDatabaseBuilder(context, MemDatabase::class.java).build()
        dao = db.getDaoQstRecord()

        runBlocking {
            inputCalendarList.forEach {
                db.getDaoQstCalendar().insert(it)
            }
            inputQstList.forEach {
                db.getDaoQst().insert(it)
            }
            inputRecordList.forEach {
                dao.insert(it)
            }
        }
    }

    @After
    fun after() {
        db.close()
    }

    @Test
    fun getFromDateTest() {
        runBlocking {
            checkGetFromDate(date2, 2, 0)
            checkGetFromDate(date3, 4, 2)
        }
    }

    private suspend fun checkGetFromDate(date: SimpleDate, listSize: Int, addIndex: Int) {
        val getFromDateList = dao.getAllFromDate(date)

        assertThat(getFromDateList.size, `is`(listSize))

        getFromDateList.forEachIndexed { index, qstRecord ->
            assertThat(qstRecord, `is`(inputRecordList[index + addIndex]))
        }

        assertThat(dao.getAllFromDateLD(date), Matchers.notNullValue())
    }

    @Test
    fun getNullListTest() {
        runBlocking {
            val getNullList = dao.getNullListFromDate(date3)
            assertThat(getNullList.size, `is`(2))
            assertThat(getNullList[0], `is`(inputRecordList[2]))
            assertThat(getNullList[1], `is`(inputRecordList[4]))
        }
    }

    @Test
    fun getAllWithNameTest() {
        runBlocking {
            checkGetAllWithName(date2, 2)
            checkGetAllWithName(date3, 4)
        }
    }

    private suspend fun checkGetAllWithName(date: SimpleDate, listSize: Int) {
        val getAllWithNameList = dao.getAllWithName(date)

        assertThat(getAllWithNameList.size, `is`(listSize))

        getAllWithNameList.forEachIndexed { index, qstRecordWithName ->
            assertThat(qstRecordWithName.qst_name, `is`(inputQstList[index].title))
        }

        assertThat(dao.getAllWithNameLD(date), Matchers.notNullValue())
    }

    @Test
    fun getFromIdTest() {
        runBlocking {
            var getFromIdList = dao.getAllFromId(1)
            assertThat(getFromIdList.size, `is`(2))
            assertThat(getFromIdList[0], `is`(inputRecordList[0]))
            assertThat(getFromIdList[1], `is`(inputRecordList[2]))

            getFromIdList = dao.getAllFromId(2)
            assertThat(getFromIdList.size, `is`(2))
            assertThat(getFromIdList[0], `is`(inputRecordList[1]))
            assertThat(getFromIdList[1], `is`(inputRecordList[3]))
        }
    }

    @Test
    fun getCntTest() {
        runBlocking {
            assertThat(dao.getCnt(date2), `is`(2))
            assertThat(dao.getCnt(date3), `is`(4))
        }
    }

    @Test
    fun getCntNotSolvedTest() {
        runBlocking {
            assertThat(dao.getCntNotSolved(date2), `is`(0))
            assertThat(dao.getCntNotSolved(date3), `is`(2))
        }
    }

    @Test
    fun deleteTest() {
        runBlocking {
            inputRecordList.forEach {
                dao.delete(it)
            }

            assertThat(dao.getCnt(date2), `is`(0))
            assertThat(dao.getCnt(date3), `is`(0))
        }
    }

    @Test
    fun updateTest() {
        val cloneInputData = List(inputRecordList.size) { index ->
            inputRecordList[index].copy()
        }
        runBlocking {
            cloneInputData.forEach {
                it.is_correct = false
                dao.insert(it)
            }

            checkIsCorrect(date1)
            checkIsCorrect(date2)
            checkIsCorrect(date3)
        }
    }

    private suspend fun checkIsCorrect(date: SimpleDate) {
        dao.getAllFromDate(date).forEach {
            assertThat(it.is_correct, `is`(false))
        }
    }
}