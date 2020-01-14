package kr.yangbob.memorization.db

import androidx.room.*

@Dao
interface QstDao
{
    @Query("SELECT * FROM qst")
    suspend fun getAll(): List<Qst>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(qst: Qst)

    @Delete
    fun delete(qst: Qst)
}