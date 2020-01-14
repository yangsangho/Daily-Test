package kr.yangbob.memorization.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(indices = [ Index(value = ["title"], unique = true) ])
data class Qst(
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null,

        var title: String,   // 길이제한 확인

        var answer: String,     // 길이제한 확인

        @ColumnInfo(name = "registration_date")
        var registrationDate: Calendar,

        @ColumnInfo(name = "next_test_date")
        var nextTestDate: Calendar,

        @ColumnInfo(name = "cur_stage")
        var curStage: Stage = Stage.INIT
              )
