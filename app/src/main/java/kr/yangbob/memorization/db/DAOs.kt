package kr.yangbob.memorization.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DaoQst
{
    @Query("SELECT * FROM qst")
    fun getAllLD(): LiveData<List<Qst>>

//    @Query("SELECT COUNT(DISTINCT registration_date) FROM qst")
//    suspend fun getRegistrationDateCnt(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qst: Qst)

    @Query("SELECT COUNT(*) FROM qst WHERE next_test_date <= :dateStr")
    suspend fun getNeedTestCnt(dateStr: String): Int

    @Query("SELECT * FROM qst WHERE next_test_date <= :dateStr")
    fun getNeedTestLD(dateStr: String): LiveData<List<Qst>>

//    @Delete
//    fun delete(qst: Qst)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface DaoQstCalendar
{
    @Query("SELECT * FROM qstcalendar WHERE id == :dateStr LIMIT 1")
    suspend fun getTodayRow(dateStr: String): QstCalendar?

    @Query("SELECT COUNT(*) FROM qstcalendar")
    suspend fun getCnt(): Int

    @Query("SELECT id FROM qstcalendar LIMIT 1")
    suspend fun getMinDate(): String?

    @Query("SELECT COUNT(*) FROM qstcalendar c WHERE cnt_need_test == (SELECT COUNT(*) FROM qstrecord WHERE calendar_id == c.id AND is_correct IS NOT NULL)")
    suspend fun getCompletedDateCnt(): Int

    @Insert
    suspend fun insert(qstCalendar: QstCalendar)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface DaoQstRecord
{
    @Query("SELECT * FROM QstRecord")
    suspend fun getAll(): List<QstRecord>

    @Query("SELECT COUNT(*) FROM qstrecord WHERE calendar_id == DATE('now')")
    suspend fun getTodayCnt(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(qstRecord: QstRecord)

    @Delete
    fun delete(qstRecord: QstRecord)
}