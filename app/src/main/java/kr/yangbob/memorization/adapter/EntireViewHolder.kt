package kr.yangbob.memorization.adapter

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.EXTRA_TO_QST_ID
import kr.yangbob.memorization.databinding.ActivityEntireLayoutListQstBinding
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.view.DetailActivity
import kr.yangbob.memorization.viewmodel.EntireViewModel

class EntireViewHolder(private val binding: ActivityEntireLayoutListQstBinding, private val model: EntireViewModel) : RecyclerView.ViewHolder(binding.root) {
    fun bind(qst: Qst) {
        binding.qst = qst
        binding.holder = this
        binding.tvEntireRegistration.text = qst.registration_date.getFormattedDate()
        binding.card.setOnClickListener {
            if (model.checkIsPossibleClick()) {
                val context = binding.root.context
                context.startActivity(Intent(context, DetailActivity::class.java).putExtra(EXTRA_TO_QST_ID, qst.id))
            }
        }
    }
}