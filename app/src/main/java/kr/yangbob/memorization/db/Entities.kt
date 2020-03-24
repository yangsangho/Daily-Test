package kr.yangbob.memorization.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["title"], unique = true)])
data class Qst(
    var title: String,   // 길이제한 확인
    var answer: String,     // 길이제한 확인
    val registration_date: MyDate,                     //date
    var next_test_date: MyDate,                         //date
    var cur_stage: Int = 0,
    var is_dormant: Boolean = false,
    var dormant_cnt: Int = 0,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
data class QstCalendar(
    @PrimaryKey
    val id: MyDate,
    var test_completion: Boolean? = null        // true : 테스트 완료, false : 테스트 안 함, null : 테스트 없음
)

data class InfoCalendar(
        val date: MyDate,
        var isCompleted: Boolean?,
        var isStartDay: Boolean = false        // true : start day , false : remain all
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
    val calendar_id: MyDate,              // date
    val challenge_stage: Int,
    var is_correct: Boolean? = null,           // null : 안 푼걸로 간주
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)

data class QstRecordWithName(
    val qst_name: String,
    val qst_id: Int,
    val calendar_id: MyDate,
    val challenge_stage: Int,
    var is_correct: Boolean? = null,
    val id: Long? = null
)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////