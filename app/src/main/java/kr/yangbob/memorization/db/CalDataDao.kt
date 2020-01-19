package kr.yangbob.memorization.db

import androidx.room.*

@Dao
interface CalDataDao
{
    @Query("SELECT * FROM caldata")
    fun getAll(): List<CalData>

    @Query("SELECT COUNT(id) FROM caldata")
    suspend fun getTestCnt(): Int

    @Query("SELECT COUNT(id) FROM caldata WHERE cnt_question == cnt_solve")
    suspend fun getTestCompletionCnt(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(calData: CalData)

    @Delete
    fun delete(calData: CalData)
}