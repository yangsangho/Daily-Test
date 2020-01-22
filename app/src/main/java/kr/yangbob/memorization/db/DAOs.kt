package kr.yangbob.memorization.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DaoQst
{
    @Query("SELECT * FROM qst")
    fun getAllLD(): LiveData<List<Qst>>

    @Query("SELECT * FROM qst WHERE id == :id")
    fun getFromId(id: Int): Qst

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qst: Qst)

    @Query("SELECT COUNT(*) FROM qst WHERE next_test_date <= :dateStr")
    suspend fun getNeedTestCnt(dateStr: String): Int

    @Query("SELECT * FROM qst WHERE next_test_date <= :dateStr")
    suspend fun getNeedTesList(dateStr: String): List<Qst>

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

    @Query("SELECT COUNT(*) FROM qstcalendar c WHERE cnt_need_test == (SELECT COUNT(*) FROM qstrecord WHERE calendar_id == c.id)")
    suspend fun getCompletedDateCnt(): Int

    @Query("SELECT id FROM QstCalendar WHERE is_update_chk == 0")       // false = 0, true = 1
    suspend fun getAllDateStrNonUpdateChk(): List<String>

    @Query("UPDATE QstCalendar SET is_update_chk = 1 WHERE id == :dateStr")
    suspend fun updateCheck(dateStr: String)

    @Insert
    suspend fun insert(qstCalendar: QstCalendar)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface DaoQstRecord
{
    @Query("SELECT * FROM QstRecord")
    suspend fun getAll(): List<QstRecord>

    @Query("SELECT * FROM qstrecord WHERE calendar_id == :dateStr")
    suspend fun getListFromDate(dateStr: String): List<QstRecord>

    @Query("SELECT COUNT(*) FROM QstRecord WHERE calendar_id == :dateStr AND is_correct == 1")
    suspend fun getCorrectCntFromDate(dateStr: String): Int

    @Query("SELECT COUNT(*) FROM qstrecord WHERE calendar_id == :dateStr")
    suspend fun getCntFromDate(dateStr: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(qstRecord: QstRecord)

    @Delete
    fun delete(qstRecord: QstRecord)
}