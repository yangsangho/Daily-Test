package kr.yangbob.memorization.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DaoQst
{
    @Query("SELECT * FROM qst")
    fun getAll(): LiveData<List<Qst>>

    @Query("SELECT COUNT(DISTINCT registration_date) FROM qst")
    suspend fun getRegistrationDateCnt(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qst: Qst)

    @Delete
    fun delete(qst: Qst)
}