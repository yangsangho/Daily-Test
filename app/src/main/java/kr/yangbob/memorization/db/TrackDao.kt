package kr.yangbob.memorization.db

import androidx.room.*

@Dao
interface TrackDao
{
    @Query("SELECT * FROM track")
    fun getAll(): List<Track>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(track: Track)

    @Delete
    fun delete(track: Track)
}