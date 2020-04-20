package kr.yangbob.memorization.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kr.yangbob.memorization.data.SimpleDate

@Entity(indices = [Index(value = ["title"], unique = true)])
data class Qst(
        var title: String,   // 길이제한 확인
        var answer: String,     // 길이제한 확인
        val registration_date: SimpleDate,                     //date
        var next_test_date: SimpleDate,                         //date
        var cur_stage: Int = 0,
        var is_dormant: Boolean = false,
        var dormant_cnt: Int = 0,
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null
)