package kr.yangbob.memorization.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["title"], unique = true)])
data class Qst(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var title: String,   // 길이제한 확인
    var answer: String,     // 길이제한 확인
    var registration_date: Long,                     //date
    var next_test_date: Long,                         //date
    var cur_stage: Stage = Stage.INIT
)
