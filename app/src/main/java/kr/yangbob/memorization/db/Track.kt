package kr.yangbob.memorization.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity
data class Track(
        @PrimaryKey
        val date: Calendar,

        @ColumnInfo(name = "cnt_question")
        var cntQuestion: Int,

        @ColumnInfo(name = "cnt_solve")
        var cntSolve: Int
                )