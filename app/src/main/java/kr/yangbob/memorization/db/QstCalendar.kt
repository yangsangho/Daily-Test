package kr.yangbob.memorization.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QstCalendar(
    @PrimaryKey
    val id: Long,   // date
    var cnt_need_test: Int
)