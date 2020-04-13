package kr.yangbob.memorization.adapter

import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.databinding.ActivityDetailLayoutListRecordBinding
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.viewmodel.QstViewModel

class DetailViewHolder(private val binding: ActivityDetailLayoutListRecordBinding, private val model: QstViewModel) :
        RecyclerView.ViewHolder(binding.root) {
    fun onBind(record: QstRecord) {
        binding.record = record
    }
}