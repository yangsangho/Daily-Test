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
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class DaoQstTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var db: MemDatabase
    private lateinit var dao: DaoQst

    private val date1 = SimpleDate.newInstanceToday()
    private val date2 = date1.clone().apply {
        addDate(Calendar.DAY_OF_MONTH, 1)
    }
    private val date3 = date2.clone().apply {
        addDate(Calendar.DAY_OF_MONTH, 1)
    }
    private val inputQstList = listOf(
            Qst("d0", "d0", date1, date2, id = 0),
            Qst("d1", "d1", date1, date2, id = 1),
            Qst("d2", "d2", date1, date2, id = 2),
            Qst("d3", "d3", date1, date3, id = 3),
            Qst("d4", "d4", date1, date3, id = 4),
            Qst("d5", "d5", date1, date3, id = 5, is_dormant = true),
            Qst("d6", "d6", date1, date3, id = 6, is_dormant = true)
    )
    private val sizeNeedTest = 3
    private val sizeDormant = 2

    @Before
    fun before() {
        stopKoin()
        db = Room.inMemoryDatabaseBuilder(context, MemDatabase::class.java).build()
        dao = db.getDaoQst()

        runBlocking {
            inputQstList.forEach {
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
            for (index in inputQstList.indices) {
                val qst = dao.getFromId(index)
                assertThat(qst, `is`(inputQstList[index]))
            }
        }
    }

    @Test
    fun getFromTitleTest() {
        runBlocking {
            for (index in inputQstList.indices) {
                val qst = dao.getFromTitle("d$index")
                assertThat(qst, `is`(inputQstList[index]))
            }
        }
    }

    @Test
    fun getAllTest() {
        runBlocking {
            val list = dao.getAll()

            assertThat(list.size, `is`(inputQstList.size))

            list.forEachIndexed { index, qst ->
                assertThat(qst, `is`(inputQstList[index]))
            }

            assertThat(dao.getAllLD(), notNullValue())
        }
    }

    @Test
    fun deleteTest() {
        runBlocking {
            inputQstList.forEach {
                dao.delete(it)
            }

            assertThat(dao.getAll().size, `is`(0))
        }
    }

    @Test
    fun insertForUpdateTest() {
        val cloneInputData = List(inputQstList.size) { index ->
            inputQstList[index].copy()
        }

        runBlocking {
            cloneInputData.forEachIndexed { index, qst ->
                qst.title = "aaa$index"
                dao.insert(qst)
            }

            dao.getAll().forEachIndexed { index, qst ->
                assertThat(qst, `is`(cloneInputData[index]))
            }
        }
    }

    @Test
    fun getNeedTestListTest() {
        runBlocking {
            val list = dao.getNeedTesList(date2)

            assertThat(list.size, `is`(sizeNeedTest))

            list.forEachIndexed { index, qst ->
                assertThat(qst, `is`(inputQstList[index]))
            }
        }
    }

    @Test
    fun getDormantTest() {
        runBlocking {
            val list = dao.getDormantList()

            assertThat(list.size, `is`(sizeDormant))

            list.forEachIndexed { index, qst ->
                assertThat(qst, `is`(inputQstList[inputQstList.size - sizeDormant + index]))
            }
        }
    }
}