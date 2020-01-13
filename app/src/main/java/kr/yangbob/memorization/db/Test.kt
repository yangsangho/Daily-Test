package kr.yangbob.memorization.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.TypeConverters

@Entity(primaryKeys = ["question_id", "track_date"],
        foreignKeys = [
            ForeignKey(entity = Qna::class,
                       parentColumns = ["id"],
                       childColumns = ["question_id"],
                       onDelete = ForeignKey.CASCADE
                      ),
            ForeignKey(entity = Track::class,
                       parentColumns = ["date"],
                       childColumns = ["track_date"],
                       onDelete = ForeignKey.CASCADE
                      )
        ]
       )
data class Test(
        @ColumnInfo(name = "question_id")
        var questionId: Int,

        @ColumnInfo(name = "track_date")
        var trackDate: Long,

        var isCorrect: Boolean?,

        var stage: Stage
               )