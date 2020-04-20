package kr.yangbob.memorization.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kr.yangbob.memorization.data.SimpleDate

@Dao
interface DaoQstCalendar {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qstCalendar: QstCalendar)

    @Query("UPDATE QstCalendar SET test_completion = :isComplete WHERE id == :id")
    suspend fun update(id: SimpleDate, isComplete: Boolean?)

    @Query("SELECT * FROM QstCalendar")
    fun getAllLD(): LiveData<List<QstCalendar>>

    @Query("SELECT * FROM QstCalendar")
    suspend fun getAll(): List<QstCalendar>

    @Query("SELECT * FROM QstCalendar WHERE id = :calendarId")
    suspend fun getFromId(calendarId: SimpleDate): QstCalendar

    @Query("SELECT COUNT(*) FROM QstCalendar")
    suspend fun getCnt(): Int

    @Query("SELECT COUNT(*) FROM QstCalendar WHERE test_completion == 1")
    suspend fun getCompletedDateCnt(): Int

    @Query("SELECT COUNT(*) FROM QstCalendar WHERE test_completion IS NOT NULL")
    suspend fun getCntHasTest(): Int

    @Query("SELECT test_completion FROM QstCalendar WHERE id == :calendarId")
    suspend fun getTestCompletionFromDate(calendarId: SimpleDate): Boolean?

    @Query("SELECT MIN(id) FROM QstCalendar")
    suspend fun getStartDate(): SimpleDate

    @Query("SELECT MAX(id) FROM QstCalendar")
    suspend fun getMaxDate(): SimpleDate?
}
