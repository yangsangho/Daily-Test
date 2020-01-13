package kr.yangbob.memorization.db

import androidx.room.*

@Dao
interface TestDao
{
    @Query("SELECT * FROM test")
    fun getAll(): List<Test>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(test: Test)

    @Delete
    fun delete(test: Test)
}