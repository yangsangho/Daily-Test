package kr.yangbob.memorization.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DaoQst
{
    @Query("SELECT * FROM qst")
    fun getAllLD(): LiveData<List<Qst>>

//    @Query("SELECT * FROM qst WHERE id == :id")
//    fun getFromId(id: Int): Qst

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

    @Query("SELECT COUNT(*) FROM QstCalendar WHERE test_completion == 1")
    suspend fun getCompletedDateCnt(): Int

    @Insert
    suspend fun insert(qstCalendar: QstCalendar)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface DaoQstRecord
{
//    @Query("SELECT * FROM QstRecord")
//    suspend fun getAll(): List<QstRecord>

    @Query("SELECT * FROM QstRecord WHERE calendar_id == :dateStr")
    fun getLDListFromDate(dateStr: String): LiveData<List<QstRecord>>

    @Query("SELECT COUNT(*) FROM QstRecord WHERE calendar_id == :dateStr AND is_correct == 1")
    suspend fun getCorrectCntFromDate(dateStr: String): Int

    @Query("SELECT COUNT(*) FROM QstRecord WHERE calendar_id == :dateStr")
    suspend fun getCntFromDate(dateStr: String): Int

    @Query("DELETE FROM QstRecord WHERE is_correct IS NULL")
    suspend fun deleteNoneSolved()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qstRecord: QstRecord)

//    @Delete
//    fun delete(qstRecord: QstRecord)
}