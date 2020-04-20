package kr.yangbob.memorization.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kr.yangbob.memorization.data.SimpleDate

@Entity(
        foreignKeys = [
            ForeignKey(entity = Qst::class,
                    parentColumns = ["id"],
                    childColumns = ["qst_id"]),
            ForeignKey(entity = QstCalendar::class,
                    parentColumns = ["id"],
                    childColumns = ["calendar_id"])
        ]
)
data class QstRecord(
        val qst_id: Int,
        val calendar_id: SimpleDate,              // date
        val challenge_stage: Int,
        var is_correct: Boolean? = null,           // null : 안 푼걸로 간주
        @PrimaryKey(autoGenerate = true)
        val id: Long? = null
)