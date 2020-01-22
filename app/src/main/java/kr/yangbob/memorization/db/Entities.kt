package kr.yangbob.memorization.db

import androidx.room.*

@Entity(indices = [Index(value = ["title"], unique = true)])
data class Qst(
    var title: String,   // 길이제한 확인
    var answer: String,     // 길이제한 확인
    val registration_date: String,                     //date
    var next_test_date: String,                         //date
    var cur_stage: Int = 0,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
data class QstCalendar(
    @PrimaryKey
    val id: String,   // date
    val cnt_need_test: Int,
    var is_update_chk: Boolean = false
)

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    val calendar_id: String,              // date
    val is_correct: Boolean,           // null : 초기화한 것으로 간주
    val challenge_stage: Int
)