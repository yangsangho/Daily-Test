package kr.yangbob.memorization.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DaoQst
{
    @Query("SELECT * FROM qst")
    fun getAllLD(): LiveData<List<Qst>>

    @Query("SELECT * FROM qst")
    suspend fun getAll(): List<Qst>

    @Query("SELECT COUNT(DISTINCT registration_date) FROM qst")
    suspend fun getRegistrationDateCnt(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qst: Qst)

    @Query("SELECT COUNT(*) FROM qst WHERE next_test_date <= :todayDate")
    suspend fun getNeedTestCnt(todayDate: Long): Int

//    @Delete
//    fun delete(qst: Qst)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface DaoQstCalendar
{
    @Query("SELECT * FROM QstCalendar WHERE id == :id")
    suspend fun getFromId(id: Long): QstCalendar?

    @Query("SELECT COUNT(*) FROM qstcalendar")
    suspend fun getCnt(): Int

    @Query("SELECT id FROM qstcalendar LIMIT 1")
    suspend fun getMinDate(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qstCalendar: QstCalendar)

    @Delete
    fun delete(qstCalendar: QstCalendar)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface DaoQstRecord
{
    @Query("SELECT * FROM QstRecord")
    suspend fun getAll(): List<QstRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(qstRecord: QstRecord)

    @Delete
    fun delete(qstRecord: QstRecord)
}