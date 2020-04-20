package kr.yangbob.memorization.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kr.yangbob.memorization.data.SimpleDate

@Entity
data class QstCalendar(
        @PrimaryKey
        val id: SimpleDate,
        var test_completion: Boolean? = null        // true : 테스트 완료, false : 테스트 안 함, null : 테스트 없음
)