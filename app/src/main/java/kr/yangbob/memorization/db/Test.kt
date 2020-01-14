package kr.yangbob.memorization.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.*

@Entity(
    primaryKeys = ["qst_id", "track_id"],
    foreignKeys = [
        ForeignKey(
            entity = Qst::class,
            parentColumns = ["id"],
            childColumns = ["qst_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Track::class,
            parentColumns = ["id"],
            childColumns = ["track_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Test(
    @ColumnInfo(index = true)
    val qst_id: Int,

    @ColumnInfo(index = true)
    val track_id: Calendar,

    val isCorrect: Boolean?,

    val stage: Stage
)

data class TestIncTitle(
    val title: String, val track_id: Calendar, val isCorrect: Boolean?, val stage: Stage
)