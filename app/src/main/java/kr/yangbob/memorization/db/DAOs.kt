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

    @Query("SELECT * FROM qst WHERE is_dormant = 0 AND next_test_date <= :dateStr")
    suspend fun getNeedTesList(dateStr: String): List<Qst>

    @Query("SELECT * FROM Qst WHERE is_dormant = 1")
    suspend fun getAllDormant(): List<Qst>
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface DaoQstCalendar {

    @Query("SELECT * FROM QstCalendar")
    suspend fun getAll(): List<QstCalendar>

    @Query("SELECT * FROM QstCalendar WHERE id = :dateStr")
    suspend fun getFromId(dateStr: String): QstCalendar

    @Query("SELECT COUNT(*) FROM QstCalendar")
    suspend fun getCnt(): Int

    @Query("SELECT MIN(id) FROM QstCalendar")
    suspend fun getStartDateStr(): String

    @Query("SELECT MAX(id) FROM QstCalendar")
    suspend fun getMaxDate(): String?

    @Query("SELECT COUNT(*) FROM QstCalendar WHERE test_completion == 1")
    suspend fun getCompletedDateCnt(): Int

    @Query("SELECT COUNT(*) FROM QstCalendar WHERE test_completion IS NOT NULL")
    suspend fun getCntHasTest(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

//    @Query("DELETE FROM QstRecord WHERE is_correct IS NULL")
//    suspend fun deleteNoneSolved()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qstRecord: QstRecord)

//    @Delete
//    fun delete(qstRecord: QstRecord)
}