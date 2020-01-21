package kr.yangbob.memorization.db

import androidx.room.*

@Dao
interface DaoQstCalendar
{
    @Query("SELECT * FROM QstCalendar")
    fun getAll(): List<QstCalendar>

    @Query("SELECT COUNT(id) FROM qstcalendar")
    suspend fun getCalendarCnt(): Int

    @Query("SELECT COUNT(id) FROM qstcalendar WHERE cnt_need_test == (SELECT COUNT(*) FROM qstrecord WHERE calendar_id == id)")
    suspend fun getTestCompletionCnt(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(qstCalendar: QstCalendar)

    @Delete
    fun delete(qstCalendar: QstCalendar)
}