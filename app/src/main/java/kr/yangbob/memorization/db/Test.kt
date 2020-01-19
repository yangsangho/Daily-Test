package kr.yangbob.memorization.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["qst_id", "caldata_id"],
    foreignKeys = [
        ForeignKey(
            entity = Qst::class,
            parentColumns = ["id"],
            childColumns = ["qst_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CalData::class,
            parentColumns = ["id"],
            childColumns = ["caldata_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Test(
    @ColumnInfo(index = true)
    val qst_id: Int,

    @ColumnInfo(index = true)
    val caldata_id: Long,       // date

    val isCorrect: Boolean?,

    val stage: Stage
)

data class TestIncTitle(
    val title: String, val caldata_id: Long, val isCorrect: Boolean?, val stage: Stage
)