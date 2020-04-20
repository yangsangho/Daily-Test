package kr.yangbob.memorization.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kr.yangbob.memorization.data.SimpleDate

@Dao
interface DaoQst {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qst: Qst)

    @Query("SELECT * FROM qst WHERE id == :id")
    suspend fun getFromId(id: Int): Qst

    @Query("SELECT * FROM Qst WHERE title == :title")
    suspend fun getFromTitle(title: String): Qst?

    @Delete
    suspend fun delete(qst: Qst)

    @Query("SELECT * FROM qst")
    suspend fun getAll(): List<Qst>

    @Query("SELECT * FROM qst")
    fun getAllLD(): LiveData<List<Qst>>

    @Query("SELECT * FROM qst WHERE is_dormant = 0 AND next_test_date <= :date")
    suspend fun getNeedTesList(date: SimpleDate): List<Qst>

    @Query("SELECT * FROM Qst WHERE is_dormant = 1")
    suspend fun getDormantList(): List<Qst>

    @Query("SELECT * FROM Qst WHERE is_dormant = 1")
    fun getDormantListLD(): LiveData<List<Qst>>
}