package kr.yangbob.memorization.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DaoQst {
    @Query("SELECT * FROM qst")
    fun getAllLD(): LiveData<List<Qst>>

    @Query("SELECT * FROM qst WHERE id == :id")
    suspend fun getFromId(id: Int): Qst

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qst: Qst)

    @Query("SELECT COUNT(*) FROM qst WHERE next_test_date <= :dateStr")
    suspend fun getNeedTestCnt(dateStr: String): Int

    @Query("SELECT * FROM qst WHERE next_test_date <= :dateStr")
    suspend fun getNeedTesList(dateStr: String): List<Qst>
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface DaoQstCalendar {
    @Query("SELECT MIN(id) FROM QstCalendar")
    suspend fun getStartDateStr(): String

    @Query("SELECT * FROM QstCalendar")
    suspend fun getAll(): List<QstCalendar>

    @Query("SELECT * FROM QstCalendar WHERE id == :dateStr LIMIT 1")
    suspend fun getTodayRow(dateStr: String): QstCalendar?

    @Query("SELECT COUNT(*) FROM QstCalendar")
    suspend fun getCnt(): Int

    @Query("SELECT id FROM QstCalendar LIMIT 1")
    suspend fun getMinDate(): String?

    @Query("SELECT COUNT(*) FROM QstCalendar WHERE test_completion == 1")
    suspend fun getCompletedDateCnt(): Int

    @Query("UPDATE QstCalendar set test_completion = 1 WHERE id == :dateStr")
    suspend fun updateComplete(dateStr: String)

    @Insert
    suspend fun insert(qstCalendar: QstCalendar)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface DaoQstRecord {
//    @Query("SELECT * FROM QstRecord")
//    suspend fun getAll(): List<QstRecord>

    @Query("SELECT * FROM QstRecord WHERE qst_id = :id AND is_correct IS NOT NULL")
    suspend fun getAllFromId(id: Int): List<QstRecord>

    @Query("SELECT (SELECT title FROM Qst WHERE id = qst_id) AS qst_name, * FROM QstRecord WHERE calendar_id == :dateStr")
    fun getAllWithName(dateStr: String): LiveData<List<QstRecordWithName>>

    @Query("SELECT * FROM QstRecord WHERE calendar_id == :dateStr")
    fun getAllFromDate(dateStr: String): LiveData<List<QstRecord>>

    @Query("SELECT * FROM QstRecord WHERE calendar_id == :dateStr AND is_correct IS NULL")
    suspend fun getNullListFromDate(dateStr: String): List<QstRecord>

    @Query("DELETE FROM QstRecord WHERE is_correct IS NULL")
    suspend fun deleteNoneSolved()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qstRecord: QstRecord)

//    @Delete
//    fun delete(qstRecord: QstRecord)
}