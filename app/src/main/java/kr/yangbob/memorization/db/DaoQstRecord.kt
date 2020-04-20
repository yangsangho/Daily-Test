package kr.yangbob.memorization.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kr.yangbob.memorization.data.QstRecordWithName
import kr.yangbob.memorization.data.SimpleDate

@Dao
interface DaoQstRecord {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qstRecord: QstRecord)

    @Delete
    suspend fun delete(qstRecord: QstRecord)

    @Query("SELECT * FROM QstRecord WHERE qst_id = :qstId")
    suspend fun getAllFromId(qstId: Int): List<QstRecord>

    @Query("SELECT (SELECT title FROM Qst WHERE id = qst_id) AS qst_name, * FROM QstRecord WHERE calendar_id == :calendarId")
    suspend fun getAllWithName(calendarId: SimpleDate): List<QstRecordWithName>

    @Query("SELECT (SELECT title FROM Qst WHERE id = qst_id) AS qst_name, * FROM QstRecord WHERE calendar_id == :calendarId")
    fun getAllWithNameLD(calendarId: SimpleDate): LiveData<List<QstRecordWithName>>

    @Query("SELECT * FROM QstRecord WHERE calendar_id == :calendarId")
    suspend fun getAllFromDate(calendarId: SimpleDate): List<QstRecord>

    @Query("SELECT * FROM QstRecord WHERE calendar_id == :calendarId")
    fun getAllFromDateLD(calendarId: SimpleDate): LiveData<List<QstRecord>>

    @Query("SELECT * FROM QstRecord WHERE calendar_id == :calendarId AND is_correct IS NULL")
    suspend fun getNullListFromDate(calendarId: SimpleDate): List<QstRecord>

    @Query("SELECT COUNT(*) FROM QstRecord WHERE calendar_id == :calendarId")
    suspend fun getCnt(calendarId: SimpleDate): Int

    @Query("SELECT COUNT(*) FROM QstRecord WHERE calendar_id == :calendarId AND is_correct IS NULL")
    suspend fun getCntNotSolved(calendarId: SimpleDate): Int
}