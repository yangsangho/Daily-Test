package kr.yangbob.memorization.adapter

import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.ANIMATION_FULL_TIME
import kr.yangbob.memorization.ANIMATION_HALF_TIME
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ActivityTestViewpagerBinding
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.viewmodel.TestViewModel

class TestViewHolder(private val model: TestViewModel, private val binding: ActivityTestViewpagerBinding, private val adapter: TestPagerAdapter) : RecyclerView.ViewHolder(binding.root) {
    private val card = binding.card
    private val tvQstAnswer = binding.tvQstAnswer.apply {
        movementMethod = ScrollingMovementMethod()
    }
    private val correctChkIcon = binding.correctChkIcon
    private val qnaIcon = binding.qnaIcon
    private val stageIcon = binding.stageIcon

    private lateinit var qstRecord: QstRecord
    private lateinit var qst: Qst

    init {
        binding.holder = this
    }

    fun onBind(qstRecord: QstRecord) {
        this.qstRecord = qstRecord
        this.qst = model.getQstFromId(qstRecord.qst_id)

        binding.strData = qst.title
        binding.isFront = true
        binding.stage = qstRecord.challenge_stage
        tvQstAnswer.rotationY = 0f
        correctChkIcon.rotationY = 0f
        qnaIcon.rotationY = 0f
        stageIcon.rotationY = 0f
        card.rotationY = 0f
        if (qstRecord.is_correct != null) {
            binding.correct = qstRecord.is_correct
        } else {
            binding.correct = null
        }
    }

    // 애니메이션 적용
    fun clickShow(view: View) {
        card.cameraDistance = (10 * card.width).toFloat()
        if (binding.isFront!!) {
            tvQstAnswer.animate().setDuration(ANIMATION_HALF_TIME).alpha(1.0f)
                    .withEndAction {
                        binding.strData = qst.answer
                        tvQstAnswer.rotationY = -180f
                        correctChkIcon.rotationY = -180f
                        qnaIcon.rotationY = -180f
                        stageIcon.rotationY = -180f
                        binding.isFront = false
                    }
            card.animate().setDuration(ANIMATION_FULL_TIME).rotationY(-180f)
        } else {
            tvQstAnswer.animate().setDuration(ANIMATION_HALF_TIME).alpha(1.0f)
                    .withEndAction {
                        binding.strData = qst.title
                        tvQstAnswer.rotationY = 0f
                        correctChkIcon.rotationY = 0f
                        qnaIcon.rotationY = 0f
                        stageIcon.rotationY = 0f
                        binding.isFront = true
                    }
            card.animate().setDuration(ANIMATION_FULL_TIME).rotationY(0f)
        }
    }

    fun clickChk(view: View) {
        if (!model.checkIsPossibleClick()) return

        val isCorrect = view.id == R.id.btnSuccessLayout
        if (binding.correct == isCorrect) {
            binding.correct = null
        } else {
            binding.correct = isCorrect
        }

        val goMove = if (model.isDormant) model.updateDormant(qst, qstRecord, isCorrect)
        else model.update(qst, qstRecord, isCorrect)

        if (goMove) adapter.move(adapterPosition)
        Handler().postDelayed({
            model.resetIsPossibleClick()
        }, 600)
    }
}