package kr.yangbob.memorization.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(indices = [ Index(value = ["question"], unique = true) ])
data class Qna(
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null,

        var question: String,   // 길이제한 확인

        var answer: String,     // 길이제한 확인

        @ColumnInfo(name = "registration_date")
        var registrationDate: Calendar,

        @ColumnInfo(name = "next_test_date")
        var nextTestDate: Calendar,

        @ColumnInfo(name = "cur_stage")
        var curStage: Stage = Stage.BEGIN_ONE
              )
