package kr.yangbob.memorization.adapter

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.EXTRA_TO_QST_ID
import kr.yangbob.memorization.databinding.ActivityResultLayoutListRecordBinding
import kr.yangbob.memorization.db.QstRecordWithName
import kr.yangbob.memorization.view.DetailActivity
import kr.yangbob.memorization.view.ResultActivity
import kr.yangbob.memorization.viewmodel.ResultViewModel

class ResultViewHolder(private val binding: ActivityResultLayoutListRecordBinding, private val model: ResultViewModel) :
        RecyclerView.ViewHolder(binding.root) {
    fun bind(record: QstRecordWithName) {
        binding.recordWithName = record
        binding.card.setOnClickListener {
            if (model.checkIsPossibleClick()) {
                val resultActivity = binding.root.context as ResultActivity
                resultActivity.startActivityForResult(
                        Intent(resultActivity, DetailActivity::class.java).putExtra(
                                EXTRA_TO_QST_ID,
                                record.qst_id
                        ), 123
                )
            }
        }
    }
}