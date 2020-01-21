package kr.yangbob.memorization.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["qst_id", "calendar_id"],
    foreignKeys = [
        ForeignKey(
            entity = Qst::class,
            parentColumns = ["id"],
            childColumns = ["qst_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = QstCalendar::class,
            parentColumns = ["id"],
            childColumns = ["calendar_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class QstRecord(
    @ColumnInfo(index = true)
    val qst_id: Int,
    @ColumnInfo(index = true)
    val calendar_id: Long,              // date
    val is_correct: Boolean? = null,           // null : 초기화한 것으로 간주
    val challenge_stage: Stage? = null
)