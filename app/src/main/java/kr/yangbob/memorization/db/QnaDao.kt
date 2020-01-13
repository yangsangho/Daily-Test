package kr.yangbob.memorization.db

import androidx.room.*

@Dao
interface QnaDao
{
    @Query("SELECT * FROM qna")
    fun getAll(): List<Qna>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(qna: Qna)

    @Delete
    fun delete(qna: Qna)
}