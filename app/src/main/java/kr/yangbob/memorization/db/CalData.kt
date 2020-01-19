package kr.yangbob.memorization.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CalData(
        @PrimaryKey
        val id: Long,   // date

        @ColumnInfo(name = "cnt_question")
        var cntQuestion: Int,

        @ColumnInfo(name = "cnt_solve")
        var cntSolve: Int,

        @ColumnInfo(name = "cnt_correct")
        var cntCorrect: Int
                )