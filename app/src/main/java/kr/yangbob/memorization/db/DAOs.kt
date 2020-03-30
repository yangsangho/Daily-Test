package kr.yangbob.memorization.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kr.yangbob.memorization.data.SimpleDate

@Dao
interface DaoQst {
    @Query("SELECT * FROM Qst WHERE title == :title")
    suspend fun getFromTitle(title: String): Qst?

    @Query("SELECT * FROM qst")
    fun getAllLD(): LiveData<List<Qst>>

    @Query("SELECT * FROM qst WHERE id == :id")
    suspend fun getFromId(id: Int): Qst

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qst: Qst)

    @Query("SELECT * FROM qst WHERE is_dormant = 0 AND next_test_date <= :date")
    suspend fun getNeedTesList(date: SimpleDate): List<Qst>

    @Query("SELECT * FROM Qst WHERE is_dormant = 1")
    fun getAllDormantLD(): LiveData<List<Qst>>

    @Query("SELECT * FROM Qst WHERE is_dormant = 1")
    suspend fun getAllDormant(): List<Qst>

    @Delete
    suspend fun delete(qst: Qst)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface DaoQstCalendar {

    @Query("SELECT test_completion FROM QstCalendar WHERE id == :calendarId")
    suspend fun getTestComplete(calendarId: SimpleDate): Boolean?

    @Query("SELECT * FROM QstCalendar")
    fun getAllLD(): LiveData<List<QstCalendar>>

    @Query("SELECT * FROM QstCalendar")
    suspend fun getAll(): List<QstCalendar>

    @Query("SELECT * FROM QstCalendar WHERE id = :calendarId")
    suspend fun getFromId(calendarId: SimpleDate): QstCalendar

    @Query("SELECT COUNT(*) FROM QstCalendar")
    suspend fun getCnt(): Int

    @Query("SELECT MIN(id) FROM QstCalendar")
    suspend fun getStartDate(): SimpleDate

    @Query("SELECT MAX(id) FROM QstCalendar")
    suspend fun getMaxDate(): SimpleDate?

    @Query("SELECT COUNT(*) FROM QstCalendar WHERE test_completion == 1")
    suspend fun getCompletedDateCnt(): Int

    @Query("SELECT COUNT(*) FROM QstCalendar WHERE test_completion IS NOT NULL")
    suspend fun getCntHasTest(): Int

    @Query("UPDATE QstCalendar SET test_completion = :isComplete WHERE id == :id")
    suspend fun update(id: SimpleDate, isComplete: Boolean?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qstCalendar: QstCalendar)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface DaoQstRecord {
    @Query("SELECT * FROM QstRecord")
    fun getAllLD(): LiveData<List<QstRecord>>

    @Query("SELECT * FROM QstRecord WHERE qst_id = :qstId")
    suspend fun getAllFromId(qstId: Int): List<QstRecord>

    @Query("SELECT (SELECT title FROM Qst WHERE id = qst_id) AS qst_name, * FROM QstRecord WHERE calendar_id == :calendarId")
    fun getAllWithName(calendarId: SimpleDate): LiveData<List<QstRecordWithName>>

    @Query("SELECT * FROM QstRecord WHERE calendar_id == :calendarId")
    fun getAllLDFromDate(calendarId: SimpleDate): LiveData<List<QstRecord>>

    @Query("SELECT * FROM QstRecord WHERE calendar_id == :calendarId")
    suspend fun getAllFromDate(calendarId: SimpleDate): List<QstRecord>

    @Query("SELECT * FROM QstRecord WHERE calendar_id == :calendarId AND is_correct IS NULL")
    suspend fun getNullListFromDate(calendarId: SimpleDate): List<QstRecord>

    @Query("SELECT COUNT(*) FROM QstRecord WHERE calendar_id == :calendarId")
    suspend fun getCnt(calendarId: SimpleDate): Int

    @Query("SELECT COUNT(*) FROM QstRecord WHERE calendar_id == :calendarId AND is_correct IS NULL")
    suspend fun getCntNotSolved(calendarId: SimpleDate): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qstRecord: QstRecord)

    @Delete
    suspend fun delete(qstRecord: QstRecord)
}