package kr.yangbob.memorization.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QnA(
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null,
        var question: String,   // 길이제한 확인
        var answer: String,     // 길이제한 확인
        @ColumnInfo(name="registration_date")
        var registrationDate: Long,
        @ColumnInfo(name = "next_test_date")
        var nextTestDate: Long,
        @ColumnInfo(name = "cur_stage")
        var curStage: Byte = 0 // converter이용해서 enum으로 변경
              )
