package kr.yangbob.memorization.db

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import kr.yangbob.memorization.data.SimpleDate
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class DaoQstCalendarTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var db: MemDatabase
    private lateinit var dao: DaoQstCalendar
    private val inputCalIdList = listOf(
            SimpleDate.newInstanceFromDateInt(20200418),
            SimpleDate.newInstanceFromDateInt(20200419),
            SimpleDate.newInstanceFromDateInt(20200420)
    )
    private val inputCalDataList = listOf(
            QstCalendar(inputCalIdList[0]),
            QstCalendar(inputCalIdList[1], false),
            QstCalendar(inputCalIdList[2], true)
    )

    @Before
    fun before() {
        stopKoin()
        db = Room.inMemoryDatabaseBuilder(context, MemDatabase::class.java).build()
        dao = db.getDaoQstCalendar()

        runBlocking {
            inputCalDataList.forEach {
                dao.insert(it)
            }
        }
    }

    @After
    fun after() {
        db.close()
    }

    @Test
    fun getFromIdTest() {
        runBlocking {
            for (index in 0..2) {
                val cal = dao.getFromId(inputCalIdList[index])
                assertThat(cal, `is`(inputCalDataList[index]))
            }
        }
    }

    @Test
    fun getAllTest() {
        runBlocking {
            val calList = dao.getAll()

            assertThat(calList.size, `is`(3))

            calList.forEachIndexed { index, qstCalendar ->
                assertThat(qstCalendar, `is`(inputCalDataList[index]))
            }

            assertThat(dao.getAllLD(), notNullValue())
        }
    }

    @Test
    fun insertForUpdateTest() {
        runBlocking {
            val cloneInputData = List(inputCalDataList.size) { index ->
                inputCalDataList[index].copy()
            }

            cloneInputData[0].test_completion = false
            cloneInputData[1].test_completion = true
            cloneInputData[2].test_completion = false

            cloneInputData.forEach {
                dao.insert(it)
            }

            dao.getAll().forEachIndexed { index, qstCalendar ->
                assertThat(qstCalendar, `is`(cloneInputData[index]))
            }
        }
    }

    @Test
    fun updateTest() {
        runBlocking {
            dao.update(inputCalIdList[0], false)
            dao.update(inputCalIdList[1], true)
            dao.update(inputCalIdList[2], null)

            val list = dao.getAll()

            list[0].test_completion = false
            list[1].test_completion = true
            list[2].test_completion = null
        }
    }

    @Test
    fun getCntTest() {
        runBlocking {
            assertThat(dao.getCnt(), `is`(3))
            assertThat(dao.getCompletedDateCnt(), `is`(1))
            assertThat(dao.getCntHasTest(), `is`(2))
        }
    }

    @Test
    fun getMaxAndStartDateTest() {
        runBlocking {
            assertThat(dao.getMaxDate(), `is`(inputCalIdList[2]))
            assertThat(dao.getStartDate(), `is`(inputCalIdList[0]))
        }
    }

    @Test
    fun getTestCompletionTest() {
        runBlocking {
            for (index in 0..2) {
                assertThat(dao.getTestCompletionFromDate(inputCalIdList[index]), `is`(inputCalDataList[index].test_completion))
            }
        }
    }
}