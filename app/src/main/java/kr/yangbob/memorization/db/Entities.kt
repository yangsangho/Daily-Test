package kr.yangbob.memorization.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["title"], unique = true)])
data class Qst(
    var title: String,   // 길이제한 확인
    var answer: String,     // 길이제한 확인
    val registration_date: String,                     //date
    var next_test_date: String,                         //date
    var cur_stage: Int = 0,
    var is_dormant: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
data class QstCalendar(
    @PrimaryKey
    val id: String,   // date
    var test_completion: Boolean? = null
)

data class InfoCalendar(
    val id: String,
    val yearMonth: Int,
    val date: Int,
    val isCompleted: Boolean?,
    val isStartOrToday: Boolean? = null    // true = start, false = today
)

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
    val calendar_id: String,              // date
    val challenge_stage: Int,
    var is_correct: Boolean? = null,           // null : 안 푼걸로 간주
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)

data class QstRecordWithName(
    val qst_name: String,
    val qst_id: Int,
    val calendar_id: String,
    val challenge_stage: Int,
    var is_correct: Boolean? = null,
    val id: Long? = null
)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////