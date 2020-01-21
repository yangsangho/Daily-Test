package kr.yangbob.memorization.db

import androidx.room.*

@Dao
interface DaoQstRecord
{
    @Query("SELECT * FROM QstRecord")
    suspend fun getAll(): List<QstRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(qstRecord: QstRecord)

    @Delete
    fun delete(qstRecord: QstRecord)
}