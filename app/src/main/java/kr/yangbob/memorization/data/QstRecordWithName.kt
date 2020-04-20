package kr.yangbob.memorization.data

data class QstRecordWithName(
        val qst_name: String,
        val qst_id: Int,
        val calendar_id: SimpleDate,
        val challenge_stage: Int,
        var is_correct: Boolean? = null,
        val id: Long? = null
)